package com.kubernetes.demooperator.dao;

import io.kubernetes.client.Discovery;
import io.kubernetes.client.apimachinery.GroupVersionResource;
import io.kubernetes.client.common.KubernetesListObject;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.util.ModelMapper;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author Eugene Pankov
 * on 10/11/2020
 */
@Component
public class GenericDao {
    private final ApiClient apiClient;

    public GenericDao(final ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public <ApiType extends KubernetesObject, ApiListType extends KubernetesListObject> GenericKubernetesApi<ApiType, ApiListType> createApi(final Class<ApiType> apiTypeClass,
                                                                                                                                             final Class<ApiListType> apiListTypeClass,
                                                                                                                                             final GroupVersionResource groupVersionResource) {
        return new GenericKubernetesApi<>(
                apiTypeClass,
                apiListTypeClass,
                groupVersionResource.getGroup(),
                groupVersionResource.getVersion(),
                groupVersionResource.getResource(),
                apiClient);
    }

    public Set<Discovery.APIResource> refreshApiDiscovery() throws ApiException {
        return ModelMapper.refresh(new Discovery(apiClient));
    }
}
