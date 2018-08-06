package com.arcvideo.pgcliveplatformserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

@Configuration
public class HttpConfig {
//    private static final int CONNECT_TIMEOUT = 2 * 1000;
//    private static final int READ_TIMEOUT = 30 * 1000;

//    @Bean
//    public CloseableHttpAsyncClient asyncHttpClient() {
//        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSoTimeout(READ_TIMEOUT).build();
//        return  HttpAsyncClients.custom().setDefaultIOReactorConfig(ioReactorConfig).build();
//    }
//
//    @Bean
//    public ClientHttpRequestFactory httpRequestFactory() {
//        return new HttpComponentsClientHttpRequestFactory(httpClient());
//    }
//
//    @Bean
//    public CloseableHttpClient httpClient() {
//        RequestConfig config = RequestConfig.custom()
//                .setSocketTimeout(READ_TIMEOUT)
//                .setConnectTimeout(CONNECT_TIMEOUT).build();
//
//        CloseableHttpClient defaultHttpClient = HttpClientBuilder.create()
//                .setDefaultRequestConfig(config).build();
//        return defaultHttpClient;
//    }

    @Bean
    @ConfigurationProperties(prefix = "custom.rest.connection")
    public HttpComponentsClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory();
    }

    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : messageConverters) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter)converter).setDefaultCharset(Charset.forName("UTF-8"));
                ((StringHttpMessageConverter)converter).setWriteAcceptCharset(false);
            }
        }
        return restTemplate;
    }
}
