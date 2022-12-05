package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class Test4 extends AbstractEvent {

    private Long id;

    public Test4(Test3 aggregate){
        super(aggregate);
    }
    public Test4(){
        super();
    }
}
