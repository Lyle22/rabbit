package org.rabbit.login.models;

import org.rabbit.common.enums.BusinessCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * message queue request
 *
 * @author nine rabbit
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageQueueRequestDTO {

    private String id;

    /**
     * the request MessageQueue
     */
    private String requestMQName;

    /**
     * the process MessageQueue
     */
    private String resultMQName;

    /**
     * the String of json format
     */
    private String jsonData;

    /**
     * Is it possible to consume repeatedly
     */
    private boolean isRepeatConsume;

    /**
     * The business category
     */
    private BusinessCategory category;

    /**
     * the id of operator
     */
    private String userId;

    /**
     * the business id
     */
    private String businessId;

    private String fileName;

    private String filePath;

    private Long size;

    private String idOrPath;

}