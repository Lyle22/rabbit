package org.rabbit.workflow.constants;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.flowable.engine.repository.Deployment;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeploymentDTO implements Serializable {

    String id;

    String name;

    Date deploymentTime;

    String category;

    String key;

    String derivedFrom;

    String derivedFromRoot;

    String tenantId;

    String engineVersion;

    boolean isNew;

    public DeploymentDTO(Deployment deployment) {
        setId(deployment.getId());
        setName(deployment.getName());
        setKey(deployment.getKey());
        setCategory(deployment.getCategory());
        setDeploymentTime(deployment.getDeploymentTime());
        setDerivedFrom(deployment.getDerivedFrom());
        setDerivedFromRoot(deployment.getDerivedFromRoot());
        setTenantId(deployment.getTenantId());
        setNew(deployment.isNew());
        setEngineVersion(deployment.getEngineVersion());
    }

}
