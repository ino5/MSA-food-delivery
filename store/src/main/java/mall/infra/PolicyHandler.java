package mall.infra;

import javax.naming.NameParser;

import javax.naming.NameParser;
import javax.transaction.Transactional;

import mall.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import mall.domain.*;

@Service
@Transactional
public class PolicyHandler{
    @Autowired FoodCookingRepository foodCookingRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderPlaced'")
    public void wheneverOrderPlaced_CopyOrderInfo(@Payload OrderPlaced orderPlaced){

        OrderPlaced event = orderPlaced;
        System.out.println("\n\n##### listener CopyOrderInfo : " + orderPlaced + "\n\n");


        

        // Sample Logic //
        FoodCooking.copyOrderInfo(event);
        

        

    }

    /**
     */
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='Paid'")
    public void wheneverPaid_UpdateStatus(@Payload Paid paid){
        Paid event = paid;
        System.out.println("\n\n##### listener UpdateStatus : " + paid + "\n\n");

        // Sample Logic //
        FoodCooking.updateStatus(event);
    }
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderCanceled'")
    public void wheneverOrderCanceled_UpdateStatus(@Payload OrderCanceled orderCanceled){

        OrderCanceled event = orderCanceled;
        System.out.println("\n\n##### listener UpdateStatus : " + orderCanceled + "\n\n");


        

        // Sample Logic //
        FoodCooking.updateStatus(event);
        

        

    }

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='OrderUpdated'")
    public void wheneverOrderUpdated_UpdateOrder(@Payload OrderUpdated orderUpdated){

        OrderUpdated event = orderUpdated;
        System.out.println("\n\n##### listener UpdateOrder : " + orderUpdated + "\n\n");


        

        // Sample Logic //

        

    }

}


