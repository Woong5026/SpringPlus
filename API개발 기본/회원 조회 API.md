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
public Result memberV2() {
    List<Member> findMembers = memberService.findMembers();
    List<MemberDto> collect = findMembers.stream()
    .map(m -> new MemberDto(m.getName()))
    .collect(Collectors.toList());

    return new Result(collect);
}

@Data
@AllArgsConstructor
static class Result<T> {
    private T data;
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
>  API 개발은 DTO가 필수!
