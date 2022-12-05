package mall.external;

import lombok.Data;
import java.util.Date;
@Data
public class Order {

    private Long id;
    private String productId;
    private Object options;
    private String addres;
    private String customerId;
    private String storeId;
}


