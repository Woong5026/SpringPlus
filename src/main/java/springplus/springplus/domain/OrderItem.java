package springplus.springplus.domain;

import lombok.Getter;
import lombok.Setter;
import springplus.springplus.domain.Item.Item;

import javax.persistence.*;

@Entity
@Table(name = "order_item")
@Getter @Setter
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;

    private int count;

    // 생성메서드
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        // OrderItem을 생성할때는 기본적으로 재고를 차감해야 한다
        item.removeStock(count);
        return orderItem;
    }

    // 비즈니스 로직
    public void cancle() {
        getItem().addStock(count);
    }
    // 조회 로직
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
