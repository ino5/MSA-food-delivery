package mall.domain;

import mall.domain.CookFinished;
import mall.domain.OrderUpdated;
import mall.StoreApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;


@Entity
@Table(name="FoodCooking_table")
@Data

public class FoodCooking  {


    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Long id;
    
    
    
    
    
    private String status;
    
    
    
    
    
    private String foodId;
    
    
    
    
    
    private String orderId;
    
    
    
    
    
    private String options;
    
    
    
    
    
    private String storeId;
    
    
    
    
    
    private String isCanceled;

    @PostPersist
    public void onPostPersist(){


        CookFinished cookFinished = new CookFinished(this);
        cookFinished.publishAfterCommit();



        OrderUpdated orderUpdated = new OrderUpdated(this);
        orderUpdated.publishAfterCommit();

    }
    @PreUpdate
    public void onPreUpdate(){
    }

    public static FoodCookingRepository repository(){
        FoodCookingRepository foodCookingRepository = StoreApplication.applicationContext.getBean(FoodCookingRepository.class);
        return foodCookingRepository;
    }



    public void accept(AcceptCommand acceptCommand){
        OrderRejected orderRejected = new OrderRejected(this);
        orderRejected.publishAfterCommit();

        OrderAccepted orderAccepted = new OrderAccepted(this);
        orderAccepted.publishAfterCommit();

    }
    public void start(){
        CookStarted cookStarted = new CookStarted(this);
        cookStarted.publishAfterCommit();

    }

    public static void copyOrderInfo(OrderPlaced orderPlaced){

        /** Example 1:  new item 
        FoodCooking foodCooking = new FoodCooking();
        repository().save(foodCooking);

        */

        /** Example 2:  finding and process
        
        repository().findById(orderPlaced.get???()).ifPresent(foodCooking->{
            
            foodCooking // do something
            repository().save(foodCooking);


         });
        */

        
    }

    /**
     *  주문 상태를 "paid"(결제완료) 로 업데이트한다.
     *  @param: paid
     */
    public static void updateStatus(Paid paid){
        repository().findById(paid.getOrderId()).ifPresent(foodCooking -> { // orderId로 찾는다.
            String status = "paid"; // 상태: "paid" (결제완료)
            foodCooking.setStatus(status); // 상태 변경
            repository().save(foodCooking); // 수정
        });

        /** Example 1:  new item 
        FoodCooking foodCooking = new FoodCooking();
        repository().save(foodCooking);

        */

        /** Example 2:  finding and process
        
        repository().findById(paid.get???()).ifPresent(foodCooking->{
            
            foodCooking // do something
            repository().save(foodCooking);


         });
        */ 
    }

    public static void updateStatus(OrderCanceled orderCanceled){

        /** Example 1:  new item 
        FoodCooking foodCooking = new FoodCooking();
        repository().save(foodCooking);

        */

        /** Example 2:  finding and process
        
        repository().findById(orderCanceled.get???()).ifPresent(foodCooking->{
            
            foodCooking // do something
            repository().save(foodCooking);


         });
        */

        
    }


}
