package mall.infra;
import mall.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;


@RestController
// @RequestMapping(value="/orders")
@Transactional
public class OrderController {

    @Autowired
    OrderRepository orderRepository;


    /**
        주문에 쿠폰 적용
     */
    @RequestMapping(
        value = "orders/{id}/updatecoupon",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Order updateCoupon(
        @PathVariable(value = "id") Long id,
        @RequestBody UpdateCouponCommand updateCouponCommand,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /order/updateCoupon  called #####");
        Optional<Order> optionalOrder = orderRepository.findById(id);

        optionalOrder.orElseThrow(() -> new Exception("No Entity Found"));
        Order order = optionalOrder.get();

        // 주문에 쿠폰 적용
        order.updateCoupon(updateCouponCommand);

        orderRepository.save(order);
        return order;
    }
}