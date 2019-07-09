package org.rabbit.entity.base;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 基础类
 * 
 */
@Data
@Accessors(chain = true)
public class BaseEntity {
	
	// 主键采用自增方式
	@TableId(type=IdType.AUTO)
	private Integer id;
	
	private String remarks;
	
	@TableField("create_date")
	private Date createDate;
	
	@TableField("create_by")
	private String createBy;
	
	@TableField("update_by")
	private String updateBy;
	
	@TableField("update_date")
	private Date updateDate;

	@TableField("flag")
	@TableLogic
	private Integer flag;
}