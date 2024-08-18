package org.rabbit.workflow.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The type Workflow request dto.
 */
@Data
@Schema(description = "Workflow (Request)")
public class WorkflowRequestDTO {
    @Schema(description = "Process Key")
    private String processKey;

    @Schema(description = "Message Name")
    private String messageName;

    @Schema(description = "Process Business Key")
    private String businessKey;

    @Schema(description = "Process Definition Id")
    private String processDefinitionId;

    @Schema(description = "Process Instance Id")
    private String processInstanceId;

    @Schema(description = "creator")
    private String creator;

    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Task ID")
    private String taskId;

    @Schema(description = "Task Delete Reason")
    private String deleteReason;

    @Schema(description = "Task Due Date")
    private Instant dueDate;

    @Schema(description = "Task attachment ID")
    private String attachmentId;

    @Schema(description = "process Category")
    private String processCategory;

    @Schema(description = "Groups")
    private List<String> groups;

    @Schema(description = "Form Properties")
    private Map<String, String> properties;

    @Schema(description = "Variables")
    private Map<String, Object> variables;

    @Schema(description = "Form Attachments")
    private Map<String, String> attachments;

    @Deprecated
    @Schema(description = "Page Index")
    private Integer pageIndex;

    public Integer getPageNum() {
        if (this.pageNum == null) {
            return this.pageIndex;
        }
        return pageNum;
    }

    @Schema(description = "Page num")
    private Integer pageNum;

    @Schema(description = "Page Size")
    private Integer pageSize;

    @Schema(description = "processKeys")
    private List<String> processKeys;

    @Schema(description = "createdDate")
    private Date[] createdDate;

    @Schema(description = "Task Due Date")
    private Date[] dueDates;

    private String involvedUser;//候选人或者绑定人

    private String assignedUser;//绑定人

    private String candidateUser;//候选人

    private String candidateOrAssigned;//候选人或者绑定人

    private String interrelatedUserId;//相关的user
    /**
     * Returns the value to which the specified key is mapped, or null if this properties contains no mapping
     * for the key. More formally, if this properties contains a mapping from a key k to a value v such that
     * (key==null ? k==null : key.equals(k)), then this method returns v; otherwise it returns null.
     * (There can be at most one such mapping.)
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
     */
    public String getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Associates the specified value with the specified key in the properties.
     * If the properties previously contained a mapping for the key, the old value is replaced.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    public void putProperty(String key, String value) {
        properties.put(key, value);
    }
}
