package mall.infra;
import mall.domain.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.EntityModel;

@Component
public class Test3HateoasProcessor implements RepresentationModelProcessor<EntityModel<Test3>>  {

    @Override
    public EntityModel<Test3> process(EntityModel<Test3> model) {

        
        return model;
    }
    
}
