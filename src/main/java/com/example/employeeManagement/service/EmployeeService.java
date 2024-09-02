package com.example.employeeManagement.service;

import com.example.employeeManagement.controller.AuthenticationController;
import com.example.employeeManagement.dto.EmployeeRequest;
import com.example.employeeManagement.model.*;
import com.example.employeeManagement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);


    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Page<Employee> getEmployees(Specification<Employee> spec, Pageable pageable) {
        return employeeRepository.findAll(spec, pageable);
    }

    public Employee createOrUpdateEmployee(EmployeeRequest employeeRequest) {
        Employee employee;
        boolean isNew = employeeRequest.getId() == null;

        if (!isNew) { // If Editing
            Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employeeRequest.getId());
            if (existingEmployeeOpt.isPresent()) {
                employee = existingEmployeeOpt.get();

                // Fetch the current user's ID and role
                Long currentUserId = authenticationService.getCurrentUserId();
                boolean isCurrentUserStaff = authenticationService.isStaff();

                // Fetch the current employee's ID using the user's ID
                Long currentEmployeeId = getCurrentEmployeeId(currentUserId)
                        .orElseThrow(() -> new IllegalStateException("No employee record found for the current user"));

                // Log details for debugging and security checks
                logger.info("Attempting to update employee. Current user ID: {}, Is staff: {}, Employee ID: {}, Request ID: {}, Current employee ID: {}",
                        currentUserId, isCurrentUserStaff, employee.getId(), employeeRequest.getId(), currentEmployeeId);

                // Check if the user is a staff member and is trying to update a different employee
                if (isCurrentUserStaff && !employee.getId().equals(currentEmployeeId)){
                    throw new AccessDeniedException("You do not have permission to update this employee record.");
                }
            } else {
                throw new RuntimeException("Employee not found with ID: " + employeeRequest.getId());
            }
        } else { // If Creating
            employee = new Employee();
        }

        // Update only the fields provided in the request
        if (employeeRequest.getFirstName() != null) {
            employee.setFirstName(employeeRequest.getFirstName());
        }
        if (employeeRequest.getLastName() != null) {
            employee.setLastName(employeeRequest.getLastName());
        }
        if (employeeRequest.getPlace() != null) {
            employee.setPlace(employeeRequest.getPlace());
        }
        if (employeeRequest.getEmail() != null) {
            employee.setEmail(employeeRequest.getEmail());
        }
        if (employeeRequest.getPhone() != null) {
            employee.setPhone(employeeRequest.getPhone());
        }
        if (employeeRequest.getCountry_id() != null) {
            employee.setCountry(fetchCountry(employeeRequest.getCountry_id()));
        }
        if (employeeRequest.getState_id() != null) {
            employee.setState(fetchState(employeeRequest.getState_id()));
        }
        if (employeeRequest.getCity_id() != null) {
            employee.setCity(fetchCity(employeeRequest.getCity_id()));
        }
        if (employeeRequest.getRole_id() != null) {
            employee.setRole(fetchRole(employeeRequest.getRole_id()));
        }
        if (employeeRequest.getDepartment_id() != null) {
            employee.setDepartment(fetchDepartment(employeeRequest.getDepartment_id()));
        }
        if (employeeRequest.getDesignation_id() != null) {
            employee.setDesignation(fetchDesignation(employeeRequest.getDesignation_id()));
        }
        if (employeeRequest.getGrade_id() != null) {
            employee.setGrade(fetchGrade(employeeRequest.getGrade_id()));
        }
        if (employeeRequest.getLanguage_ids() != null) {
            List<Language> languages = fetchLanguages(employeeRequest.getLanguage_ids());
            employee.setLanguages(languages);
        }

        // Save employee first
        Employee savedEmployee = employeeRepository.save(employee);

        // If it's a new employee, create a user
        if (isNew) {
            // Create a new user with the employee's email
            User newUser = new User();
            newUser.setUsername(employee.getEmail());
            newUser.setPassword(passwordEncoder.encode(employeeRequest.getPassword())); // Hash the password

            // Fetch the STAFF role
            Role staffRole = roleRepository.findByName("ROLE_STAFF")
                    .orElseThrow(() -> new RuntimeException("ROLE_STAFF role not found"));

            // Save the user
            newUser.setRoles(new HashSet<>());
            newUser.getRoles().add(staffRole);
            userRepository.save(newUser);
        }

        return savedEmployee;
    }


    public Optional<Long> getCurrentEmployeeId(Long currentUserId) {
        return employeeRepository.findEmployeeIdByUserId(currentUserId);
    }

    private Country fetchCountry(Long id) {
        return countryRepository.findById(id).orElseThrow(() -> new RuntimeException("Country not found with ID: " + id));
    }

    private State fetchState(Long id) {
        return stateRepository.findById(id).orElseThrow(() -> new RuntimeException("State not found with ID: " + id));
    }

    private City fetchCity(Long id) {
        return cityRepository.findById(id).orElseThrow(() -> new RuntimeException("City not found with ID: " + id));
    }

    private Role fetchRole(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));
    }

    private Department fetchDepartment(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Department not found with ID: " + id));
    }

    private Designation fetchDesignation(Long id) {
        return designationRepository.findById(id).orElseThrow(() -> new RuntimeException("Designation not found with ID: " + id));
    }

    private Grade fetchGrade(Long id) {
        return gradeRepository.findById(id).orElseThrow(() -> new RuntimeException("Grade not found with ID: " + id));
    }

    private List<Language> fetchLanguages(List<Long> ids) {
        return languageRepository.findAllById(ids);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
