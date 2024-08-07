package org.rabbit.entity.form.entity.article;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;
import org.rabbit.entity.form.entity.base.BaseEntity;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class Article extends BaseEntity {
	
	private String title;

	@Tolerate
	public Article() {
		super();
	}
}
