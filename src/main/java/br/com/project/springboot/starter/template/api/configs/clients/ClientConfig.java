package br.com.project.springboot.starter.template.api.configs.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {
    private final ObjectMapper objectMapper;

    @Bean
    public ClientErrorDecoderConfig errorDecoder() {
        return new ClientErrorDecoderConfig(objectMapper);
    }
}
