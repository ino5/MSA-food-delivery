package mall.domain;

import mall.domain.DeliveryStarted;
import mall.DeliveryApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;


@Entity
@Table(name="Test_table")
@Data

public class Test  {


    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Long id;

    @PostPersist
    public void onPostPersist(){


        DeliveryStarted deliveryStarted = new DeliveryStarted(this);
        deliveryStarted.publishAfterCommit();

    }

    public static TestRepository repository(){
        TestRepository testRepository = DeliveryApplication.applicationContext.getBean(TestRepository.class);
        return testRepository;
    }




    public static void startDelivery(OrderPlaced orderPlaced){

        /** Example 1:  new item 
        Test test = new Test();
        repository().save(test);

        */

        /** Example 2:  finding and process
        
        repository().findById(orderPlaced.get???()).ifPresent(test->{
            
            test // do something
            repository().save(test);


         });
        */

        
    }


}
