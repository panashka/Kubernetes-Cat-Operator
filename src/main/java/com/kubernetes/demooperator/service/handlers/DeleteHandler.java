package com.kubernetes.demooperator.service.handlers;

import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.springframework.stereotype.Component;

/**
 * @author Eugene Pankov
 * on 29/09/2021
 */
@Component
public class DeleteHandler implements Handler {

    @Override
    public void handle(V1ObjectMeta objMetadata) {
        System.out.printf("Dependent objects of cat %s are going to be removed%n", objMetadata.getName());
    }

    @Override
    public String getType() {
        return "DELETED";
    }
}
