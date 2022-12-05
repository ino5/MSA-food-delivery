package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class CouponCreated extends AbstractEvent {

    private Long id;
    private String orderId;
    private String discountPrice;
    private String isUsed;

    public CouponCreated(Coupon aggregate){
        super(aggregate);
    }
    public CouponCreated(){
        super();
    }
}
