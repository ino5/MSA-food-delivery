package mall.domain;

import mall.domain.OrderPlaced;
import mall.domain.OrderCanceled;
import mall.domain.OrderUpdated;
import mall.OrderingApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;


@Entity
@Table(name="Order_table")
@Data

public class Order  {


    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Long id;
    
    
    
    
    
    private String productId;
    
    
    
    @ElementCollection
    
    private List<String> options;
    
    
    
    
    
    private String addres;
    
    
    
    
    
    private String customerId;
    
    
    
    
    
    private String storeId;
    
    
    
    
    
    private String couponId;

    @PostPersist
    public void onPostPersist(){


        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publishAfterCommit();

        // OrderCanceled orderCanceled = new OrderCanceled(this);
        // orderCanceled.publishAfterCommit();


        OrderUpdated orderUpdated = new OrderUpdated(this);
        orderUpdated.publishAfterCommit();

    }
    @PreUpdate
    public void onPreUpdate(){
    }

    /**
        주문이 취소될 때 보상 처리
     */
    @PreRemove
    public void onPreRemove(){
        // 주문 취소 publish
        OrderCanceled orderCanceled = new OrderCanceled(this);
        orderCanceled.publishAfterCommit();
    }

    public static OrderRepository repository(){
        OrderRepository orderRepository = OrderingApplication.applicationContext.getBean(OrderRepository.class);
        return orderRepository;
    }

    /**
        주문에 쿠폰 적용하기
        @param updateCouponCommand
     */
    public void updateCoupon(UpdateCouponCommand updateCouponCommand) {
        String couponId = updateCouponCommand.getCouponId();
        this.setCouponId(couponId);
        repository().save(this);
    }




}
