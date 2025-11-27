package org.yaojiu.supermarket.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.yaojiu.supermarket.interceptor.PermissionInterceptor;

@Configuration(proxyBeanMethods = false)
public class InterceptorConfigure implements WebMvcConfigurer {

    @Autowired
    private PermissionInterceptor permissionInterceptor;

    public static final String[] INTERCEPTOR_EXCLUDE_PATHS = new String[]{
            "/",
            "/auth/login",
            "/auth/reg",
            "/auth/refresh",
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(INTERCEPTOR_EXCLUDE_PATHS);
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("*")
                .maxAge(3600);
    }
}
