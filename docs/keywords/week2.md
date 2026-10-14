# 1. Java의 record
- record: 정해진 데이터를 담는 클래스를 간결하게 선언하는 Java 문법입니다. 외부 라이브러리가 아닙니다.
- 자동 생성: 구성요소의 private final 필드, 생성자, 접근 메서드, equals(), hashCode(), toString()을 제공합니다. 접근 메서드는 getTitle()이 아닌 title() 형태입니다.
- 일반 클래스와의 차이: 구성요소 필드를 재할당할 수 없고, 다른 클래스를 상속하거나 record를 상속할 수 없습니다. 인터페이스 구현과 메서드 추가는 가능합니다.
- 주의점: 필드가 참조하는 List 같은 객체의 내부 상태까지 변경을 막지는 않습니다. 이를 얕은 불변성이라고 합니다.
- 사용 시점: 생성 후 값을 변경하지 않는 요청·응답 DTO에 적합합니다. 현재 CreatePostRequest와 UpdatePostRequest에 사용했습니다. 상태를 변경하는 Post에는 일반 클래스를 사용했습니다.

# 2. @Controller와 @RestController
- @Controller: Spring MVC에서 요청을 처리하는 클래스로, 일반적으로 화면을 반환할 때 사용합니다. String 반환값은 보통 뷰 이름으로 해석됩니다.
- @RestController: @Controller와 @ResponseBody가 결합된 애너테이션입니다. 반환값을 HTTP 응답 본문으로 전달합니다.
- @ResponseBody: 반환값을 메시지 변환기를 통해 JSON 등의 응답 데이터로 변환합니다. @RestController가 JSON만 반환하는 것은 아닙니다.
- 사용 시점: HTML 화면을 제공하면 @Controller, 데이터를 제공하는 API라면 @RestController를 사용합니다. @Controller도 @ResponseBody나 ResponseEntity로 데이터를 응답할 수 있습니다.
- 현재 적용: PostController는 ApiResponse 데이터를 반환하므로 @RestController를 사용하고, ResponseEntity로 상태코드와 본문을 설정합니다.

# 3. DTO, VO, DAO
- DTO(Data Transfer Object): 계층이나 시스템 사이에서 데이터를 전달하는 객체입니다. 내부 도메인 구조와 API 요청·응답을 분리하고, 필요한 필드만 전달하기 위해 사용합니다. 현재 CreatePostRequest와 UpdatePostRequest가 해당합니다.
- VO(Value Object): 별도의 식별자보다 값으로 의미와 동등성을 판단하는 객체입니다. 금액·주소·좌표처럼 값에 도메인 의미와 검증 규칙을 담을 때 사용하며, 일반적으로 불변으로 설계합니다.
- DAO(Data Access Object): 저장소의 조회·저장·수정·삭제를 담당하는 객체입니다. Service에서 SQL이나 저장 기술의 세부사항을 분리할 때 사용합니다.
- 차이: DTO는 데이터 전달, VO는 값의 의미와 동등성, DAO는 저장소 접근에 초점을 둡니다. record를 사용했다고 자동으로 VO가 되는 것은 아닙니다.
- 현재 적용: PostRepository와 InMemoryPostRepository가 DAO와 유사한 데이터 접근 책임을 담당합니다. Repository와 DAO가 완전히 같은 개념은 아닙니다.

# 4. RESTful은 무엇을 말할까?
- 특별한 규칙이 없이 API를 작성할 경우 사람마다 작성하는 방식이 달라지고 부여한 의미를 개인말고는 알 수 없기 때문에 API를 읽는 과정에서 소통 오해의 소지가 다분하다.
- 이러한 문제를 해결하기 위해서 RESTful한 API 설계 규칙을 정해두고 어느정도 적절한 규칙을 부여하는 개념이라고 생각한다. 
- 이 REST방식을 반드시 100% 엄격하게 지켜야 하는것도 아니며 그렇다고 완전히 무시해서도 안되는 그런 규칙이고 어느정도 RESTful하게 설계된 API들로 개발자들의 소통에 편의를 주는 요소일 뿐만 아니라
- 클라이언트가 서버 내부 구현에 의존하지 않고 공통된 인터페이스로 자원을 다루도록 하는 데 의미가 있다고 생각합니다.

# 5. Spring Bean의 생명주기
- Spring Bean: Spring IoC 컨테이너가 생성하고 관리하는 객체입니다.
- 정의 등록: 컴포넌트 스캔이나 @Bean 설정으로 관리할 객체를 등록합니다. 정의 등록과 실제 객체 생성은 다릅니다.
- 객체 생성: 생성자를 호출합니다. 생성자 주입은 필요한 의존 객체를 인자로 전달하면서 객체를 생성하는 방식입니다.
- 의존성 설정: 필드·setter 주입과 속성 설정을 수행합니다. 초기화 전에 필요한 의존성을 준비합니다.
- 초기화: 의존성을 갖춘 뒤 준비 작업을 수행합니다. 서로 다른 메서드를 지정했다면 @PostConstruct → afterPropertiesSet() → initMethod 순으로 실행합니다.
- 사용: 준비된 Bean을 요청 처리 등에 사용합니다. BeanPostProcessor는 초기화 전후 처리를 수행하며, 프록시가 적용될 수도 있습니다.
- 종료: 정상적인 컨테이너 종료 시 @PreDestroy → destroy() → destroyMethod 순으로 자원을 정리합니다. 강제 종료에서는 실행을 보장할 수 없습니다.
- 주의점: 기본 singleton은 Bean 정의당, 컨테이너당 하나이며 자동으로 스레드 안전해지지 않습니다. prototype은 초기화까지 관리하지만 종료 콜백은 관리하지 않습니다.
- 현재 적용: Controller, Service, Repository, PostIdGenerator는 Bean입니다. 직접 생성하는 Post와 요청 DTO는 현재 Bean으로 등록하지 않았습니다.
