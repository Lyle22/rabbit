package org.rabbit.entity.template;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import org.rabbit.entity.base.BaseEntity;

/**
 * @author Lyle
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("doc_template")
public class DocumentTemplate extends BaseEntity {

    @TableField(value = "name")
    private String name;

    @TableField(value= "document_id")
    private String documentId;

    @TableField(value= "file_type")
    private String fileType;

    @TableField(value= "template_variable")
    private String templateVariable;

    @TableField(value= "description")
    private String description;

}
