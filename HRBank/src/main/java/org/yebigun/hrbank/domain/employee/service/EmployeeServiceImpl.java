package org.yebigun.hrbank.domain.employee.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.department.repository.DepartmentRepository;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeCreateRequest;
import org.yebigun.hrbank.domain.employee.entity.Employee;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.mapper.EmployeeMapper;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public EmployeeDto createEmployee(EmployeeCreateRequest request, MultipartFile profile) {

        if (request.getName() == null || request.getName().trim().isEmpty())
            throw new IllegalArgumentException("이름은 필수입니다.");

        if (request.getEmail() == null || request.getEmail().trim().isEmpty())
            throw new IllegalArgumentException("이메일은 필수입니다.");

        if (request.getDepartmentId() == null)
            throw new IllegalArgumentException("부서는 필수입니다.");

        if (request.getPosition() == null || request.getPosition().trim().isEmpty())
            throw new IllegalArgumentException("직급은 필수입니다.");

        if (request.getHireDate() == null)
            throw new IllegalArgumentException("입사일은 필수입니다.");

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 부서입니다."));

        String generatedEmpNo = generateUniqueEmployeeNumber();

        Employee employee = Employee.builder()
            .name(request.getName())
            .email(request.getEmail())
            .department(department)
            .employeeNumber(generatedEmpNo)
            .position(request.getPosition())
            .hireDate(request.getHireDate())
            .memo(request.getMemo())
            .status(EmployeeStatus.ACTIVE)
            .profile(null) // 실제 프로필 이미지는 별도 처리 필요
            .build();

        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toDto(saved);
    }

    // 추가된 누락 메서드
    @Override
    @Transactional(readOnly = true)
    public long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
        return employeeRepository.countByCondition(status, fromDate, toDate);
    }

    private String generateUniqueEmployeeNumber() {
        String year = String.valueOf(java.time.Year.now().getValue());
        String prefix = "EMP-" + year + "-";
        int maxSeq = employeeRepository.findAll().stream()
            .filter(e -> e.getEmployeeNumber() != null && e.getEmployeeNumber().startsWith(prefix))
            .map(e -> {
                try {
                    return Integer.parseInt(e.getEmployeeNumber().substring(prefix.length()));
                } catch (Exception ex) {
                    return 0;
                }
            })
            .max(Integer::compareTo)
            .orElse(0);
        int nextSeq = maxSeq + 1;
        return String.format("%s%03d", prefix, nextSeq);
    }

}