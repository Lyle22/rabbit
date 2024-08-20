package org.rabbit.entity.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * 基础类
 *
 * @author nine rabbit
 */
@Data
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private String id;
	
	@TableField("remarks")
	private String remarks;
	
	@TableField("del_flag")
	@TableLogic
	private Integer delFlag;

	@TableField(value = "created_by")
	@Getter
	@Setter
	private String createdBy;

	@TableField(value = "modified_by")
	@Getter
	@Setter
	private String modifiedBy;

	@TableField(value = "created_date", fill = FieldFill.INSERT)
	@Getter
	@Setter
	private Instant createdDate;

	@TableField(value = "modified_date", fill = FieldFill.INSERT_UPDATE)
	@Getter
	@Setter
	private Instant modifiedDate;

}