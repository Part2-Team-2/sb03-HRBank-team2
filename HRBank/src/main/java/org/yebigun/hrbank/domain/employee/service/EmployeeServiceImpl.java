package org.yebigun.hrbank.domain.employee.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.department.repository.DepartmentRepository;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeCreateRequest;
import org.yebigun.hrbank.domain.employee.entity.Employee;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.mapper.EmployeeMapper;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private static final Set<String> VALID_GROUP_BY = Set.of("department", "position");

    @Override
    public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy,
                                                                 EmployeeStatus status) {

        if (!VALID_GROUP_BY.contains(groupBy)) {
            throw new IllegalArgumentException("지원하지 않는 그룹화 기준입니다.");
        }

        List<EmployeeDistributionDto> employees = employeeRepository.findEmployeeByStatusGroupByDepartmentOrPosition(
            groupBy, status);

        return employees;
    }

    @Override
    @Transactional
    public EmployeeDto createEmployee(EmployeeCreateRequest request, MultipartFile profile) {

        // record 방식: getXxx() → xxx()
        if (request.name() == null || request.name().trim().isEmpty())
            throw new IllegalArgumentException("이름은 필수입니다.");

        if (request.email() == null || request.email().trim().isEmpty())
            throw new IllegalArgumentException("이메일은 필수입니다.");

        if (request.departmentId() == null)
            throw new IllegalArgumentException("부서는 필수입니다.");

        if (request.position() == null || request.position().trim().isEmpty())
            throw new IllegalArgumentException("직급은 필수입니다.");

        if (request.hireDate() == null)
            throw new IllegalArgumentException("입사일은 필수입니다.");

        if (!request.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }

        if (employeeRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        Department department = departmentRepository.findById(request.departmentId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부서입니다."));

        String generatedEmpNo = generateUniqueEmployeeNumber();

        Employee employee = Employee.builder()
            .name(request.name())
            .email(request.email())
            .department(department)
            .employeeNumber(generatedEmpNo)
            .position(request.position())
            .hireDate(request.hireDate())
            .memo(request.memo())
            .status(EmployeeStatus.ACTIVE)
            .profile(null) // 실제 프로필 이미지는 별도 처리 필요
            .build();

        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
        return employeeRepository.countByCondition(status, fromDate, toDate);
    }

    private String generateUniqueEmployeeNumber() {
        String year   = String.valueOf(Year.now().getValue());
        String prefix = "EMP-" + year + "-";
        String empNo;

        do {
            int rand = ThreadLocalRandom.current().nextInt(0, 100_000_000);
            empNo = prefix + String.format("%08d", rand);
        } while (employeeRepository.existsByEmployeeNumber(empNo));

        return empNo;
    }

}