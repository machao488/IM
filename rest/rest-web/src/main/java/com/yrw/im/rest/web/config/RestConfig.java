package com.yrw.im.rest.web.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.yrw.im.common.domain.constant.MqConstant;
import com.yrw.im.common.domain.po.DbModel;
import com.yrw.im.rest.web.handler.ValidHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import javax.validation.Validator;

/**
 * Date: 2019-04-21
 * Time: 15:08
 *
 * @author yrw
 */
@Configuration
@MapperScan(value = "com.yrw.im.rest.web.mapper")
@ComponentScan(basePackages = "com.yrw.im.rest.web.service")
public class RestConfig {

    @Bean
    @Primary
    public MybatisPlusProperties mybatisPlusProperties() {
        MybatisPlusProperties properties = new MybatisPlusProperties();
        GlobalConfig globalConfig = new GlobalConfig();

        properties.setTypeAliasesSuperType(DbModel.class);
        properties.setMapperLocations(new String[]{"classpath*:/mapper/**/*.xml"});
        properties.setGlobalConfig(globalConfig);

        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setTablePrefix("im_");
        globalConfig.setDbConfig(dbConfig);

        return properties;
    }

    @Bean
    public Integer init(Validator validator, RedisTemplate<String, String> redisTemplate) {
        ValidHandler.setValidator(validator);
        return 1;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory listenerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    @Bean
    public Queue offlineQueue() {
        return new Queue(MqConstant.OFFLINE_QUEUE);
    }

    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplateString
        (ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveRedisTemplate<>(connectionFactory, RedisSerializationContext.string());
    }
}
