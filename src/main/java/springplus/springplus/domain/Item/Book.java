package springplus.springplus.domain.Item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
// ITem 클래스에서 B 라는 값으로 구분되겠다
@DiscriminatorValue("B")
public class Book extends Item{

    private String author;
    private String isbn;


}
