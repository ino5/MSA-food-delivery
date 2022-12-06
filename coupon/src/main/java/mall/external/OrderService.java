package mall.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;


@FeignClient(name = "ordering", url = "${api.url.ordering}")
public interface OrderService {
    /**
        쿠폰을 사용할 때 주문에 반영하기 위해  ordering context에 호출
     */
    @RequestMapping(method= RequestMethod.PATCH, path="/orders/{id}/updatecoupon)
    public void updateCoupon(@RequestBody Order order);
}
