package org.rabbit.entity.article;

import org.rabbit.entity.base.BaseEntity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

/**
 * Article class
 *
 * @author nine rabbit
 */
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
