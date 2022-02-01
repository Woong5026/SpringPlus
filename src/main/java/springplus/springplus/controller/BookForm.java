package springplus.springplus.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {

    // 상품은 수정이 있으니 id 값이 필요
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    // 여기까지 상품의 공통속성성
    private String author;
    private String isbn;
}
