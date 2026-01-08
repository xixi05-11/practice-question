package com.jie.practicequestions.config;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Configuration
public class JacksonConfig {
    
    @Bean
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        return new JsonMapperBuilderCustomizer() {
            @Override
            public void customize(JsonMapper.@NonNull Builder jsonMapperBuilder) {
                jsonMapperBuilder.addModule(module);
            }
        };
    }
}