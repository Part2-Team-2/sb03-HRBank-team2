package org.yebigun.hrbank.domain.employee.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.department.repository.DepartmentRepository;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeTrendDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeCreateRequest;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeListRequest;
import org.yebigun.hrbank.domain.employee.entity.Employee;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.mapper.EmployeeMapper;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;
import org.yebigun.hrbank.global.dto.CursorPageResponse;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private static final Set<String> VALID_UNIT = Set.of("day", "week", "month", "quarter", "year");
    private static final Set<String> VALID_GROUP_BY = Set.of("department", "position");
    private static final Set<String> VALID_SORT_DIRECTION = Set.of("asc", "desc");
    private static final Long UNIT = 12L;

    @Override
    public List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to, String unit) {

        if (!VALID_UNIT.contains(unit)) {
            throw new IllegalArgumentException("지원하지 않는 시간 단위입니다.");
        }

        from = from != null ? from : getDate(unit);
        to = to != null ? to : LocalDate.now();

        List<EmployeeTrendDto> employeeTrends = employeeRepository.findEmployeeTrend(from, to, unit);

        return employeeTrends;
    }

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
        if (toDate == null) {
            toDate = LocalDate.now();
        }

        return employeeRepository.countByCondition(status, fromDate, toDate);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<EmployeeDto> findEmployees(EmployeeListRequest request) {
        String nameOrEmail = request.nameOrEmail();
        String employeeNumber = request.employeeNumber();
        String departmentName = request.departmentName();
        String position = request.position();
        LocalDate hireDateFrom = request.hireDateFrom();
        LocalDate hireDateTo = request.hireDateTo();
        EmployeeStatus status = request.status();
        String cursor = request.cursor();
        int size = request.size();
        String sortField = request.sortField();
        String sortDirection = request.sortDirection();

        if (!VALID_SORT_DIRECTION.contains(sortDirection)) {
            throw new IllegalArgumentException("잘못된 정렬 방향입니다.");
        }

        List<Employee> employees = employeeRepository.findAllByRequest(
            nameOrEmail, employeeNumber, departmentName, position, hireDateFrom, hireDateTo, status,
            cursor,
            size, sortField, sortDirection
        );

        // cursor
        boolean hasNext = employees.size() > size;

        String nextCursor;
        Long nextIdAfter;

        if (hasNext) {
            // 마지막 요소는 다음 페이지가 존재하는지 여부만 확인하기 위한 데이터
            employees.remove(employees.size() - 1);
            nextCursor = getNextCursor(sortField, employees.get(employees.size() - 1));
            nextIdAfter = employees.get(employees.size() - 1).getId();
        } else {
            nextCursor = null;
            nextIdAfter = null;
        }
        
        List<EmployeeDto> employeeDtos = employees.stream()
            .map(employeeMapper::toDto)
            .toList();

        long totalElements = employeeRepository.countByRequest(nameOrEmail, employeeNumber, departmentName,
            position, hireDateFrom, hireDateTo, status);

        CursorPageResponse<EmployeeDto> response = new CursorPageResponse<>(employeeDtos, nextIdAfter,
            nextCursor, size, totalElements, hasNext);

        return response;
    }

    private String getNextCursor(String sortField, Employee employee) {
        return switch (sortField) {
            case "name" -> employee.getName();
            case "employeeNumber" -> employee.getEmployeeNumber();
            case "hireDate" -> employee.getHireDate().toString();
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 필드입니다.");
        };
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

    private LocalDate getDate(String unit) {
        LocalDate fromDate = LocalDate.now();

        fromDate = switch (unit) {
            case "day" -> fromDate.minusDays(UNIT);
            case "week" -> fromDate.minusWeeks(UNIT);
            case "month" -> fromDate.minusMonths(UNIT);
            case "quarter" -> fromDate.minusMonths(UNIT * 3);
            case "year" -> fromDate.minusYears(UNIT);
            default -> fromDate;
        };

        return fromDate;
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("직원을 찾을 수 없습니다."));
        return employeeMapper.toDto(employee);
    }
}
