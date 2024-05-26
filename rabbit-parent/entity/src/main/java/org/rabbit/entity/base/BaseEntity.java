package org.rabbit.entity.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 基础类
 * 
 */
@Data
@Accessors(chain = true)
public class BaseEntity {
	
	@TableId
	private String id;
	
	@TableField("remarks")
	private String remarks;
	
	@TableField("del_flag")
	@TableLogic
	private Integer delFlag;

	@TableField("create_date")
	private Date createDate;
	
	@TableField("create_by")
	private String createBy;
	
	@TableField("update_by")
	private String updateBy;
	
	@TableField("update_date")
	private Date updateDate;

}