package springplus.springplus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springplus.springplus.domain.Item.Item;
import springplus.springplus.domain.Member;
import springplus.springplus.domain.Order;
import springplus.springplus.repository.OrderSearch;
import springplus.springplus.service.ItemService;
import springplus.springplus.service.MemberService;
import springplus.springplus.service.OrderService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model){

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItem();

        model.addAttribute("members",members);
        model.addAttribute("items",items);

        return "order/orderForm";
    }
    // submit 후 넘어가는 post
    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count){
        orderService.order(memberId, itemId, count);

        return "redirect:/orders";
    }
    //
    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch")OrderSearch orderSearch, Model model){
        List<Order> orders = orderService.findOrders(orderSearch);

        model.addAttribute("orders",orders);
        return "order/orderList";
    }
    // 취소
    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancleOrder(orderId);
        return "redirect:/orders";
    }
}
