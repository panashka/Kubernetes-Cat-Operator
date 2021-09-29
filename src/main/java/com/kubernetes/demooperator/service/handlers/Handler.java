package com.kubernetes.demooperator.service.handlers;

import io.kubernetes.client.openapi.models.V1ObjectMeta;

/**
 * @author Eugene Pankov
 * on 28/09/2021
 */
public interface Handler {
    void handle(V1ObjectMeta objMetadata);
    String getType();
}
