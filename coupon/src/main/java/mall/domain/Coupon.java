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
    
    
    
    
    
    private String isUsed;

    @PostPersist
    public void onPostPersist(){


        CouponCreated couponCreated = new CouponCreated(this);
        couponCreated.publishAfterCommit();


        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.


        mall.external.Order order = new mall.external.Order();
        // mappings goes here
        CouponApplication.applicationContext.getBean(mall.external.OrderService.class)
            .updateCoupon(order);


        CouponUsed couponUsed = new CouponUsed(this);
        couponUsed.publishAfterCommit();

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
