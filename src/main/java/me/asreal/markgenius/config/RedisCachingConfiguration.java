package me.asreal.markgenius.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;

@Configuration
public class RedisCachingConfiguration {

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        //Store different cache configs
        var redisCacheConfigs = new HashMap<String, RedisCacheConfiguration>();
        //Create default cache config
        var redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(5))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        //Set different cache instances (In cases where the default is not preferred)
        redisCacheConfigs.put("PAPER_SUMMARIES_CACHE", redisCacheConfiguration
                .entryTtl(Duration.ofHours(5)));
        redisCacheConfigs.put("AI_FEEDBACK_CACHE", redisCacheConfiguration
                .entryTtl(Duration.ofHours(2)));
        redisCacheConfigs.put("USER_ACCOUNT", redisCacheConfiguration
                .entryTtl(Duration.ofMinutes(30)));
        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(redisCacheConfigs)
                .build();
    }

}
