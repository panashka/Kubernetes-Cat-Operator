package com.kubernetes.demooperator.service.handlers;

import com.kubernetes.demooperator.service.GenericService;
import com.kubernetes.demooperator.service.ServiceUtil;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CustomObjectsApi;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1OwnerReference;
import io.kubernetes.client.util.Yaml;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Eugene Pankov
 * on 28/09/2021
 */
@Component
public class AddHandler implements Handler {

    private final CustomObjectsApi customObjectsApi;
    private final GenericService genericService;

    public AddHandler(CustomObjectsApi customObjectsApi, GenericService genericService) {
        this.customObjectsApi = customObjectsApi;
        this.genericService = genericService;
    }

    @Override
    public void handle(V1ObjectMeta objMetadata) {
        Object cat = null;
        try {
            cat = customObjectsApi.getNamespacedCustomObject("dev.com", "v1", objMetadata.getNamespace(), "cats", objMetadata.getName());
            Map<String, Object> spec = getMapField(cat, "spec");
            String kittyName = getStringField(spec, "kittyname");
            String image = getStringField(spec, "image");

            Map<String, Object> metadata = getMapField(cat, "metadata");

            V1OwnerReference v1OwnerReference = buildOwnerReference(cat, metadata);

            String catYaml = ServiceUtil.readCatYamlFile();
            catYaml = catYaml.replaceAll("\\$\\{cat-name}", kittyName);
            catYaml = catYaml.replaceAll("\\$\\{cat-image}", image);

            List<Object> objects = Yaml.loadAll(catYaml);
            for (Object object : objects) {
                KubernetesObject obj = (KubernetesObject) object;
                obj.getMetadata().setOwnerReferences(Collections.singletonList(v1OwnerReference));
                genericService.create(obj, false);
            }
        } catch (ApiException | IOException apiException) {
            apiException.printStackTrace();
        }
    }

    private V1OwnerReference buildOwnerReference(Object cat, Map<String, Object> metadata) {
        V1OwnerReference v1OwnerReference = new V1OwnerReference();
        v1OwnerReference.setKind(getStringField(cat, "kind"));
        v1OwnerReference.setName(getStringField(metadata, "name"));
        v1OwnerReference.setBlockOwnerDeletion(true);
        v1OwnerReference.setController(true);
        v1OwnerReference.setUid(getStringField(metadata, "uid"));
        v1OwnerReference.setApiVersion(getStringField(cat, "apiVersion"));
        return v1OwnerReference;
    }

    @SuppressWarnings("unchecked")
    private String getStringField(Object obj, String fieldName) {
        Map<String, String> map = (Map<String, String>) obj;
        return map.get(fieldName);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapField(Object obj, String fieldName) {
        Map<String, Object> map = (Map<String, Object>) obj;
        return (Map<String, Object>) map.get(fieldName);
    }

    @Override
    public String getType() {
        return "ADDED";
    }
}
