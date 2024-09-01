package com.example.employeeManagement.service;

import com.example.employeeManagement.dto.EmployeeRequest;
import com.example.employeeManagement.model.*;
import com.example.employeeManagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Employee employee = new Employee();
        boolean isNew = employeeRequest.getId() == null;

        if (!isNew) { //If Editing
            Optional<Employee> existingEmployee = employeeRepository.findById(employeeRequest.getId());
            if (existingEmployee.isPresent()) {
                employee = existingEmployee.get();

                // Fetch the current user's ID and role
                Long currentUserId = authenticationService.getCurrentUserId();
                boolean isCurrentUserStaff = authenticationService.isStaff();

                // Check if the user is a staff member and is trying to update a different employee
                if ((currentUserId != employeeRequest.getId()) && isCurrentUserStaff && !employee.getId().equals(currentUserId)) {
                    throw new AccessDeniedException("You do not have permission to update this employee record.");
                }
            } else {
                throw new RuntimeException("Employee not found with ID: " + employeeRequest.getId());
            }
        }

        // Set employee details
        employee.setFirstName(employeeRequest.getFirstName());
        employee.setLastName(employeeRequest.getLastName());
        employee.setPlace(employeeRequest.getPlace());
        employee.setEmail(employeeRequest.getEmail());
        employee.setPhone(employeeRequest.getPhone());

        // Fetch and set related entities
        employee.setCountry(fetchCountry(employeeRequest.getCountry_id()));
        employee.setState(fetchState(employeeRequest.getState_id()));
        employee.setCity(fetchCity(employeeRequest.getCity_id()));
        employee.setRole(fetchRole(employeeRequest.getRole_id()));
        employee.setDepartment(fetchDepartment(employeeRequest.getDepartment_id()));
        employee.setDesignation(fetchDesignation(employeeRequest.getDesignation_id()));
        employee.setGrade(fetchGrade(employeeRequest.getGrade_id()));

        // Fetch and set languages
        List<Language> languages = fetchLanguages(employeeRequest.getLanguage_ids());
        employee.setLanguages(languages);

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
            User createdUser = userRepository.save(newUser);

            // Initialize roles if needed
            createdUser.setRoles(new HashSet<>()); // Assuming roles are stored in a set
            createdUser.getRoles().add(staffRole);

            // Save user with role
            userRepository.save(createdUser);
        }

        return savedEmployee;
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

    public void logCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(", "));
            System.out.println("Current user roles: " + roles);
        } else {
            System.out.println("No authentication details found");
        }
    }

    public void ensureAdminAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication............ " + authentication);
        if (authentication != null && authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Access denied: Only admins can create employees.");
        }
    }
}
