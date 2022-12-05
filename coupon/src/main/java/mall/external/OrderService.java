package mall.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;


@FeignClient(name = "ordering", url = "${api.url.ordering}")
public interface OrderService {
    @RequestMapping(method= RequestMethod.PATCH, path="/orders")
    public void updateCoupon(@RequestBody Order order);
}
