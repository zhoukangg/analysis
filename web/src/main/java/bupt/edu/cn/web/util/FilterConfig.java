package bupt.edu.cn.web.util;

import com.peaceful.auth.sdk.Impl.AuthServiceImpl;
import com.peaceful.auth.sdk.api.AuthService;
import com.peaceful.common.util.HttpContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean httpContextFilter(){
        //创建 过滤器注册bean
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        HttpContextFilter filter = new HttpContextFilter();

        registrationBean.setFilter(filter);

        List urls = new ArrayList();
        urls.add("/*");   //给所有请求加过滤器
        //设置 有效url
        registrationBean.setUrlPatterns(urls);

        return registrationBean;
    }
}