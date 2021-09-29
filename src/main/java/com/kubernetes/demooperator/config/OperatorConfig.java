package com.kubernetes.demooperator.config;

import com.google.protobuf.Api;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Eugene Pankov
 * on 25/09/2021
 */
@Configuration
public class OperatorConfig {

    @Bean("apiClient")
    public ApiClient apiClient() throws IOException {
        return ClientBuilder.cluster().build();
//        String kubeConfigPath = System.getenv("HOME") + "/.kube/config";
//        return  ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
    }

    @Bean
    @DependsOn("apiClient")
    public CoreV1Api coreV1Api(ApiClient apiClient) {
        return new CoreV1Api(apiClient);
    }

    @Bean
    @DependsOn("apiClient")
    public CustomObjectsApi customObjectsApi(ApiClient apiClient) {
        return new CustomObjectsApi(apiClient);
    }
}
