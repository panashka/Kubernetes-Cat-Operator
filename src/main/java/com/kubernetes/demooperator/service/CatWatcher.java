package com.kubernetes.demooperator.service;

import com.google.gson.reflect.TypeToken;
import com.kubernetes.demooperator.service.handlers.Handler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.openapi.models.V1CustomResourceDefinition;
import io.kubernetes.client.util.Watch;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Eugene Pankov
 * on 28/09/2021
 */
@Component
public class CatWatcher {

    private final CustomObjectsApi customObjectsApi;
    private final ApiClient apiClient;
    private final List<Handler> handlers = new ArrayList<>();

    public CatWatcher(CustomObjectsApi customObjectsApi, ApiClient apiClient) {
        this.customObjectsApi = customObjectsApi;
        this.apiClient = apiClient;
    }

    public void registerHandler(Handler handler) {
        handlers.add(handler);
    }

    public void watch() throws ApiException, IOException {
        try (Watch<V1CustomResourceDefinition> watch = Watch.createWatch(
                apiClient,
                customObjectsApi.listNamespacedCustomObjectCall(
                        "dev.com", "v1", "default", "cats", "no_pretty",
                        null, null, null, null, null, null, Boolean.TRUE, null),
                new TypeToken<Watch.Response<V1CustomResourceDefinition>>() {
                }.getType())) {

            for (Watch.Response<V1CustomResourceDefinition> item : watch) {
                Objects.requireNonNull(item.object.getMetadata());
                System.out.printf("%s : %s%n", item.type, item.object.getMetadata().getName());
                handlers.stream()
                        .filter(handler -> handler.getType().equals(item.type))
                        .findFirst()
                        .ifPresent(handler -> handler.handle(item.object.getMetadata()));
            }
        }
    }
}
