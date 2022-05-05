package com.wangqi.config;

import com.wangqi.config.Interceptor.LoginInterceptor1;
import com.wangqi.config.Interceptor.LoginInterceptor2;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class LoginConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册TestInterceptor拦截商家管理页面
        InterceptorRegistration registration1 = registry.addInterceptor(new LoginInterceptor1());
        registration1.addPathPatterns("/pages/store/**");      //拦截商家管理页面
        registration1.excludePathPatterns();
        //拦截器2  拦截管理员页面
        InterceptorRegistration registration2= registry.addInterceptor(new LoginInterceptor2());
        registration2.addPathPatterns("/pages/admin/**");//只拦截管理员页面
        registration2.excludePathPatterns();
    }
}
