package bupt.edu.cn.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"bupt.edu.cn","com.cn.bupt.cad.bigdataroles"})
public class WebApplication extends SpringBootServletInitializer{
	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//		return builder.sources(WebApplication.class);
//	}

}
