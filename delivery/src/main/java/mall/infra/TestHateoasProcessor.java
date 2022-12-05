package mall.infra;
import mall.domain.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.EntityModel;

@Component
public class TestHateoasProcessor implements RepresentationModelProcessor<EntityModel<Test>>  {

    @Override
    public EntityModel<Test> process(EntityModel<Test> model) {

        
        return model;
    }
    
}
