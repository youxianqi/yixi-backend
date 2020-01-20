package youxianqi.yixi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class App
{
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        try {
            ApplicationContext springAppContext = SpringApplication.run(App.class);
            springAppContext.getBean(MainService.class).init();
            springAppContext.getBean(MainService.class).start();

        } catch (Exception e) {
            logger.error(ExceptionUtil.getExceptionStack(e));
        }
    }
}
