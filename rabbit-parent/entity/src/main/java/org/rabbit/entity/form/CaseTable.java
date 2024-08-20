package org.rabbit.entity.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import org.rabbit.entity.base.BaseEntity;

/**
 * <p>
 * case table
 * </p>
 *
 * @author nine
 * @since 2024-07-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("cmmn_case_table")
public class CaseTable extends BaseEntity {

    @TableField(value = "case_type_id")
    private String caseTypeId;

    @TableField(value = "label")
    private String label;

    @TableField(value = "table_name")
    private String tableName;

    @TableField(value = "status")
    private String status;

}
