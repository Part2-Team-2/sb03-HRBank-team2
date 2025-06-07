package org.yebigun.hrbank.domain.employee.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
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
    public Long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
        return employeeRepository.countByCondition(status, fromDate, toDate);
    }
}
