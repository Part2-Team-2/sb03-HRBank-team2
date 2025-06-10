package org.yebigun.hrbank.global.seeder;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.department.repository.DepartmentRepository;

@Profile("local")
@Component
@RequiredArgsConstructor
public class DepartmentDataSeeder {

    private final DepartmentRepository departmentRepository;

    @PostConstruct
    public void init() {
        createDummyDepartments();
    }

    /**
     * 더미 부서 데이터를 만들기 위한 메소드입니다.
     * 개수: 20
     * runtime에 자동으로 실행되는 구조입니다.
     */

    @Transactional
    public void createDummyDepartments() {
        Map<String, Department> existingDepartments = departmentRepository.findAll().stream()
            .collect(Collectors.toMap(Department::getName, d -> d));

        insertIfNotExists(existingDepartments, "인사팀", "채용, 평가, 복지 등 인사관리 담당", LocalDate.of(2020, 1, 10));
        insertIfNotExists(existingDepartments, "재무팀", "회계, 세무, 자금 운용 업무 담당", LocalDate.of(2020, 3, 15));
        insertIfNotExists(existingDepartments, "마케팅팀", "마케팅 전략 수립 및 광고 캠페인 기획", LocalDate.of(2021, 5, 1));
        insertIfNotExists(existingDepartments, "영업팀", "고객 발굴 및 제품/서비스 판매 활동 수행", LocalDate.of(2021, 7, 20));
        insertIfNotExists(existingDepartments, "연구개발팀", "신제품 및 신기술 연구 및 개발", LocalDate.of(2019, 9, 1));
        insertIfNotExists(existingDepartments, "고객지원팀", "고객 응대 및 문제 해결 지원 업무 담당", LocalDate.of(2022, 1, 5));
        insertIfNotExists(existingDepartments, "전략기획팀", "기업 전략 수립 및 신규 사업 기획", LocalDate.of(2021, 11, 25));
        insertIfNotExists(existingDepartments, "보안팀", "사이버 보안 및 정보보호 정책 수립", LocalDate.of(2023, 3, 10));
        insertIfNotExists(existingDepartments, "데이터분석팀", "데이터 수집 및 분석, 인사이트 도출", LocalDate.of(2020, 4, 4));
        insertIfNotExists(existingDepartments, "디자인팀", "UI/UX 및 그래픽 디자인 담당", LocalDate.of(2020, 6, 17));
        insertIfNotExists(existingDepartments, "QA팀", "제품 품질 보증 및 테스트 수행", LocalDate.of(2020, 8, 3));
        insertIfNotExists(existingDepartments, "생산관리팀", "제품 생산 계획 및 공정 관리", LocalDate.of(2018, 2, 21));
        insertIfNotExists(existingDepartments, "물류팀", "재고 및 출고, 유통 관리 업무", LocalDate.of(2018, 6, 1));
        insertIfNotExists(existingDepartments, "법무팀", "계약, 소송, 컴플라이언스 등 법률 자문 제공", LocalDate.of(2020, 9, 10));
        insertIfNotExists(existingDepartments, "총무팀", "사무환경 및 비품, 복리후생 운영", LocalDate.of(2020, 10, 1));
        insertIfNotExists(existingDepartments, "운영팀", "전사 운영 효율화 및 업무 표준화 담당", LocalDate.of(2019, 12, 1));
        insertIfNotExists(existingDepartments, "교육팀", "임직원 교육 과정 기획 및 운영", LocalDate.of(2021, 1, 20));
        insertIfNotExists(existingDepartments, "해외사업팀", "해외 지사 관리 및 글로벌 사업 확장", LocalDate.of(2021, 8, 15));
        insertIfNotExists(existingDepartments, "감사팀", "내부 감사 및 부정 방지 활동", LocalDate.of(2022, 6, 30));
        insertIfNotExists(existingDepartments, "CSR팀", "사회공헌 및 지속가능경영 활동", LocalDate.of(2022, 10, 5));
    }

    private void insertIfNotExists(Map<String, Department> existingMap, String name, String description, LocalDate date) {
        if (!existingMap.containsKey(name)) {
            Department department = Department.builder()
                .name(name)
                .description(description)
                .establishedDate(date)
                .build();
            departmentRepository.save(department);
        }
    }

}
