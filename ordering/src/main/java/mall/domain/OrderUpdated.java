package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class OrderUpdated extends AbstractEvent {

    private Long id;
    private String productId;
    private List<String> options;
    private String addres;
    private String customerId;
    private String storeId;
    private String couponId;

    public OrderUpdated(Order aggregate){
        super(aggregate);
    }
    public OrderUpdated(){
        super();
    }
}
