package br.com.project.springboot.starter.template.api.configs;

import br.com.project.springboot.starter.template.api.filters.AuthFilter;
import br.com.project.springboot.starter.template.api.filters.LogFilter;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterRegistrationConfig {
    private final AuthFilter authFilter;
    private final LogFilter logFilter;

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration() {
        return disabled(authFilter);
    }

    @Bean
    public FilterRegistrationBean<LogFilter> logFilterRegistration() {
        return disabled(logFilter);
    }

    private <T extends Filter> FilterRegistrationBean<T> disabled(T filter) {
        FilterRegistrationBean<T> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);

        return registration;
    }
}
