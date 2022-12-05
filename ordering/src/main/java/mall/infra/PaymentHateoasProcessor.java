package mall.infra;
import mall.domain.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.EntityModel;

@Component
public class PaymentHateoasProcessor implements RepresentationModelProcessor<EntityModel<Payment>>  {

    @Override
    public EntityModel<Payment> process(EntityModel<Payment> model) {

        
        return model;
    }
    
}
