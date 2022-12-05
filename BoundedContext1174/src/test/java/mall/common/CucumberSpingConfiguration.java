package mall.common;


import mall.BoundedContext1174Application;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = { BoundedContext1174Application.class })
public class CucumberSpingConfiguration {
    
}
