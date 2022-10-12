앞서 개발한 웹 애플리케이션은 타임리프 뷰 템플릿을 사용한 서버 사이드 렌더링 기법이다.  <br/>
즉, 서버 측에서 화면을 렌더링해서 클라이언트에 보내준다.

 

서버 사이드 렌더링 기법은 백엔드 개발자라면 반드시 익혀야 한다. 그러나 이번에는 Rest API 기법을 알아볼 것이다. <br/>
Rest API는 서버 측에서는 화면을 렌더링하는 것이 아닌 화면을 구성하는데 필요한 데이터(JSON 등)를 <br/>
클라이언트로 전송하고 화면을 렌더링하는 역할은 클라이언트에 맡기는 것이다. 즉, 클라이언트 사이드 렌더링을 하는 것이다.

 

최근에는 이처럼 프론트, 백 개발자가 협업하는 Rest API 기법이 많이 사용되기 때문에 역시 필수적으로 알아둬야 하는 기법이다. <br/>
지금부터 API 통신에 대해서 알아보자.

<br/>

---

<br/>


* @RestController : @Controller + @ResponsBody가 합쳐진 것
  * @ResponsBody란? 데이터를 제이슨이나 xml로 바로 보낼때 사용

회원 등록을 API 방식으로 처리해보자.

* 기존 방식
1. 클라이언트(HTML Form을 통해 요청 파라미터 전송)
2. 서버(회원 등록 후 화면 렌더링해서 전송)
3. 클라이언트(받은 화면 정보를 그대로 출력)

* API 방식
1. 클라이언트(회원 정보를 json 형식으로 전송)
2. 서버(회원 등록 후 등록된 회원 정보를 json으로 전송)

- MemberApiController 

+) 기존 뷰 템플릿 컨트롤러와 API 전용 컨트롤러는 다른 패키지에 관리하는 것이 좋다. (공통 처리 관점)

```java

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}

```

<@RestController>

@RestController는 @Controller에 @ResponseBody를 더한 애노테이션이라고 보면 된다.<br/>
반환한 객체를 일정 형식(여기서는 json)에 맞춰 HTTP 메시지 바디에 등록해 클라이언트에 전송한다.

 

<@RequestBody>

클라이언트에서 json 형식으로 데이터가 들어왔다고 가정한다.<br/>
json 형식의 데이터를 @RequetBody가 알아서 해당 객체의 프로퍼티를 확인하여 알맞은 필드에 데이터 값을 저장해준다.

 
<@Valid>

요청 데이터를 받을 때 발생하는 문제 처리를 위한 애노테이션이다. 여기서는 회원 이름 중복 문제를 처리할 것이다. <br/>
Member 엔티티 name 필드에 @NotEmpty를 추가하면 회원 이름 중복 시 오류를 출력해준다.

<CreateMemberResponse>

리턴용 객체이다. 객체에 데이터를 저장하고 리턴해주면 알아서 json 형식으로 변경해 클라이언트에 보내준다.

---
  
<v1 방식(엔티티를 Request Body에 직접 매핑)의 문제점>

1. 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty)

-> 화면 렌더링 계층에서 필요한 로직이 핵심 엔티티에 포함되면 안된다. 엔티티는 핵심 비즈니스 로직만을 담고 있어야 한다.

2. 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 모든 요청 요구사항을 담기는 어렵다.

-> 엔티티가 API에 의존할 수 있다.

3. 엔티티가 변경되면 API 스펙이 변한다.(엔티티를 파마메터로 받지도 말고 외부로 노출시키지도 말자)

-> 만약 회원 엔티티의 필드 name을 username으로 변경했다면 API 요청 데이터 역시 "name":"hello"가 아닌 "username":"hello"가 되야 한다.<br/>
  엔티티 변경에 따라 API도 변해야 하는 번거로움이 있다.

> 결론 : 엔티티와 API가 변경에 대해서 서로 영향을 주지 않는 것이 좋다. -> API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받자.
  
  ---
  
  - MemberApiController.java - v2(DTO 방식) 추가
  
  ```java
  
  @PostMapping("/api/v2/members")
public CreateMemberResponse saveMemberV2(@RequestBody @Valid **CreateMemberRequest** request) {

    Member member = new Member();
    member.setName(request.getName());
    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
}

@Data
static class **CreateMemberRequest** {
    @NotEmpty
    private String name;
}
  
  ```
  
  CreateMemberRequest를 Member 엔티티 대신에 RequestBody와 매핑한다.
  
  1. 엔티티와 화면 렌더링 계층을 위한 로직을 분리할 수 있다.

-> @NotEmpty를 DTO에 붙일 수 있다

2. 엔티티와 API 스펙을 명확하게 분리할 수 있다.

-> Member 엔티티에 대한 다양한 API에 대해서도 전용 DTO를 만들면 되기 때문에 엔티티를 수정할 일이 없다.

3. 엔티티가 변해도 API 스펙이 변하지 않는다.

-> Member 엔티티의 name을 username으로 변경해도 DTO에서 name으로 받고 Member의 username으로 넘겨주면 된다. <br/>
-> 즉, 중간 처리 과정이 있기 때문에 엔티티는 엔티티대로 자유롭게 변경해도 된다. <br/>
-> 만약 이름을 변경할 경우가 생긴다면 dto의 이름을 바꾸면 된다(대신 포스트맨에서는 dto의 값으로 조회해야 한다)   
 
```java

 @PostMapping("/api/v2/membersCheck")
    public CreateMemberResponse saveMemberV9(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setUsername(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
 
```

3번의 API 스펙이 변하지 않는다를 검증하기 위해 새로운 username이라는 필드를 멤버 엔티티에 생성하고 <br/>
CreateMemberRequest의 name과 매핑한 결과 잘 동작되었다.  <br/>
즉, 이는 dto에서 name으로 받아 클라이언트로 넘겨주기 때문에 엔티티의 필드를 변경해도 상관없다는 것이 검증되었다 
 
<br/>
 
 ---
 
 <br/>
  
<정리>

다양한 종류의 데이터를 담고 있는 여러 API들을 각각에 알맞은 DTO에 담고 컨트롤러에서 DTO 데이터를 알맞게 엔티티에 전달하는 방식을 사용해야 한다.
 
<br/> 
 
+) 참고 : 이너 클래스

예제에서는 DTO를 이너 클래스로 사용했다. 물론 따로 파일을 만들어서 DTO를 관리해도 좋다. <br/>
따로 파일을 만들게 되면 다른 컨트롤러에서도 자유롭게 사용할 수 있다.  <br/>
이너 클래스의 경우 해당 클래스 안에서만 한정적으로 사용한다는 의미를 부여할 수 있다.

또한 이너 클래스는 항상 static 클래스를 사용해야 한다. <br/>
CreateMemberRequest의 경우처럼 외부에서 생성하는 경우 static 클래스가 아니면 생성할 수 없기 때문이다.  <br/>
(이 밖에 여러 이점들이 있으니 꼭 static을 사용하자) 
 
 <br/>
