package org.rabbit.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.rabbit.entity.base.BaseEntity;

/**
 * The class of system configuration.
 *
 * @author nine rabbit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("sys_config")
public class SystemConfig extends BaseEntity {

    @TableField("name")
    private String name;

    @TableField("system_id")
    private String systemId;

    @TableField("system_type")
    private String systemType;

    @TableField("json_config")
    private String jsonConfig;

}
