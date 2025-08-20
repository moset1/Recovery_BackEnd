package com.solution.demo.configuration;

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class WebConfig {

    @Bean
    public Filter shallowEtagHeaderFilter() {
        // GET, HEAD 요청에 대한 응답을 기반으로 ETag를 생성하고,
        // 다음 요청에 If-None-Match 헤더가 있으면 304 Not Modified를 반환해줍니다.
        return new ShallowEtagHeaderFilter();
    }
}