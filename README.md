# Recovery Backend Project

본 프로젝트는 사용자의 운동 기록을 관리하는 백엔드 API 서버입니다. 단순한 기능 구현을 넘어, 지속 가능한 코드와 안정적인 시스템 운영을 목표로 대대적인 리팩토링을 진행했습니다. 이 과정에서 **RESTful API 원칙 준수**와 **견고한 개발 프로세스 확립**이라는 두 가지 핵심 가치에 집중했습니다.

---

## 👨‍💻 개발자
- **김모세**: [GitHub Profile](https://github.com/moset1)

---

## 🛠️ 기술 스택 (Tech Stack)

| 구분 | 기술                                         |
|---|--------------------------------------------|
| **Language** | Java 17                                    |
| **Framework** | Spring Boot 3.x                            |
| **Security** | Spring Security, **JWT (JSON Web Token)**  |
| **Database** | H2 (for testing), MySQL                    |
| **Build Tool** | Gradle                                     |
| **Testing** | JUnit 5, Mockito, **Spring Security Test** |
| **CI/CD** | **GitHub Actions**                         |

---

## 📖 주요 기능 (Key Features)

- **사용자 관리**: **JWT 토큰 기반**의 안전한 회원가입 및 로그인 기능
- **운동 정보 제공**: 재활에 필요한 운동의 상세 정보(주의사항, 영상 링크 등) 조회 기능
- **운동 기록 관리**: 사용자의 일일/기간별 운동 수행 기록 조회 및 신규 기록 저장 기능

---

## ⚙️ 기술적 결정 및 개선 과정 (Refactoring Journey)

이 프로젝트는 단순히 기능을 구현하는 것을 넘어, **지속 가능한 코드와 안정적인 시스템 운영**을 목표로 했습니다. 초기 설계의 한계를 극복하기 위해 아래 두 가지 핵심 영역에 대한 기술적 개선을 수행했습니다.

### 1. RESTful API의 완성: Stateless 아키텍처로의 전환

초기 설계의 상태 의존성(Stateful) 문제를 해결하여 확장성과 성능을 극대화했습니다.

#### 문제점 (Problem)
기존 세션 기반 인증 방식은 서버가 각 사용자의 로그인 상태를 저장해야 했습니다. 이는 서버 증설 시 세션 클러스터링 등 추가적인 복잡성을 야기하여 수평적 확장(Scale-out)을 저해하는 요인이었습니다.

#### 해결 방안 (Solution)
*   **JWT 기반 무상태(Stateless) 인증**: 세션 방식 대신 **JWT(JSON Web Token)**를 도입하여 인증 시스템을 전면 개편했습니다. 서버는 더 이상 클라이언트의 상태를 저장하지 않으며, 각 요청은 토큰을 통해 독립적으로 처리됩니다.
    - `SecurityConfig`에서 `SessionCreationPolicy.STATELESS`를 적용하여 세션 사용을 중단했습니다.
    - `JwtTokenProvider`와 `JwtAuthenticationFilter`를 구현하여 토큰의 생성, 검증, 인증 처리를 자동화했습니다.

*   **HTTP ETag 캐싱을 통한 성능 최적화**: 불필요한 데이터 전송을 줄이고 응답 속도를 개선하기 위해 HTTP 캐싱을 적용했습니다.
    - Spring이 제공하는 `ShallowEtagHeaderFilter`를 `WebConfig`에 등록하여 `GET` 요청에 대한 `ETag` 헤더를 자동으로 생성하도록 했습니다.
    - 데이터 변경이 없는 반복 요청에 대해 서버는 `304 Not Modified`로 응답하여 네트워크 트래픽을 획기적으로 줄였습니다.

#### 기대효과 (Impact)
*   **높은 확장성**: 서버의 수평적 확장이 용이한 아키텍처를 구축했습니다.
*   **성능 향상**: 네트워크 효율성을 극대화하여 사용자에게 더 빠른 응답 속도를 제공합니다.
*   **클라이언트 독립성**: 다양한 클라이언트(웹, 모바일 앱 등)를 유연하게 지원할 수 있습니다.

### 2. TDD와 CI를 통한 견고한 개발 프로세스 확립

지속적인 기능 추가와 코드 변경에도 흔들리지 않는 안정적인 시스템을 구축했습니다.

#### 목표 (Goal)
코드 변경 시 발생할 수 있는 **회귀 버그(Regression Bug)를 사전에 방지**하고, 언제나 안정적으로 배포 가능한 코드베이스를 유지하는 것을 목표로 했습니다.

#### 구현 내용 (Implementation)
*   **계층별 테스트 전략 수립**:
    - **단위 테스트**: `Mockito`를 활용하여 각 클래스나 메소드가 독립적으로 정확히 동작하는지 검증했습니다.
    - **통합 테스트**: `MockMvc`와 `@SpringBootTest`를 사용하여 API 엔드포인트부터 데이터베이스까지의 전체 흐름과 보안 규칙을 종합적으로 테스트했습니다.

*   **GitHub Actions 기반 CI 파이프라인 구축**:
    - `Pull Request`가 생성되거나 업데이트될 때마다, GitHub Actions 워크플로우가 모든 테스트 코드를 자동으로 실행합니다.
    - 테스트가 하나라도 실패할 경우 `main` 브랜치로의 병합(Merge)이 불가능하도록 브랜치 보호 규칙을 설정하여 코드의 무결성을 항상 보장했습니다.

#### 기대효과 (Impact)
*   **높은 코드 신뢰성**: 자동화된 테스트가 코드 변경에 대한 즉각적인 피드백을 제공하여 버그가 운영 환경에 유입될 가능성을 크게 낮췄습니다.
*   **향상된 유지보수성**: 잘 작성된 테스트 코드는 그 자체로 **살아있는 문서(Living Documentation)** 역할을 하여 새로운 팀원이 합류하거나 기존 코드를 수정할 때 로직을 쉽고 빠르게 파악할 수 있도록 돕습니다.
*   **자신감 있는 개발 문화**: 리팩토링이나 새로운 기능 추가 시, 기존 기능이 망가질지 모른다는 불안감 없이 자신감 있게 개발을 진행할 수 있는 안정적인 환경을 구축했습니다.

---

## 🚀 프로젝트 실행 방법

1.  **프로젝트 클론**
    ```sh
    git clone https://github.com/moset1/Recovery_BackEnd.git
    ```
2.  **프로젝트 실행**
    - IntelliJ IDEA와 같은 IDE에서 프로젝트를 열고, `RecoveryBackEndApplication.java` 파일을 찾아 실행합니다.
    - 실행 전 `src/main/resources/application.properties`에 데이터베이스 및 JWT 설정을 확인/수정합니다.
    ```properties
    # JWT Secret Key (반드시 강력한 무작위 문자열로 변경)
    jwt.secret=your-very-secret-key-that-is-long-and-secure
    ```
3.  **H2 데이터베이스 콘솔 접속**
    - 애플리케이션 실행 후, 웹 브라우저에서 `http://localhost:8080/h2-console`로 접속합니다.
    - **JDBC URL**: `jdbc:h2:mem:testdb`
    - **사용자명**: `sa`
    - **비밀번호**: (공백)
4.  **테스트 실행**
    - 프로젝트의 모든 테스트를 실행하려면 다음 명령어를 사용하세요.
    ```sh
    ./gradlew test
    ```
