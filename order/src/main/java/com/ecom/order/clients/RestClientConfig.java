package com.ecom.order.clients;

//import io.micrometer.observation.ObservationRegistry;
//import io.micrometer.tracing.Tracer;
//import io.micrometer.tracing.propagation.Propagator;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

//    private final ObservationRegistry observationRegistry;   // Collects metrics and data
//    private final Tracer tracer;                             // Captures trace and span id
//    private final Propagator propagator;                     // Injects trace context headers into outgoing http request

    @Bean
    @Primary
    public RestClient.Builder restClientBuilder() {
        RestClient.Builder builder = RestClient.builder();
//        if (observationRegistry != null) {
//            builder.requestInterceptor(createTracingInterceptor());
//        }
        return builder;
    }


    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClient() {
        RestClient.Builder builder = RestClient.builder();
//        if (observationRegistry != null) {
//            builder.requestInterceptor(createTracingInterceptor());
//        }
        return builder;
    }

//    private ClientHttpRequestInterceptor createTracingInterceptor() {
//        return ((request, body, execution) -> {
//            if(tracer != null && propagator != null && tracer.currentSpan() != null) {
//                propagator.inject(
//                        tracer.currentTraceContext().context(),
//                        request.getHeaders(),
//                        (carrier, key, value) -> carrier.add(key, value));
//            }
//            return execution.execute(request,body);
//        }
//        );
//    }
}
