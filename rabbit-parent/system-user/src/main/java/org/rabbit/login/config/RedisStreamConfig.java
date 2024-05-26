package org.rabbit.login.config;

import lombok.extern.slf4j.Slf4j;
import org.rabbit.login.models.MessageQueueRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Lyle
 */
@Slf4j
@Configuration
public class RedisStreamConfig {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MQConfiguration mqConfiguration;

    private ThreadPoolExecutor threadPoolExecutor() {
        AtomicInteger index = new AtomicInteger(1);
        int processors = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(processors, processors, 0, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), r -> {
            Thread thread = new Thread(r);
            thread.setName("redis-stream-" + index.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        });
        return executor;
    }

    private StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, MessageQueueRequestDTO>> getOptions() {
        return
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        // 一次最多获取多少条消息
                        .batchSize(10)
                        // 运行 Stream 的 poll task
                        .executor(threadPoolExecutor())
                        // 可以理解为 Stream Key 的序列化方式
                        .keySerializer(RedisSerializer.string())
                        // 可以理解为 Stream 后方的字段的 key 的序列化方式
                        .hashKeySerializer(RedisSerializer.string())
                        // 可以理解为 Stream 后方的字段的 value 的序列化方式
                        .hashValueSerializer(RedisSerializer.string())
                        // Stream 中没有消息时，阻塞多长时间，需要比 `spring.redis.timeout` 的时间小
                        .pollTimeout(Duration.ofSeconds(1))
                        // ObjectRecord 时，将 对象的 filed 和 value 转换成一个 Map 比如：将Book对象转换成map
                        .objectMapper(new ObjectHashMapper())
                        // 获取消息的过程或获取到消息给具体的消息者处理的过程中，发生了异常的处理
                        //.errorHandler(new ConsumeErrorHandler())
                        // 将发送到Stream中的Record转换成ObjectRecord，转换成具体的类型是这个地方指定的类型
                        .targetType(MessageQueueRequestDTO.class)
                        .build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public StreamMessageListenerContainer<String, ObjectRecord<String, MessageQueueRequestDTO>> streamMessageListenerContainer(LettuceConnectionFactory redisConnectionFactory) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String,
                MessageQueueRequestDTO>> options = getOptions();

        StreamMessageListenerContainer<String, ObjectRecord<String, MessageQueueRequestDTO>> listenerContainer = StreamMessageListenerContainer
                .create(redisConnectionFactory, options);

        if (null == mqConfiguration) {
            log.warn("Missing ConsumerStreamMessageListeners of message queue, if necessary, please add the " +
                    "corresponding configuration");
            return listenerContainer;
        }
        return listenerContainer;
    }
}
