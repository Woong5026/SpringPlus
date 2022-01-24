package springplus.springplus.domain.Item;

import lombok.Getter;
import lombok.Setter;
import springplus.springplus.domain.Category;
import springplus.springplus.exception.NotEnoughStockException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
// 상속한 테이블을 한 테이블에 모든 컬럼값을 다 넣는것
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// 상속 테이블들을 구분할 수 있는 것(상속관계 매핑)
@DiscriminatorColumn(name = "dtype")
public class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // 비즈니스 로직

    // 재고수량 증가 로직
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }
    // 재고 감소 로직
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
