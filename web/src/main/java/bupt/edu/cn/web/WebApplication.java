package bupt.edu.cn.web;

//import bupt.edu.cn.web.util.realtime.SocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"bupt.edu.cn"})
public class WebApplication {

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	private static ApplicationContext applicationContext;

	public static void main(String[] args) {
		ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(WebApplication.class, args);
		WebApplication.applicationContext = configurableApplicationContext;
	}
}
