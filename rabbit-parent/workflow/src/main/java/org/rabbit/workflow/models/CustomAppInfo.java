package org.rabbit.workflow.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.flowable.engine.repository.Deployment;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Lyle
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomAppInfo implements Serializable {

    String name;

    String category;

    String key;

    Date deploymentTime;

    String tenantId;

    public CustomAppInfo(Deployment deployment) {
        setName(deployment.getName());
        setKey(deployment.getKey());
        setCategory(deployment.getCategory());
        setDeploymentTime(deployment.getDeploymentTime());
        setTenantId(deployment.getTenantId());
    }

}
