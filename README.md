# 🪖 2조 팀 예비군 - HRBank 프로젝트

[🔗 프로젝트 노션 페이지 바로가기](https://www.notion.so/ohgiraffers/2-207649136c1180e3953cfcfcde029bfb)

## 👥 팀원 구성

| 이름     | GitHub 링크 |
|----------|-------------|
| 한동우   | [@Dw-real](https://github.com/Dw-real) |
| 김동욱   | [@bladnoch](https://github.com/bladnoch) |
| 이주용   | [@pureod](https://github.com/pureod) |
| 이승진   | [@noonsong0208](https://github.com/noonsong0208) |
| 이건민   | [@GeonMin02](https://github.com/GeonMin02) |

---

## 📌 프로젝트 소개

> 코드잇 백엔드 부트캠프 3기 최종 팀 프로젝트  
> 스프링 기반 사내 직원 정보 관리 및 백업 시스템

- **프로젝트명**: HRBank
- **진행기간**: 2025.06.03 ~ 2025.06.13
<img width="939" alt="스크린샷 2025-06-17 오후 3 08 12" src="https://github.com/user-attachments/assets/301f663d-9865-4728-a7dc-f0dac5eb3e3b" />

---

## 🛠 기술 스택



### Backend

- **Framework**: Spring Boot 3.5.0  
- **Data Access**: Spring Data JPA, JDBC  
- **Scheduler**: Spring Scheduler  
- **Build Tool**: Gradle (Java 17 Toolchain)  
- **문서화**: SpringDoc OpenAPI  
- **QueryDSL**: 5.0.0 (Jakarta 기반)  
- **DTO 매핑**: MapStruct 1.5.5.Final  
- **기타**: Lombok

### Database

- PostgreSQL (운영용)
- H2 (로컬/테스트용)

### 협업 도구

- Git & GitHub
- Discord
- ZEP


---
## ✨ 구현 기능

### 👤 한동우

<p align="center">
  <img src="https://github.com/user-attachments/assets/8fee4af7-3a3a-45ab-9369-2634f5065b9e" width="800"/>
  <br/>
  <img src="https://github.com/user-attachments/assets/26726632-4584-43ef-961a-b5e0f4c6357d" width="800"/>
</p>

- **직원 목록 조회 API**
  - 직원 전체 리스트를 페이징 처리 및 조건 검색과 함께 조회하는 API 구현 (QueryDSL 사용)
- **직원 수 추이 조회 API**
  - 월별 혹은 연도별 기준으로 직원 수 변화량을 조회할 수 있는 API 구현 (QueryDSL 사용)
- **직원 분포 조회 API**
  - 직급, 부서 등 다양한 기준에 따른 직원 분포 현황 제공 (QueryDSL 사용)
- **직원 수 조회 API**
  - 전체 직원 수 또는 조건부 필터를 적용한 직원 수를 반환하는 API (QueryDSL 사용)

---

### 🫣 김동욱

<p align="center">
  <img src="https://github.com/user-attachments/assets/589edb40-de48-4fc0-8bbf-c8f3fd6051ed" width="800"/>
</p>

- **직원 데이터 관리 API**
  - POST 요청을 통해 직원 정보를 백업하는 API 엔드포인트 구현  
  - 백업 시 직원 데이터 변경 여부를 판단하여 파일을 생성하는 로직 개발  
  - 백업 서비스 메서드의 동시 실행을 방지하기 위한 AOP 기반 동시성 제어 기능 구현
- **직원 정보 다운로드 기능**
  - 다운로드 버튼 클릭 시, 직원 정보를 파일로 다운로드할 수 있는 기능 구현 
- **직원 백업 데이터 조회 API** 
  - GET 요청을 사용하여 직원 백업 데이터를 정렬 후, 커서 기반 페이지네이션 방식으로 반환하는 API 구현  
  - QueryDSL을 활용해 검색 조건에 맞는 데이터를 필터링하여 응답하도록 기능 구현
- **자동 백업 및 파일 정리 기능**
  - Spring Scheduler를 사용하여 직원 백업을 1시간마다 자동으로 수행하는 기능 개발  
  - 시스템 오류 등으로 생성된 고아 파일을 주기적으로 탐색하고 삭제하는 정리 스케줄러 기능 구현

---

### 🏢 이주용

<p align="center">
  <img src="https://github.com/user-attachments/assets/3ca97b3d-5c6d-44b0-b10e-d350d518e2b5" width="800"/>
</p>

- **부서 목록 조회 API**
  - 부서 전체 리스트를 커서 기반 페이지네이션과 조건 검색(이름, 설명 등)과 함께 조회하는 API 구현 (QueryDSL 사용)
- **부서 단건 조회 API**
  - 부서 ID를 기준으로 특정 부서 정보를 상세 조회하는 API 구현
- **부서 등록 API**
  - 새로운 부서를 등록할 수 있는 API 구현 (입력값 검증 포함)
- **부서 수정 API**
  - 기존 부서 정보를 수정할 수 있는 API 구현 (부분 수정 지원, 유효성 검증 포함)
- **부서 삭제 API**
  - 부서 ID를 기반으로 해당 부서를 삭제하는 API 구현  
  - 단, 소속 직원이 있을 경우 삭제 제한 로직 포함

---

### 👨‍💻 이승진

- **직원 정보 관리 전담**
  - 직원 등록, 상세 조회, 목록, 수정, 삭제 기능 백엔드 구현 (Spring Boot, JPA 기반)  
  - RESTful API로 프론트엔드와 연동, 업무 로직/DTO/엔티티 관리
- **직원 정보 등록 & 상세 조회**
  - 이름, 이메일, 부서, 직함, 입사일, 상태, 프로필 이미지 등 상세 정보 등록 및 조회 API 구현  
  - 프로필 이미지는 메타정보(DB), 실제 파일(로컬)로 분리 저장 (파일 관리 요구사항 준수)
- **직원 정보 수정 & 삭제**
  - 사원번호 제외 모든 항목 수정 지원, 이메일 중복 불가 등 비즈니스 규칙 반영  
  - 직원 삭제 시 연관 프로필 이미지도 자동 삭제 처리

---

### 🗂 이건민

<p align="center">
  <img src="https://github.com/user-attachments/assets/2deff0f1-b27d-41d2-b5d8-e3cfd752ac72" width="800"/>
  <br/>
  <img src="https://github.com/user-attachments/assets/99cdd679-cfda-4217-8d8b-6b318421e68f" width="800"/>
</p>

- **직원 정보 이력 자동 저장 메서드**
  - 직원 생성 / 수정 / 삭제 시의 정보를 로그로 남겨 정보 확인 가능  
  - ChangeType에 따른 이력 정보 필드 저장 방식 설계  
  - memo값이 비어있을 때, 타입에 따른 기본 메세지 보정 적용
- **조회 필터링**
  - 부분 일치(사원 번호, 메모, IP 주소), 기간 일치(from ~ to), 완전 일치(수정 유형) 등 복합 조건

---

## **파일 구조**
```
├── main
│   ├── java
│   │   └── org
│   │       └── yebigun
│   │           └── hrbank
│   │               ├── domain
│   │               │   ├── backup
│   │               │   │   ├── aop
│   │               │   │   │   ├── BackupSynchronizedExecutionAspect.java
│   │               │   │   │   └── SynchronizedExecution.java
│   │               │   │   ├── controller
│   │               │   │   │   ├── BackupApi.java
│   │               │   │   │   └── BackupController.java
│   │               │   │   ├── dto
│   │               │   │   │   ├── BackupDto.java
│   │               │   │   │   └── CursorPageResponseBackupDto.java
│   │               │   │   ├── entity
│   │               │   │   │   ├── Backup.java
│   │               │   │   │   └── BackupStatus.java
│   │               │   │   ├── mapper
│   │               │   │   │   └── BackupMapper.java
│   │               │   │   ├── repository
│   │               │   │   │   ├── BackupRepository.java
│   │               │   │   │   ├── BackupRepositoryCustom.java
│   │               │   │   │   └── BackupRepositoryImpl.java
│   │               │   │   ├── scheduler
│   │               │   │   │   └── BackupScheduler.java
│   │               │   │   └── service
│   │               │   │       ├── BackupService.java
│   │               │   │       └── BackupServiceImpl.java
│   │               │   ├── binaryContent
│   │               │   │   ├── controller
│   │               │   │   │   ├── BinaryContentApi.java
│   │               │   │   │   └── BinaryContentController.java
│   │               │   │   ├── dto
│   │               │   │   │   └── BinaryContentResponseDto.java
│   │               │   │   ├── entity
│   │               │   │   │   └── BinaryContent.java
│   │               │   │   ├── mapper
│   │               │   │   │   └── BinaryContentMapper.java
│   │               │   │   ├── repository
│   │               │   │   │   └── BinaryContentRepository.java
│   │               │   │   ├── scheduler
│   │               │   │   │   └── BinaryContentScheduler.java
│   │               │   │   ├── service
│   │               │   │   │   ├── BinaryContentService.java
│   │               │   │   │   └── BinaryContentServiceImpl.java
│   │               │   │   └── storage
│   │               │   │       ├── BackupFileStorage.java
│   │               │   │       ├── BackupFileStorageImpl.java
│   │               │   │       ├── BinaryContentStorage.java
│   │               │   │       └── BinaryContentStorageImpl.java
│   │               │   ├── changelog
│   │               │   │   ├── controller
│   │               │   │   │   ├── ChangeLogApi.java
│   │               │   │   │   └── ChangeLogController.java
│   │               │   │   ├── dto
│   │               │   │   │   └── data
│   │               │   │   │       ├── ChangeLogDto.java
│   │               │   │   │       ├── ChangeLogSearchCondition.java
│   │               │   │   │       └── DiffDto.java
│   │               │   │   ├── entity
│   │               │   │   │   ├── ChangeLog.java
│   │               │   │   │   ├── ChangeLogDiff.java
│   │               │   │   │   ├── ChangeType.java
│   │               │   │   │   └── PropertyName.java
│   │               │   │   ├── mapper
│   │               │   │   │   ├── ChangeLogDiffMapper.java
│   │               │   │   │   └── ChangeLogMapper.java
│   │               │   │   ├── repository
│   │               │   │   │   ├── ChangeLogDiffRepository.java
│   │               │   │   │   ├── ChangeLogRepository.java
│   │               │   │   │   ├── ChangeLogRepositoryCustom.java
│   │               │   │   │   └── ChangeLogRepositoryImpl.java
│   │               │   │   └── service
│   │               │   │       ├── ChangeLogService.java
│   │               │   │       └── ChangeLogServiceImpl.java
│   │               │   ├── changelog.zip
│   │               │   ├── department
│   │               │   │   ├── controller
│   │               │   │   │   ├── DepartmentApi.java
│   │               │   │   │   └── DepartmentController.java
│   │               │   │   ├── dto
│   │               │   │   │   ├── data
│   │               │   │   │   │   ├── DepartmentDto.java
│   │               │   │   │   │   └── DepartmentEmployeeCount.java
│   │               │   │   │   └── request
│   │               │   │   │       ├── DepartmentCreateRequest.java
│   │               │   │   │       └── DepartmentUpdateRequest.java
│   │               │   │   ├── entity
│   │               │   │   │   └── Department.java
│   │               │   │   ├── exception
│   │               │   │   │   ├── DepartmentHasEmployeesException.java
│   │               │   │   │   ├── DuplicatedDepartmentNameException.java
│   │               │   │   │   └── NotFoundDepartmentException.java
│   │               │   │   ├── mapper
│   │               │   │   │   └── DepartmentMapper.java
│   │               │   │   ├── repository
│   │               │   │   │   ├── DepartmentRepository.java
│   │               │   │   │   ├── DepartmentRepositoryCustom.java
│   │               │   │   │   └── DepartmentRepositoryImpl.java
│   │               │   │   └── service
│   │               │   │       ├── DepartmentService.java
│   │               │   │       └── DepartmentServiceImpl.java
│   │               │   ├── employee
│   │               │   │   ├── controller
│   │               │   │   │   ├── EmployeeApi.java
│   │               │   │   │   └── EmployeeController.java
│   │               │   │   ├── dto
│   │               │   │   │   ├── data
│   │               │   │   │   │   ├── EmployeeDistributionDto.java
│   │               │   │   │   │   ├── EmployeeDto.java
│   │               │   │   │   │   └── EmployeeTrendDto.java
│   │               │   │   │   └── request
│   │               │   │   │       ├── EmployeeCreateRequest.java
│   │               │   │   │       ├── EmployeeListRequest.java
│   │               │   │   │       └── EmployeeUpdateRequest.java
│   │               │   │   ├── entity
│   │               │   │   │   ├── Employee.java
│   │               │   │   │   └── EmployeeStatus.java
│   │               │   │   ├── exception
│   │               │   │   │   ├── DuplicateEmailException.java
│   │               │   │   │   ├── UnsupportedGroupByException.java
│   │               │   │   │   ├── UnsupportedSortDirectionException.java
│   │               │   │   │   ├── UnsupportedSortFieldException.java
│   │               │   │   │   └── UnsupportedUnitException.java
│   │               │   │   ├── mapper
│   │               │   │   │   └── EmployeeMapper.java
│   │               │   │   ├── repository
│   │               │   │   │   ├── EmployeeRepository.java
│   │               │   │   │   ├── EmployeeRepositoryCustom.java
│   │               │   │   │   └── EmployeeRepositoryImpl.java
│   │               │   │   └── service
│   │               │   │       ├── EmployeeService.java
│   │               │   │       └── EmployeeServiceImpl.java
│   │               │   └── employee.zip
│   │               ├── global
│   │               │   ├── base
│   │               │   │   ├── BaseEntity.java
│   │               │   │   └── BaseUpdatableEntity.java
│   │               │   ├── config
│   │               │   │   ├── QuerydslConfig.java
│   │               │   │   └── SwaggerConfig.java
│   │               │   ├── dto
│   │               │   │   ├── CursorPageResponse.java
│   │               │   │   └── ErrorResponse.java
│   │               │   ├── exception
│   │               │   │   ├── CustomBackupSynchronizedException.java
│   │               │   │   ├── DuplicateException.java
│   │               │   │   ├── GlobalExceptionHandler.java
│   │               │   │   ├── NotFoundException.java
│   │               │   │   └── UnsupportedException.java
│   │               │   ├── seeder
│   │               │   │   ├── AllDataSeederRunner.java
│   │               │   │   ├── DataSeeder.java
│   │               │   │   ├── DepartmentDataSeeder.java
│   │               │   │   └── EmployeeDataSeeder.java
│   │               │   └── util
│   │               └── HrBankApplication.java
│   └── resources
│       ├── application-deploy.yaml
│       ├── application-dev.yaml
│       ├── application.yaml
│       ├── schema.sql
│       ├── static
│       │   ├── assets
│       │   │   ├── images
│       │   │   │   └── default-profile.svg
│       │   │   └── index-aNksrdbr.js
│       │   ├── favicon.ico
│       │   └── index.html
│       └── templates
└── test
    └── java
        └── org
            └── yebigun
                └── hrbank
                    └── HrBankApplicationTests.java
```
## 🌐 배포 주소

> Railway를 통해 배포된 HRBank 웹페이지

🔗 [HRBank 웹페이지](https://sb03-hrbank-team2-production-99c9.up.railway.app)

---

## 💬 프로젝트 회고록

> 발표자료 또는 회고 링크를 여기에 첨부하세요

📄 [프로젝트 회고록](https://www.notion.so/ohgiraffers/HR-Bank-4L-210649136c118042a1d9d8d928ce1eb8)

---
