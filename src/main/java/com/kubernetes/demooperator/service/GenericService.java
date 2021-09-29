package com.kubernetes.demooperator.service;

import com.kubernetes.demooperator.dao.GenericDao;
import io.kubernetes.client.apimachinery.GroupVersionResource;
import io.kubernetes.client.common.KubernetesListObject;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.util.ModelMapper;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import io.kubernetes.client.util.generic.options.CreateOptions;
import org.springframework.stereotype.Service;

/**
 * @author Eugene Pankov
 * on 29/09/2021
 */
@Service
public class GenericService {
    private final GenericDao genericDao;

    public GenericService(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    public <ApiType extends KubernetesObject> KubernetesObject create(final ApiType obj, final boolean isDryRun) throws ApiException {
        refreshApiDiscovery();
        if (obj.getMetadata().getNamespace() == null) {
            obj.getMetadata().setNamespace("default");
        }
        final GenericKubernetesApi<KubernetesObject, KubernetesListObject> api = createApi(obj.getClass());
        final CreateOptions createOptions = new CreateOptions();
        if (isDryRun) {
            createOptions.setDryRun("All");
        }
        return api.create(obj, createOptions).throwsApiException().getObject();
    }

    public <ApiType extends KubernetesObject> KubernetesObject delete(final ApiType obj) throws ApiException {
        refreshApiDiscovery();
        String name  = obj.getMetadata().getName();
        String namespace = obj.getMetadata().getNamespace() != null
                ? obj.getMetadata().getNamespace()
                : "default";
        final GenericKubernetesApi<KubernetesObject, KubernetesListObject> api = createApi(obj.getClass());
        return api.delete(namespace, name).throwsApiException().getObject();
    }

    private void refreshApiDiscovery() throws ApiException {
        if (!ModelMapper.isApiDiscoveryRefreshed()) {
            genericDao.refreshApiDiscovery();
        }
    }

    private GenericKubernetesApi<KubernetesObject, KubernetesListObject> createApi(final Class<?> clazz) {
        final GroupVersionResource groupVersionResource = ModelMapper.getGroupVersionResourceByClass(clazz);
        final Class<?> listClass = ServiceUtil.getListClass(clazz);
        return genericDao.createApi(ServiceUtil.castClass(clazz), ServiceUtil.castClass(listClass), groupVersionResource);
    }
}
