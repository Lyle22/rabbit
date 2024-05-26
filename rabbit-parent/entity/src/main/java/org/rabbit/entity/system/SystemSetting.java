package org.rabbit.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.Instant;

/**
 * The type System setting.
 *
 * @author nine rabbit
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("SystemSetting")
public class SystemSetting {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    @TableField("system_id")
    private String systemId;

    @TableField("system_type")
    private String systemIdType;

    private String jsonValue;

    @TableField("modified_date")
    private Instant modifiedDate;

}
