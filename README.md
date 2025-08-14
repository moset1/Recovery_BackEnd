# Recovery_BackEnd

2024 Google Solution Challenge 참가 프로젝트

# 설명
뇌졸중 환자들의 재활을 돕는 테블릿용 어플리케이션 

---

## 🛠️ 기술 스택 (Tech Stack)
- **Language**: Java 17
- **Framework**: Spring Boot 3.x, Spring Security
- **Database**: H2 (In-memory)
- **Build Tool**: Gradle
- **Testing**: JUnit 5, Mockito

---

## 📖 주요 기능 및 API 명세

### 1. 운동 (Exercise)
`GET /api/exercises/{exerciseName}`
- 특정 운동의 상세 정보(주의사항, 영상 링크 등)를 조회합니다.

### 2. 운동 기록 (Performance)
`GET /api/performance/daily`
- **Query Parameters**: `email`, `date`
- 특정 사용자의 일일 운동 기록 목록을 조회합니다.

`GET /api/performance/period`
- **Query Parameters**: `email`, `startDate`, `endDate`
- 특정 사용자의 기간별 운동 기록 목록을 조회합니다.

`POST /api/performance/save`
- 사용자의 운동 수행 결과를 저장합니다.
- **(리팩토링 대상, 상세 내용은 아래 개선 과정 참고)**

### 3. 사용자 (User)
`POST /api/user/signup`
- 이메일, 이름, 비밀번호를 받아 회원가입을 처리합니다.

---

## ⚙️ 프로젝트 개선 과정 (리팩토링)

단위 테스트 코드를 작성하는 과정에서 API의 안정성과 확장성을 높일 수 있는 몇 가지 개선점을 발견하여 리팩토링을 진행했습니다.

### 1. 동기
`PerformanceController`의 운동 기록 저장 API에 대한 테스트를 작성하던 중, 아래와 같은 문제점들을 발견했습니다.
- **계층 간 역할 불분명**: 컨트롤러가 사용자(`AppUser`)와 운동(`Exercise`) 정보를 직접 조회하는 등 서비스 계층의 역할을 수행하고 있었습니다.
- **불안정한 API 명세**: API가 데이터베이스와 직접 매핑된 `Entity` 객체를 그대로 요청받고 반환하여, DB 스키마 변경 시 API 명세까지 변경되는 취약한 구조였습니다.
- **미흡한 예외 처리**: 존재하지 않는 사용자나 운동 정보로 요청 시, `NullPointerException`이 발생하며 서버 오류(500)를 반환하여 클라이언트에게 명확한 에러 상황을 전달하지 못했습니다.

### 2. 개선 내용

#### ✅ DTO(Data Transfer Object) 도입
- `PerformanceSaveRequestDto`와 `PerformanceResponseDto`를 도입하여 API의 요청/응답 명세를 DB 구조와 분리했습니다.
- 이를 통해 API의 안정성을 높이고, 필요한 데이터만 외부에 노출하도록 변경했습니다.

#### ✅ 서비스 계층 역할 강화 및 책임 분리
- 컨트롤러에 있던 사용자/운동 정보 조회 로직을 `PerformanceService`로 이동시켰습니다.
- 컨트롤러는 이제 HTTP 요청을 받아 서비스에 전달하고, 그 결과를 응답하는 역할에만 집중합니다.

#### ✅ 명시적인 예외 처리
- `ResourceNotFoundException` 커스텀 예외를 정의하고, `@RestControllerAdvice`를 이용한 `GlobalExceptionHandler`를 구현했습니다.
- 이제 존재하지 않는 리소스 요청 시, `404 Not Found` 상태 코드와 명확한 에러 메시지를 반환하여 API의 신뢰도를 높였습니다.

### 3. API 명세 변경 (`/api/performance/save`)

**리팩토링 전 (Before)**
```json
// Request & Response Body (Entity 직접 사용)
{
    "performanceId": null,
    "appUser": { "email": "test@example.com" },
    "exercise": { "exerciseName": "팔 들어올리기" },
    "date": null,
    "score": "95"
}
```

**리팩토링 후 (After)**
```json
// Request Body (Request DTO)
{
    "userEmail": "test@example.com",
    "exerciseName": "팔 들어올리기",
    "score": "95"
}

// Response Body (Response DTO)
{
    "performanceId": 1,
    "userEmail": "test@example.com",
    "exerciseName": "팔 들어올리기",
    "date": "2024-05-21",
    "score": "95"
}
```
