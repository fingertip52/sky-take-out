package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AliOssUtil配置类
 * 这个配置类 AliOssConfiguration 的作用是将阿里云 OSS（对象存储服务）的工具类 AliOssUtil
 * 注册为 Spring 容器中的 Bean，以便在项目里可以通过依赖注入的方式使用它
 */

//@Configuration 注解，该类会被 Spring 视为配置类，其主要功能是定义和注册 Bean。
@Configuration
@Slf4j
public class AliOssConfiguration {

    //交给spring容器管理
    @Bean
    //当没有该bean的时候才会创建，保证容器中只有一个bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始创建阿里云文件上传工具类对象：{}", aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
