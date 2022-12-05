package mall.common;


import mall.고객센터Application;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = { 고객센터Application.class })
public class CucumberSpingConfiguration {
    
}
