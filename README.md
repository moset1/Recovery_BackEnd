# Recovery: 뇌졸중 환자 재활 보조 애플리케이션 백엔드

2024 Google Solution Challenge 참가 프로젝트로, 뇌졸중 환자의 재활 과정을 돕는 태블릿용 애플리케이션의 REST API 서버를 개발했습니다.

---

## 👨‍💻 개발자
- **김모세**: [GitHub 프로필 링크](https://github.com/moset1)

---

## 🛠️ 기술 스택 (Tech Stack)

| 구분 | 기술 |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.x, Spring Security |
| **Database** | H2 (In-memory) |
| **Build Tool** | Gradle |
| **Testing** | JUnit 5, Mockito |

---

## 📖 주요 기능 (Key Features)

- **사용자 관리**: Spring Security를 이용한 안전한 회원가입 및 이메일/비밀번호 기반 로그인/로그아웃 기능
- **운동 정보 제공**: 재활에 필요한 운동의 상세 정보(주의사항, 영상 링크 등) 조회 기능
- **운동 기록 관리**: 사용자의 일일/기간별 운동 수행 기록 조회 및 신규 기록 저장 기능

---

## ⚙️ 기술적 결정 및 개선 과정

이 프로젝트는 단순히 기능을 구현하는 것을 넘어, 안정적이고 확장 가능한 API 서버를 구축하는 것을 목표로 했습니다. 단위 테스트 작성 과정에서 발견된 문제점들을 아래와 같이 개선했습니다.

### 1. DTO 도입을 통한 API 명세 안정화

#### 문제점
초기 API는 데이터베이스와 직접 매핑된 `Entity` 객체를 요청/응답에 그대로 사용했습니다. 이는 DB 스키마 변경이 API 명세에 직접적인 영향을 미치는 취약한 구조였습니다.

#### 해결 방안
`Request/Response DTO(Data Transfer Object)`를 도입하여 계층 간 데이터 전송 역할을 분리했습니다.
- **안정성 확보**: DB 스키마가 변경되어도 DTO를 통해 API 명세를 동일하게 유지할 수 있습니다.
- **데이터 은닉**: 클라이언트에게 불필요한 서버 내부 데이터를 노출하지 않아 보안이 향상됩니다.

### 2. 전역 예외 처리 및 책임 분리

#### 문제점
존재하지 않는 리소스 요청 시, 서비스 로직의 부재로 `NullPointerException`이 발생하며 서버 오류(500)를 반환했습니다. 또한, 컨트롤러가 서비스 계층의 역할(사용자 정보 조회 등)을 일부 수행하여 역할 분리가 불분명했습니다.

#### 해결 방안
- **책임 분리**: 컨트롤러에 있던 비즈니스 로직을 `Service` 계층으로 모두 이동시켜, 컨트롤러는 오직 HTTP 요청/응답 처리에만 집중하도록 구조를 개선했습니다.
- **전역 예외 처리**: `ResourceNotFoundException`과 같은 커스텀 예외를 정의하고, `@RestControllerAdvice`를 이용한 `GlobalExceptionHandler`를 구현했습니다. 이를 통해 존재하지 않는 리소스 요청 시, `404 Not Found` 상태 코드와 명확한 에러 메시지를 클라이언트에게 반환하여 API의 신뢰도를 높였습니다.

### 3. RESTful 인증 응답 처리

#### 문제점
기본 `formLogin` 설정은 로그인 성공/실패 시 특정 URL로 리다이렉트합니다. 이는 JSON 데이터를 주고받는 REST API 환경(모바일 앱, SPA 등)에서는 클라이언트가 후속 조치를 취하기 어려운 방식입니다.

#### 해결 방안
`SecurityConfig`에서 `successHandler`와 `failureHandler`를 직접 구현하여, 페이지 리다이렉트 대신 **상태 코드와 JSON 메시지를 반환**하도록 변경했습니다.
- **로그인 성공**: `200 OK` 상태 코드와 함께 성공 메시지를 담은 JSON 응답
- **로그인 실패**: `401 Unauthorized` 상태 코드와 함께 실패 원인을 담은 JSON 응답

이를 통해 API 클라이언트가 인증 결과를 명확히 파악하고, 토큰 저장이나 에러 메시지 표시 등 후속 작업을 원활하게 수행할 수 있도록 API의 유연성과 사용성을 크게 향상시켰습니다.

---

## 🚀 프로젝트 실행 방법

1.  **프로젝트 클론**
    ```sh
    git clone https://github.com/your-repo/Recovery_BackEnd.git
    ```
2.  **프로젝트 실행**
    - IntelliJ IDEA와 같은 IDE에서 프로젝트를 열고, `RecoveryBackEndApplication.java` 파일을 찾아 실행합니다.
3.  **H2 데이터베이스 콘솔 접속**
    - 애플리케이션 실행 후, 웹 브라우저에서 `http://localhost:8080/h2-console`로 접속합니다.
    - **JDBC URL**: `jdbc:h2:mem:testdb`
    - **사용자명**: `sa`
    - **비밀번호**: (공백)
