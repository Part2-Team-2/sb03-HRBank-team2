package org.yebigun.hrbank.global.seeder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.department.repository.DepartmentRepository;
import org.yebigun.hrbank.domain.employee.entity.Employee;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;

@Profile("dev")
@Component
@DependsOn("departmentDataSeeder")
@RequiredArgsConstructor
public class EmployeeDataSeeder implements DataSeeder {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public void seed() {
        createDummyEmployees();
    }

    /**
     * 더미 직원 데이터를 만들기 위한 메소드입니다. 개수: 30 runtime에 자동으로 실행되는 구조입니다.
     */
    public void createDummyEmployees() {
        Map<String, Employee> existingEmployees = employeeRepository.findAll().stream()
            .collect(Collectors.toMap(Employee::getEmail, e -> e));

        List<Department> existsDepartment = departmentRepository.findAll();
        int departmentCount = existsDepartment.size();
        Random random = new Random();

        insertIfNotExists(existingEmployees, "김민수", "kimms@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00001", "사원", LocalDate.parse("2025-01-01"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "이서연", "leesy@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00002", "대리", LocalDate.parse("2025-01-05"), EmployeeStatus.ON_LEAVE);
        insertIfNotExists(existingEmployees, "박지훈", "parkjh@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00003", "과장", LocalDate.parse("2025-01-10"), EmployeeStatus.RESIGNED);
        insertIfNotExists(existingEmployees, "최윤아", "choiya@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00004", "팀장", LocalDate.parse("2025-01-12"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "정우성", "jungws@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00005", "부장", LocalDate.parse("2025-01-15"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "한지민", "hanjm@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00006", "사원", LocalDate.parse("2025-01-18"), EmployeeStatus.ON_LEAVE);
        insertIfNotExists(existingEmployees, "윤도현", "yoondh@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00007", "대리", LocalDate.parse("2025-01-20"), EmployeeStatus.RESIGNED);
        insertIfNotExists(existingEmployees, "서지훈", "seojh@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00008", "과장", LocalDate.parse("2025-01-22"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "백승훈", "baeksh@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00009", "팀장", LocalDate.parse("2025-01-25"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "오세훈", "ohsh@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00010", "부장", LocalDate.parse("2025-01-28"), EmployeeStatus.ON_LEAVE);
        insertIfNotExists(existingEmployees, "장하윤", "janghy@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00011", "사원", LocalDate.parse("2025-02-01"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "유재석", "yooys@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00012", "대리", LocalDate.parse("2025-02-03"), EmployeeStatus.RESIGNED);
        insertIfNotExists(existingEmployees, "강호동", "kanghd@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00013", "과장", LocalDate.parse("2025-02-06"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "서강준", "seogj@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00014", "팀장", LocalDate.parse("2025-02-08"), EmployeeStatus.ON_LEAVE);
        insertIfNotExists(existingEmployees, "신세경", "shinsk@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00015", "부장", LocalDate.parse("2025-02-11"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "박보영", "parkby@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00016", "사원", LocalDate.parse("2025-02-13"), EmployeeStatus.RESIGNED);
        insertIfNotExists(existingEmployees, "송중기", "songjg@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00017", "대리", LocalDate.parse("2025-02-16"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "문채원", "mooncw@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00018", "과장", LocalDate.parse("2025-02-18"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "공유", "gongy@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00019", "팀장", LocalDate.parse("2025-02-21"), EmployeeStatus.ON_LEAVE);
        insertIfNotExists(existingEmployees, "김태희", "kimth@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00020", "부장", LocalDate.parse("2025-02-24"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "이동욱", "leedw@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00021", "사원", LocalDate.parse("2025-02-27"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "전지현", "jeonjh@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00022", "대리", LocalDate.parse("2025-03-01"), EmployeeStatus.RESIGNED);
        insertIfNotExists(existingEmployees, "이준기", "leejg@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00023", "과장", LocalDate.parse("2025-03-04"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "수지", "suzy@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00024", "팀장", LocalDate.parse("2025-03-07"), EmployeeStatus.ON_LEAVE);
        insertIfNotExists(existingEmployees, "임영웅", "limyw@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00025", "부장", LocalDate.parse("2025-03-09"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "강다니엘", "kangdn@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00026", "사원", LocalDate.parse("2025-03-11"), EmployeeStatus.RESIGNED);
        insertIfNotExists(existingEmployees, "차은우", "chaeunw@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00027", "대리", LocalDate.parse("2025-03-13"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "아이유", "iu@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00028", "과장", LocalDate.parse("2025-03-15"), EmployeeStatus.ACTIVE);
        insertIfNotExists(existingEmployees, "서현진", "seohy@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00029", "팀장", LocalDate.parse("2025-03-17"), EmployeeStatus.ON_LEAVE);
        insertIfNotExists(existingEmployees, "유인나", "yooina@test.com", existsDepartment.get(random.nextInt(departmentCount)),
            "EMP-2025-00030", "부장", LocalDate.parse("2025-03-20"), EmployeeStatus.ACTIVE);
    }

    private void insertIfNotExists(Map<String, Employee> existingMap, String name, String email,
        Department department,
        String employeeNumber, String position, LocalDate hireDate, EmployeeStatus status) {
        if (!existingMap.containsKey(name)) {
            Employee employee = Employee.builder()
                .name(name)
                .email(email)
                .department(department)
                .employeeNumber(employeeNumber)
                .position(position)
                .hireDate(hireDate)
                .status(status)
                .memo(null)
                .profile(null)
                .build();

            employeeRepository.save(employee);
        }
    }
}
