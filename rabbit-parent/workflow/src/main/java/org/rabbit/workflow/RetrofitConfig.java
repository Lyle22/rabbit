package org.rabbit.workflow;

import lombok.NonNull;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Bean configuration for Retrofit2
 *
 * @author weltuser
 */
@Configuration
public class RetrofitConfig {
//
//    @Value("${docpal.api.docker.host.url}")
//    private String docpalApiServer;

//    @Bean
//    public DocpalAPIService docpalAPIService() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(docpalApiServer)
//                .addConverterFactory(NullOnEmptyConverterFactory.create())
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(
//                        JacksonConverterFactory.create(
//                                (new ObjectMapper()).registerModule(new JavaTimeModule())
//                        )
//
//                )
//                .client(genericClient("docpal-workflow"))
//                .build();
//        return retrofit.create(DocpalAPIService.class);
//    }

    public OkHttpClient genericClient(@NonNull String serverName) {
        // 设置长链接
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.MINUTES)
                .writeTimeout(0, TimeUnit.SECONDS);

        int cpuCores = Runtime.getRuntime().availableProcessors(); // 获取机器的CPU核心数
        int maxIdleConnections = cpuCores * 2; // 最大空闲连接数为CPU核心数的两倍
        long keepAliveDuration = 5; // 连接的最大空闲时间为5分钟
        ConnectionPool connectionPool = new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MINUTES);

        httpClient.connectionPool(connectionPool);

        httpClient.addInterceptor(chain -> {
            Request oldRequest = chain.request();
            Request request = oldRequest.newBuilder()
//                    .header("ServerName", serverName)
//                    .header("ServerKey", registeredServers.getKey().get(serverName))
                    .build();
            return chain.proceed(request);
        });
        return httpClient.build();
    }
}