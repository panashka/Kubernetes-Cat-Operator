package com.kubernetes.demooperator.service;

import io.kubernetes.client.openapi.models.*;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Eugene Pankov
 * on 21.09.20
 */
public final class ServiceUtil {
    private ServiceUtil() {
        throw new UnsupportedOperationException(this.getClass() + " could not be instantiated by constructor");
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> castClass(final Class<?> clazz) {
        return (Class<T>) clazz;
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    public static Class<?> getListClass(final Class<?> clazz) {
        switch (clazz.getSimpleName()) {
            case "V1Pod":
                return V1PodList.class;
            case "V1Service":
                return V1ServiceList.class;
            case "V1PersistentVolumeClaim":
                return V1PersistentVolumeClaim.class;
            case "V1Deployment":
                return V1DeploymentList.class;
            case "V1ReplicaSet":
                return V1ReplicaSetList.class;
            case "V1ConfigMap":
                return V1ConfigMapList.class;
            case "V1Endpoints":
                return V1EndpointsList.class;
            case "V1Job":
                return V1JobList.class;
            default:
        }
        throw new RuntimeException("Unsupported object exception.");
    }

    public static String readCatYamlFile() throws IOException {
        File file = ResourceUtils.getFile("classpath:nginx-cat.yaml");
        StringBuilder contentBuilder = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        }
        return contentBuilder.toString();
    }
}