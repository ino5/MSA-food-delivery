package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class CouponUsed extends AbstractEvent {

    private Long id;
    private String orderId;
    private String discountPrice;
    private String isUsed;

    public CouponUsed(Coupon aggregate){
        super(aggregate);
    }
    public CouponUsed(){
        super();
    }

}
