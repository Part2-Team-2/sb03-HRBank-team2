package org.yebigun.hrbank.domain.employee.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeTrendDto;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private static final Set<String> VALID_UNIT = Set.of("day", "week", "month", "quarter", "year");
    private static final Set<String> VALID_GROUP_BY = Set.of("department", "position");
    private static final Long UNIT = 12L;

    @Override
    public List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to, String unit) {

        if (!VALID_UNIT.contains(unit)) {
            throw new IllegalArgumentException("지원하지 않는 시간 단위입니다.");
        }

        LocalDate today = LocalDate.now();

        if (from == null) {
            from = getDate(unit);
        }

        if (to == null) {
            to = today;
        }

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
    public Long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
        if (toDate == null) {
            toDate = LocalDate.now();
        }

        return employeeRepository.countByCondition(status, fromDate, toDate);
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
}
