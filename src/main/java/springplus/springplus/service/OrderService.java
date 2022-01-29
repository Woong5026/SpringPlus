package springplus.springplus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springplus.springplus.domain.Delivery;
import springplus.springplus.domain.Item.Item;
import springplus.springplus.domain.Member;
import springplus.springplus.domain.Order;
import springplus.springplus.domain.OrderItem;
import springplus.springplus.repository.ItemRepository;
import springplus.springplus.repository.MemberRepository;
import springplus.springplus.repository.OrderRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문
    public Long order(Long memberId, Long itemId, int count){
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성(회원 주소 넣기)
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);
        return order.getId();


    }
    // 주문 취소
    @Transactional
    public void cancleOrder(Long orderId){
        Order order = orderRepository.findOne(orderId);
        order.cancle();
    }

    // 검색
//    public List<Order> findOrders(OrderSearch orderSearch){
//        return orderRepository.findAll(orderSearch);
//    }
}