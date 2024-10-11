# SPRING ADVANCED
## 서비스 테스트 코드 작성률

<img width="690" alt="스크린샷 2024-09-13 00 46 57" src="https://github.com/user-attachments/assets/55a858b2-7b44-47af-b4ca-2f9d227c0881">
<img width="711" alt="스크린샷 2024-09-13 00 47 07" src="https://github.com/user-attachments/assets/5c0f5d2c-e996-4a1d-af2a-afe06a3550f1">

# 프로젝트 소개
레거시 코드의 테스트 코드 기반 리팩토링
# 요구 사항
## Level 1
### **1. 코드 개선 퀴즈 -** Early Return

조건에 맞지 않는 경우 즉시 리턴하여, 불필요한 로직의 실행을 방지하고 성능을 향상시킵니다.
패키지 `package org.example.expert.domain.auth.service;` 의 `AuthService` 클래스에 있는 `signup()` 중 아래의 코드 부분의 위치를 리팩토링해서

```java
if (userRepository.existsByEmail(signupRequest.getEmail())) {
    throw new InvalidRequestException("이미 존재하는 이메일입니다.");
}
```

해당 에러가 발생하는 상황일 때, `passwordEncoder`의 `encode()` 동작이 불필요하게 일어나지 않게 코드를 개선해주세요.

🚫 **주의사항!**

`SignupReqeust` 클래스의 `@NotBlank`, `@Email`과 관계 없이 리팩토링해주세요.

### **2. 리팩토링 퀴즈 - 불필요한 `if-else` 피하기**

복잡한 `if-else` 구조는 코드의 가독성을 떨어뜨리고 유지보수를 어렵게 만듭니다. 불필요한 `else` 블록을 없애 코드를 간결하게 합니다.
패키지 package org.example.expert.client; 의 WeatherClient 클래스에 있는 getTodayWeather() 중 아래의 코드 부분을 리팩토링해주세요.
```java
WeatherDto[] weatherArray = responseEntity.getBody();
if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
    throw new ServerException("날씨 데이터를 가져오는데 실패했습니다. 상태 코드: " + responseEntity.getStatusCode());
} else {
    if (weatherArray == null || weatherArray.length == 0) {
        throw new ServerException("날씨 데이터가 없습니다.");
    }
}
```

### **3. 리팩토링 퀴즈 - 메서드 분리**

복잡한 로직은 메서드로 분리하고, 메서드 이름만으로 동작을 명확히 이해할 수 있어야 합니다.
패키지 package org.example.expert.domain.user.service; 의 UserService 클래스에 있는 changePassword() 중 아래 코드 부분을 리팩토링해주세요.
```java
if (userChangePasswordRequest.getNewPassword().length() < 8 ||
        !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
        !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
    throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
}
```

### **4. 관심사 분리 - JWT 유효성 검사 로직 수정**

JWT 인증은 컨트롤러가 아닌 필터에서 처리하게 하여, 인증과 비즈니스 로직을 분리해야 합니다. 이렇게 하면 코드 중복을 줄이고, 컨트롤러는 본래의 기능에만 집중할 수 있습니다.
패키지 `package org.example.expert.domain.manager.controller;` 의 `ManagerController` 클래스에 있는 `deleteManager()` 에서 JWT 토큰을 직접 해석하여 사용자 정보를 추출하는 방식이 사용되고 있습니다. 

JWT 처리 로직과 비즈니스 로직을 분리하고, 인증된 사용자 정보를 더 적절한 방식으로 가져올 수 있도록 `deleteManager()` 메서드를 개선해주세요.

필요 시, `ManagerService` 클래스에 있는 `deleteManager()` 또한 리팩토링해주세요.


## Level 2
### **5. 테스트 연습 - 1**
테스트 패키지 `package org.example.expert.config;` 의 `PassEncoderTest` 클래스에 있는 `matches_메서드가_정상적으로_동작한다()` 테스트가 의도대로 성공할 수 있게 수정해 주세요.

### **6. 유닛 테스트 - 1**
테스트 패키지 `package org.example.expert.domain.manager.service;` 의 `ManagerServiceTest` 의 클래스에 있는 `manager_목록_조회_시_Todo가_없다면_NPE_에러를_던진다()` 테스트가 성공하고 컨텍스트와 일치하도록 테스트 코드와 테스트 코드 메서드 명을 수정해 주세요.

### **7. 유닛 테스트 - 2**
테스트 패키지 `org.example.expert.domain.comment.service;` 의 `CommentServiceTest` 의 클래스에 있는 `comment_등록_중_할일을_찾지_못해_에러가_발생한다()` 테스트가 성공할 수 있도록 **테스트 코드**를 수정해 주세요.

### **8. 유닛 테스트 - 3**
테스트 패키지 `org.example.expert.domain.manager.service`의 `ManagerServiceTest` 클래스에 있는 `todo의_user가_null인_경우_예외가_발생한다()` 테스트가 성공할 수 있도록 **서비스 로직**을 수정해 주세요.

### **9. AOP**

- 어드민 사용자만 접근할 수 있는 특정 API에는 접근할 때마다 접근 로그를 기록해야 합니다.
    - 어드민 사용자만 접근할 수 있는 컨트롤러 메서드는 다음 두 가지예요.
        - 패키지 `org.example.expert.domain.comment.controller;` 의 `CommentAdminController` 클래스에 있는 `deleteComment()`
        - 패키지 `package org.example.expert.domain.user.controller;` 의 `UserAdminController` 클래스에 있는 `changeUserRole()`
    - Spring AOP를 사용하여 해당 API들에 대한 접근 로그를 기록하는 기능을 구현하세요.

**주의사항!**
- **로그 기록에는 다음 정보가 포함되어야 합니다:**
    - 요청한 사용자의 ID
    - API 요청 시각
    - API 요청 URL

## Level 3

### **10. Service 테스트**
👉 Service 테스트는 왜 작성할까요?   
애플리케이션의 핵심 비즈니스 로직이 정확하게 구현되었는지 확인하기 위해 작성합니다. Service 테스트를 통해 복잡한 로직이 예상대로 동작하고, 데이터 처리가 정확하게 이루어지는지 검증할 수 있어요.

- 가능한 많은 서비스 테스트 코드를 작성하세요!
- 서비스 테스트 코드는 다음과 같은 대상으로 작성하면 좋아요.
    - 예외 조건 및 복잡한 로직을 가진 메소드
    - 데이터베이스, 외부 API 등 외부 시스템과 상호작용이 있는 메소드
    - 여러 서비스에서 공통적으로 사용되는 메소드
- 서비스 테스트 코드 작성률을 `README.md` 에 첨부해 주세요.
 
### **11. Controller 테스트**
👉 Controller 테스트는 왜 작성할까요?   
사용자 요청에 대해 예상한 대로 응답이 반환되는지 확인하기 위해 작성합니다.   
Controller 테스트를 통해 HTTP 요청 및 응답 흐름과 클라이언트와의 상호작용이 제대로 이루어지는지 검증할 수 있어요.   

- 가능한 많은 단위 컨트롤러 테스트 코드를 작성하세요!
- 컨트롤러 테스트 코드는 다음과 같은 대상으로 작성하면 좋아요.
    - 입력 값에 대해 다양한 HTTP 응답을 반환하는 메소드
    - 여러 API에서 공통으로 사용하는 메소드. ex) 인증이나 권한 검증을 수행하는 메소드
    - 외부 서비스와 통신하거나 데이터베이스와 상호작용하여 데이터를 받아오거나 저장하는 메소드
- 컨트롤러 테스트 코드 작성률을 `README.md` 에 첨부해 주세요.

### **12. 도전! 실무 과제**
🚀 간단한 실무 기반의 과제를 수행해봅니다!   

드디어 개발자 취업에 성공했어요!   
여러분이 신입 개발자로 입사하게 되면, 처음부터 프로젝트를 만들기보다 이미 구축된 시스템에 추가 요구사항을 개발하는 경우가 더 많습니다. 추가 요구사항을 개발하고, 그에 따른 테스트 코드를 작성합니다. 이를 통해 새로운 코드가 프로젝트에 안정적으로 통합되는 것을 보장할 수 있어요!   

- 요구사항
    - `댓글(comment)` 을 저장할 때, 해당 댓글의 `할일(todo)` 에 해당하는 `담당자(manager)` 가 아니면 댓글을 달 수 없도록 `예외(throw)`처리합니다.
- 테스트 코드 추가/수정
    - 7-2 과제를 잘 수행했다면  `CommentServiceTest.java` 테스트 코드는 정상 실행 됩니다. 하지만 기획 요건을 추가하게 되면 테스트 코드가 정상 실행되지 않습니다.
    - 12-1 과제: 추가한 기획 요건에 따라 예외를 처리하는 테스트 코드를 추가하세요.
    - 12-2 과제: 추가한 기획 요건에 따라 오류가 발생하는 기존 테스트 코드를 수정하세요.
