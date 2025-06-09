package org.yebigun.hrbank.domain.department.service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.department.dto.data.DepartmentDto;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentCreateRequest;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentUpdateRequest;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.department.mapper.DepartmentMapper;
import org.yebigun.hrbank.domain.department.repository.DepartmentRepository;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;
import org.yebigun.hrbank.global.dto.CursorPageResponse;

@Log4j2
@RequiredArgsConstructor
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final EmployeeRepository employeeRepository;

//    /**
//     * 주석 처리된 부분은 더미 부서 데이터를 만들기 위한 메소드입니다.
//     * 개수: 20
//     * 주석 해제를 할 경우, runtime에 자동으로 실행되는 구조입니다.
//     */
//
//    @PostConstruct
//    public void initDummyData() {
//        createDummyDepartments();
//    }
//
//    @Transactional
//    public void createDummyDepartments() {
//        Map<String, Department> existingDepartments = departmentRepository.findAll().stream()
//            .collect(Collectors.toMap(Department::getName, d -> d));
//
//        insertIfNotExists(existingDepartments, "인사팀", "채용, 평가, 복지 등 인사관리 담당", LocalDate.of(2020, 1, 10));
//        insertIfNotExists(existingDepartments, "재무팀", "회계, 세무, 자금 운용 업무 담당", LocalDate.of(2020, 3, 15));
//        insertIfNotExists(existingDepartments, "마케팅팀", "마케팅 전략 수립 및 광고 캠페인 기획", LocalDate.of(2021, 5, 1));
//        insertIfNotExists(existingDepartments, "영업팀", "고객 발굴 및 제품/서비스 판매 활동 수행", LocalDate.of(2021, 7, 20));
//        insertIfNotExists(existingDepartments, "연구개발팀", "신제품 및 신기술 연구 및 개발", LocalDate.of(2019, 9, 1));
//        insertIfNotExists(existingDepartments, "고객지원팀", "고객 응대 및 문제 해결 지원 업무 담당", LocalDate.of(2022, 1, 5));
//        insertIfNotExists(existingDepartments, "전략기획팀", "기업 전략 수립 및 신규 사업 기획", LocalDate.of(2021, 11, 25));
//        insertIfNotExists(existingDepartments, "보안팀", "사이버 보안 및 정보보호 정책 수립", LocalDate.of(2023, 3, 10));
//        insertIfNotExists(existingDepartments, "데이터분석팀", "데이터 수집 및 분석, 인사이트 도출", LocalDate.of(2020, 4, 4));
//        insertIfNotExists(existingDepartments, "디자인팀", "UI/UX 및 그래픽 디자인 담당", LocalDate.of(2020, 6, 17));
//        insertIfNotExists(existingDepartments, "QA팀", "제품 품질 보증 및 테스트 수행", LocalDate.of(2020, 8, 3));
//        insertIfNotExists(existingDepartments, "생산관리팀", "제품 생산 계획 및 공정 관리", LocalDate.of(2018, 2, 21));
//        insertIfNotExists(existingDepartments, "물류팀", "재고 및 출고, 유통 관리 업무", LocalDate.of(2018, 6, 1));
//        insertIfNotExists(existingDepartments, "법무팀", "계약, 소송, 컴플라이언스 등 법률 자문 제공", LocalDate.of(2020, 9, 10));
//        insertIfNotExists(existingDepartments, "총무팀", "사무환경 및 비품, 복리후생 운영", LocalDate.of(2020, 10, 1));
//        insertIfNotExists(existingDepartments, "운영팀", "전사 운영 효율화 및 업무 표준화 담당", LocalDate.of(2019, 12, 1));
//        insertIfNotExists(existingDepartments, "교육팀", "임직원 교육 과정 기획 및 운영", LocalDate.of(2021, 1, 20));
//        insertIfNotExists(existingDepartments, "해외사업팀", "해외 지사 관리 및 글로벌 사업 확장", LocalDate.of(2021, 8, 15));
//        insertIfNotExists(existingDepartments, "감사팀", "내부 감사 및 부정 방지 활동", LocalDate.of(2022, 6, 30));
//        insertIfNotExists(existingDepartments, "CSR팀", "사회공헌 및 지속가능경영 활동", LocalDate.of(2022, 10, 5));
//    }
//
//    private void insertIfNotExists(Map<String, Department> existingMap, String name, String description, LocalDate date) {
//        if (!existingMap.containsKey(name)) {
//            Department department = Department.builder()
//                .name(name)
//                .description(description)
//                .establishedDate(date)
//                .build();
//            departmentRepository.save(department);
//        }
//    }



    @Override
    @Transactional
    public DepartmentDto create(DepartmentCreateRequest request) {
        String name = request.name();
        String description = request.description();
        LocalDate establishedDate = request.establishedDate();

        if (departmentRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
        }

        Department department = Department.builder()
            .name(name)
            .description(description)
            .establishedDate(establishedDate)
            .build();

        Department createdDepartment = departmentRepository.save(department);

        return departmentMapper.toDto(createdDepartment, calculateEmployeeCount(createdDepartment));
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDto findById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 부서입니다"));

        return departmentMapper.toDto(department, calculateEmployeeCount(department));
    }

    @Override
    @Transactional
    public DepartmentDto update(Long departmentId, DepartmentUpdateRequest request) {
        String name = request.name();
        String description = request.description();
        LocalDate establishedDate = request.establishedDate();

        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 부서입니다."));

        if (!department.getName().equals(name) && departmentRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
        }

        department.update(name, description, establishedDate);

        return departmentMapper.toDto(department, calculateEmployeeCount(department));
    }

    @Override
    @Transactional
    public void delete(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 부서입니다."));
        int employeeCount = employeeRepository.countByDepartmentId(departmentId);
        if (employeeCount > 0) {
            throw new IllegalArgumentException("소속 직원이 있는 부서는 삭제할 수 없습니다.");
        }

        departmentRepository.delete(department);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<DepartmentDto> findDepartments(
        String nameOrDescription,
        Long idAfter,
        String cursor,
        Integer size,
        String sortField,
        String sortDirection
    ) {
        int pageSize = size != null ? size : 10;

        Long effectiveCursor = null;
        if (idAfter != null) {
            effectiveCursor = idAfter;
        } else if (cursor != null && !cursor.isBlank()) {
            effectiveCursor = decodeCursor(cursor);
        }

        List<Department> departments = departmentRepository.findNextDepartments(
            effectiveCursor, pageSize, sortField, sortDirection, nameOrDescription
        );

        boolean hasNext = departments.size() > pageSize;

        List<Department> currentPage = hasNext ? departments.subList(0, pageSize) : departments;

        Long nextIdAfter =
            hasNext && !currentPage.isEmpty() ? currentPage.get(pageSize - 1).getId() : null;

        String nextCursor = (nextIdAfter != null)
            ? Base64.getEncoder().encodeToString(String.valueOf(nextIdAfter).getBytes())
            : null;

        long totalElements = departmentRepository.countAllByCondition(nameOrDescription);

        List<Long> departmentIds = currentPage.stream()
            .map(Department::getId)
            .toList();

        Map<Long, Long> employeeCountMap = employeeRepository.countByDepartmentIds(departmentIds);

        List<DepartmentDto> dtoList = departments.stream()
            .map(dept -> DepartmentDto.builder()
                .id(dept.getId())
                .name(dept.getName())
                .description(dept.getDescription())
                .establishedDate(dept.getEstablishedDate())
                .employeeCount(employeeCountMap.getOrDefault(dept.getId(), 0L).intValue())
                .build())
            .toList();

        return new CursorPageResponse<>
            (
                dtoList, nextIdAfter, nextCursor, pageSize, totalElements, hasNext
            );
    }

    private Long decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(new String(Base64.getDecoder().decode(cursor)));
        } catch (Exception e) {
            log.warn("Invalid cursor decode error", e);
            throw new IllegalArgumentException("잘못된 커서 형식입니다.");
        }
    }


    private int calculateEmployeeCount(Department department) {

        return employeeRepository.countByDepartmentId(department.getId());
    }
}
