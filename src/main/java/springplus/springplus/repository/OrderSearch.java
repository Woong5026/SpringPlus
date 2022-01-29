package springplus.springplus.repository;

import lombok.Getter;
import lombok.Setter;
import springplus.springplus.domain.Order;
import springplus.springplus.domain.OrderStatus;

@Getter @Setter
public class OrderSearch {

    private String memberNames;
    private OrderStatus orderStatus;
}
