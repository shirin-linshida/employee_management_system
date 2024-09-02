package com.example.employeeManagement.config;

import com.example.employeeManagement.model.*;
import com.example.employeeManagement.repository.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner firstTimeRun() {
        return args -> {
            if (userRepository.count() > 0) {
                System.out.println("Data initialization skipped: User table is not empty.");
                return;
            }
            initializeRoles();
            initializeAdminUser();
            loadCountries();
            loadStates();
            loadCities();
            loadDepartments();
            loadDesignations();
            loadGrades();
            loadLanguages();
            loadEmployees();
        };
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);

            Role staffRole = new Role();
            staffRole.setName("ROLE_STAFF");
            roleRepository.save(staffRole);
        }
    }

    private void initializeAdminUser() {
        if (userRepository.findByUsername("admin@kiebot.com").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin@kiebot.com");
            admin.setPassword(passwordEncoder.encode("adminpassword"));

            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
        }
    }

    private void loadStates() throws IOException, CsvValidationException {
        Resource resource = new ClassPathResource("data/states.csv");
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            String[] nextLine;
            boolean isFirstLine = true; // Skip the header line if present
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header line
                }

                // Debugging: Print the line being processed
                System.out.println("Processing line: " + String.join(", ", nextLine));

                String stateName = nextLine[1]; // Assuming the state name is in the second column
                Long countryId;

                try {
                    countryId = Long.parseLong(nextLine[2]); // Assuming country_id is in the third column
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid country_id format in CSV: " + nextLine[2], e);
                }

                // Check if country exists
                Country country = countryRepository.findById(countryId)
                        .orElseThrow(() -> new IllegalArgumentException("Country ID not found: " + countryId));

                // Check if the state already exists to avoid duplicates
                if (stateRepository.findByNameAndCountry(stateName, country).isEmpty()) {
                    State state = new State();
                    state.setName(stateName);
                    state.setCountry(country);
                    stateRepository.save(state);
                } else {
                    System.out.println("State already exists: " + stateName + " in country " + country.getName());
                }
            }
        }
    }

    private void loadCities() throws IOException, CsvValidationException {
        Resource resource = new ClassPathResource("data/cities.csv");
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            String[] nextLine;
            boolean isFirstLine = true; // Skip the header line
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Debugging: Print the line being processed
                System.out.println("Processing line: " + String.join(", ", nextLine));

                String cityName = nextLine[1]; // Correct index for name column
                Long stateId = Long.parseLong(nextLine[2]); // Correct index for state_id column

                State state = stateRepository.findById(stateId)
                        .orElseThrow(() -> new IllegalArgumentException("State ID not found: " + stateId));

                // Check if the city already exists to avoid duplicates
                if (cityRepository.findByNameAndState(cityName, state).isEmpty()) {
                    City city = new City();
                    city.setName(cityName);
                    city.setState(state);
                    cityRepository.save(city);
                } else {
                    System.out.println("City already exists: " + cityName + " in state " + state.getName());
                }
            }
        }
    }


    private void loadDepartments() throws IOException, CsvValidationException {
        Resource resource = new ClassPathResource("data/departments.csv");
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                Department department = new Department();
                department.setName(nextLine[0]);
                departmentRepository.save(department);
            }
        }
    }

    private void loadDesignations() throws IOException, CsvValidationException {
        Resource resource = new ClassPathResource("data/designations.csv");
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            String[] nextLine;
            boolean isFirstLine = true; // Skip the header line
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Debugging: Print the line being processed
                System.out.println("Processing line: " + String.join(", ", nextLine));

                Designation designation = new Designation();
                designation.setName(nextLine[1]); // Name is at index 1
                Department department = departmentRepository.findById(Long.parseLong(nextLine[2])).orElseThrow(); // department_id is at index 2
                designation.setDepartment(department);
                designationRepository.save(designation);
            }
        }
    }


    private void loadGrades() throws IOException, CsvValidationException {
        Resource resource = new ClassPathResource("data/grades.csv");
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                Grade grade = new Grade();
                grade.setName(nextLine[0]);
                gradeRepository.save(grade);
            }
        }
    }

    private void loadLanguages() throws IOException, CsvValidationException {
        Resource resource = new ClassPathResource("data/languages.csv");
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                Language language = new Language();
                language.setName(nextLine[0]);
                languageRepository.save(language);
            }
        }
    }



    private void loadCountries() throws IOException, CsvValidationException {
        Resource resource = new ClassPathResource("data/countries.csv");
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            String[] nextLine;
            boolean isFirstLine = true; // Skip the header line
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                Country country = new Country();
                country.setId(Long.parseLong(nextLine[0])); // Assuming you want to use the ID from the CSV
                country.setName(nextLine[1]);
                countryRepository.save(country);
            }
        }
    }


    private void loadEmployees() throws IOException, CsvValidationException {
        Resource resource = new ClassPathResource("data/employees.csv");
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            String[] nextLine;
            boolean isFirstLine = true; // Skip the header line
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                Employee employee = new Employee();
                employee.setEmail(nextLine[1]); // email is at index 1
                employee.setFirstName(nextLine[2]); // first_name is at index 2
                employee.setLastName(nextLine[3]); // last_name is at index 3
                employee.setPhone(nextLine[4]); // phone is at index 4
                employee.setPlace(nextLine[5]); // place is at index 5

                try {
                    City city = cityRepository.findById(Long.parseLong(nextLine[6])).orElse(null);
                    if (city != null) {
                        employee.setCity(city);
                    } else {
                        System.out.println("City not found for ID: " + nextLine[6]);
                    }

                    Country country = countryRepository.findById(Long.parseLong(nextLine[7])).orElse(null);
                    if (country != null) {
                        employee.setCountry(country);
                    } else {
                        System.out.println("Country not found for ID: " + nextLine[7]);
                    }

                    Department department = departmentRepository.findById(Long.parseLong(nextLine[8])).orElse(null);
                    if (department != null) {
                        employee.setDepartment(department);
                    } else {
                        System.out.println("Department not found for ID: " + nextLine[8]);
                    }

                    Designation designation = designationRepository.findById(Long.parseLong(nextLine[9])).orElse(null);
                    if (designation != null) {
                        employee.setDesignation(designation);
                    } else {
                        System.out.println("Designation not found for ID: " + nextLine[9]);
                    }

                    Grade grade = gradeRepository.findById(Long.parseLong(nextLine[10])).orElse(null);
                    if (grade != null) {
                        employee.setGrade(grade);
                    } else {
                        System.out.println("Grade not found for ID: " + nextLine[10]);
                    }

                    Role role = roleRepository.findById(Long.parseLong(nextLine[11])).orElse(null);
                    if (role != null) {
                        employee.setRole(role);
                    } else {
                        System.out.println("Role not found for ID: " + nextLine[11]);
                    }

                    State state = stateRepository.findById(Long.parseLong(nextLine[12])).orElse(null);
                    if (state != null) {
                        employee.setState(state);
                    } else {
                        System.out.println("State not found for ID: " + nextLine[12]);
                    }

                    employeeRepository.save(employee);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID format: " + e.getMessage());
                }
            }
        }
    }


}
