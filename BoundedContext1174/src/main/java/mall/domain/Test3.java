package mall.domain;

import mall.domain.Test4;
import mall.BoundedContext1174Application;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;


@Entity
@Table(name="Test3_table")
@Data

public class Test3  {


    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Long id;

    @PostPersist
    public void onPostPersist(){


        Test4 test4 = new Test4(this);
        test4.publishAfterCommit();

    }

    public static Test3Repository repository(){
        Test3Repository test3Repository = BoundedContext1174Application.applicationContext.getBean(Test3Repository.class);
        return test3Repository;
    }




    public static void test2(OrderPlaced orderPlaced){

        /** Example 1:  new item 
        Test3 test3 = new Test3();
        repository().save(test3);

        */

        /** Example 2:  finding and process
        
        repository().findById(orderPlaced.get???()).ifPresent(test3->{
            
            test3 // do something
            repository().save(test3);


         });
        */

        
    }


}
