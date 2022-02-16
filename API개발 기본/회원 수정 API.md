- MemberApiController.java

```java

@PatchMapping("/api/v2/members/{id}")
public UpdateMemberResponse updateMemberV2(
    @PathVariable("id") Long id,
    @RequestBody @Valid UpdateMemberRequest request) {

    memberService.update(id, request.getName());
    Member findMember = memberService.findOne(id);
    return new UpdateMemberResponse(id, findMember.getName());
}

@Data
static class UpdateMemberRequest {
	private String name;
}

@Data
@AllArgsConstructor
static class UpdateMemberResponse {
    private Long id;
    private String name;
}

```

<회원 수정도 요청, 응답용 DTO를 추가>

+) 참고 : 롬복을 사용할 때, 핵심 엔티티에는 보통 @Getter만 사용하는 것이 좋지만,<br/>
이처럼 단순 데이터 전송용 DTO에는 @Data같은 애노테이션을 자유롭게 사용해도 좋다.

- MemberService.java

```java

@Transactional
public void update(Long id, String name) {
    Member member = memberRepository.findOne(id);
    member.setName(name);
}

```

데이터 수정은 이렇게 영속성 컨텍스트 변경 감지 기능을 사용하는 것이 좋다.


+) update와 find 분리

코드를 보면 변경 감지로 엔티티를 수정한 뒤, 다시 id 값을 통해 엔티티를 조회한다.

```java

memberService.update(id, request.getName());
Member findMember = memberService.findOne(id);

```

update 메서드에서 Member 엔티티를 반환해도 되지만, 이러면 update 메서드가 엔티티를 조회한다는 성격도 갖고 있다.

변경 메서드와 조회 메서드를 명확하게 분리해서, 유지보수를 쉽게 하기 위해 위와 같은 로직을 사용했다.


---

- 정리

```java

@PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

```

putmapping 실행 후 @PathVariable과 @RequestBody에 아이디 값이 담기고 네임 값을 얻는다

그후 update메서드 안에 @Transactional을 통해 영속성 컨텍스트에서 조회 후 id값을 가져온다.

그 후 파라메터로 넘어온 name을 commit이 끝난 시점에서 변경감지를 실행하고 db에 데이터를 날리는 과정
