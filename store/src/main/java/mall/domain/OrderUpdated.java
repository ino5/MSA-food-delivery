package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class OrderUpdated extends AbstractEvent {

    private Long id;
    private String status;
    private String foodId;
    private String orderId;
    private String options;
    private String storeId;
    private String isCanceled;

    public OrderUpdated(FoodCooking aggregate){
        super(aggregate);
    }
    public OrderUpdated(){
        super();
    }
}
