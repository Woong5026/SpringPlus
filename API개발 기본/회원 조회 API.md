- MemberApiController.java - 회원 조회 API v1 추가

```java

@GetMapping("/api/v1/members")
public List<Member> membersV1() {
    return memberService.findMembers();
}

```
v1 : 응답 값으로 엔티티를 직접 외부에 노출

* 문제점

1. 응답 스펙을 맞추기 위해 로직이 추가된다. <br/>
-> 만약 조회시 Member와 연관관계인 Order를 노출하고 싶지 않다면 Order 필드에 @JsonIgnore 로직을 추가해야 한다.

2. 실무에서는 같은 엔티티에 대해 API가 용도에 따라 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 응답 로직을 담기는 어렵다. <br/>
-> 어떤 API는 Order를 노출하고 싶다면? -> 복잡해진다.

3. 컬렉션을 직접 반환하면 향후 API 스펙을 변경하기 어렵다. <br/>
-> List<Member>를 그대로 반환하면 다음과 같이 [ ]로 둘러싸인 배열 형태로 반환된다.
  
```java

  [
    {
        "id": 1,
        "name": "member1",
        "address": {
            "city": "Seoul",
            "street": "123",
            "zipcode": "12345"
        },
        "orders": []
    },
    
    ...
]
  
```
  
이런 형태는 [ ]로 스펙이 굳어버려 확장을 할 수 없다. 만약 컬렉션 크기인 count를 데이터에 추가하고 싶다면 위 경우 불가능하다. <br/>
또한, 엔티티 전부가 아닌 필요한 정보만을 반환하는 경우가 많기 때문에 별도 DTO를 사용하는 것이 맞다.
    
```java
  "count" : 4  
  "data": [ // List를 그대로 반환하면 []으로 스펙이 굳기때문에 data라는 필드에 []값을 넣어 굳히면 
            // 나중에 새로운 필드를 추가할때도 List<Member> 는 건드리지 않고 필드만 dto에 추가하면 된다
    {
        "id": 1,
        "name": "member1",
        "address": {
            "city": "Seoul",
            "street": "123",
            "zipcode": "12345"
        },
        "orders": []
    },
    
    ...
]
  
```    
    
<br/>
  
  ---
 
<br/>    
    
  * MemberApiController.java - 회원 조회 API v2 추가
  
  ```java
  
  @GetMapping("/api/v2/members")
public Result memberV2() { // 응답값 자체를 껍데기 클래스
    List<Member> findMembers = memberService.findMembers();
    List<MemberDto> collect = findMembers.stream()
    .map(m -> new MemberDto(m.getName()))
    .collect(Collectors.toList());

    return new Result(collect);
}

@Data
@AllArgsConstructor
static class Result<T> { // 위에서 Result(collect)를 반환했으니 
    private T data; // 이제 data필드의 값은 리스트가 반환된다
}

@Data
@AllArgsConstructor
static class MemberDto {
    private String name;
}
  
  ```
  
  엔티티의 name만을 반환한다고 하자.
  
<MemberDto  <br/>
엔티티를 DTO로 변환해서 반환한다. (List<Member> -> List<MemberDto>)   <br/>
노출할 정보만 MemberDto에 만들면 된다, API스펙이 MemberDto와 1대1 이 된다
    
<Result<T>> <br/>
이제 List<MemberDto>에는 보내고자 하는 데이터가 들어가 있다. <br/>
그러나 앞서 말한 컬렉션을 직접 반환하면 이후에 확장하기 어렵다는 문제점으로 인해 추가로 Result 클래스로 컬렉션을 감싸서 반환해준다.

<br/>    
    
 +) Result 클래스를 만들어서 response 데이터를 보내고 이때 제너릭으로 설정한 이유?
    
현재 코드

```java
    
static class Result<T> {

private T data;

}
    
```
        
그런데 제너릭을 쓰지 않는 반환용 DTO를 만들어서
    
```java
    
static class ResultDto{

private List<OrderDto> orderDtos

}
    
```
    
이렇게 하지 않는 이유가 궁금했다    

+) Answer
    
Result 객체가 생기기 전에는 MemberDto, OrderDto 등 각각 서로 다른 타입으로 반환하고 있다. <br/>
MemberDto는 멤버 정보를 담고 있는 객체이고, OrderDto는 주문 정보를 담고 있는 객체

예를들어, 응답 데이터로 특정 도메인의 정보(Member, Order 등)뿐만 아니라 응답 상태 코드를 추가적으로 나타내라는 요구사항이 추가된다면 <br/>
MemberDto, OrderDto는 응답 상태 코드를 가지기에는 객체의 정체성과 맞지 않다. 응답 상태 코드는 멤버 정보도 아니고, 주문 정보도 아니기 때문

따라서 API 컨트롤러의 응답을 추상화한 Result라는 클래스를 도출

Result는 응답을 추상화했기 때문에 사용자가 요청한 데이터(MemberDto, OrderDto)도 담아야 하고 <br/>
응답 그 자체에 대한 데이터(StatusCode)도 담을 수 있어야 한다.

이런 맥락에서 Result 객체의 핵심인 사용자가 요청한 데이터를 받는 data 필드가 생기게 되고, 추가 요구사항이었던 statusCode도 추가 가능 <br/>
이때 사용자가 요청한 데이터가 어떤 타입이든 Result 객체에서 받을 수 있으려면 제네릭을 사용하여 명시한 타입을 data의 타입으로 사용할 수 있게 하는 것

실무에서 어떤 것을 선호한다기 보다, 제네릭이 필요한 상황이 있다면 제네릭을 사용

    
<br/>    
    
* 결과
    
```java
    
    {    
    "data": [
        {
            "name": "member1"
        },
        {
            "name": "member2"
        }
    ]
}
    
```
    
이처럼 Result로 감쌌기 때문에 [ ]로 닫힌 형태가 아닌 { [ ] } 형태이기 때문에 만약 count 값을 추가한다고 하면
    
<br/>    
    
```java
    
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }
    
    ```
    
    ```java
    
        {
        "count" : 4

        "data": [
            {
                "name": "member1"
            },
            {
                "name": "member2"
            }
        ]
    }
    
```
    
이렇게 쉽게 추가할 수 있다.
    
만약 간단한 정보를 조회하는 칼럼하나가 필요하다면 위와 같이 count필드를 추가하면 되고 <br/>
List로 스펙이 굳어버리면 해당정보가 필요할때 엔티티에 추가해야 하는데 엔티티는 한 곳에서만 쓰이는 것이 아닌 여러 곳에서 사용되기에 <br/>
하나의 메소드에서만 필드정보가 필요하다면 컬렉션 안에 넣어주면 엔티티 변경없이 추가 정보를 조회할 수 있다    
 
>  API 개발은 DTO가 필수!
