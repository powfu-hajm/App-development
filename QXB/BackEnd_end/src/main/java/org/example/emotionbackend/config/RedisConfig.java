package org.example.emotionbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 自定义 RedisTemplate，解决默认序列化乱码问题
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //===== key 序列化方式 =====//
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        //===== value 序列化方式 =====//
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        // key采用String序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);

        // hash 的 key 也采用String
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // value采用JSON序列化方式
        redisTemplate.setValueSerializer(jsonRedisSerializer);

        // hash 的 value也采用JSON序列化方式
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
