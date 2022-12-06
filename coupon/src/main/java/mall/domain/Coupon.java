package mall.domain;

import mall.domain.CouponCreated;
import mall.domain.CouponUsed;
import mall.CouponApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;


@Entity
@Table(name="Coupon_table")
@Data

public class Coupon  {


    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Long id;
    
    
    
    
    
    private String orderId;
    
    
    
    
    
    private String discountPrice;
    
    
    
    
    
    private boolean isUsed;

    @PostPersist
    public void onPostPersist(){
        // CouponCreated couponCreated = new CouponCreated(this);
        // couponCreated.publishAfterCommit();

        // 쿠폰을 사용한다면 
        if (isUsed == true) {

            // ordering context에 동기 호출(req / res)

            //Following code causes dependency to external APIs
            // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.            
            mall.external.Order order = new mall.external.Order().;
            
            // 동기 호출 할 때 coupon의 id와 order의 id 담아 보내기
            order.setCouponId(this.getId()); // coupon id
            order.setOrderId(this.getOrderId()); // order id
            
            // mappings goes here
            CouponApplication.applicationContext.getBean(mall.external.OrderService.class)
                .updateCoupon(order);

            // couponUsed publish
            CouponUsed couponUsed = new CouponUsed(this);
            couponUsed.publishAfterCommit();
        }
    }

    @PreUpdate
    public void onPreUpdate(){
    }

    public static CouponRepository repository(){
        CouponRepository couponRepository = CouponApplication.applicationContext.getBean(CouponRepository.class);
        return couponRepository;
    }




    public static void createCoupon(Delivered delivered){

        /** Example 1:  new item 
        Coupon coupon = new Coupon();
        repository().save(coupon);

        */

        /** Example 2:  finding and process
        
        repository().findById(delivered.get???()).ifPresent(coupon->{
            
            coupon // do something
            repository().save(coupon);


         });
        */

        
    }


}
