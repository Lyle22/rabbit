package org.rabbit.login.models;

import lombok.Data;

/**
 * message queue properties
 *
 * @author nine rabbit
 */
@Data
public class MQProperties {

    private static final String CONSUME_GROUP_PREFIX = "consume-group-";

    /**
     * the message queue name
     */
    String name;

    /**
     * the consumer of message queue
     */
    String consumerName;

    public String getGroupName() {
        return CONSUME_GROUP_PREFIX + getName();
    }

}
