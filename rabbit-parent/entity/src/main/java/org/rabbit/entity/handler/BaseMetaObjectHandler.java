package org.rabbit.entity.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * @author nine rabbit
 **/
@Component
public class BaseMetaObjectHandler implements MetaObjectHandler {

        private final static String CREATE_BY = "created_by";
        private final static String CREATE_TIME = "created_date";
        private final static String UPDATE_BY = "modified_by";
        private final static String UPDATE_TIME = "modified_date";

        /**
         * 插入元对象字段填充（用于插入时对公共字段的填充）
         * @param metaObject
         */
        @Override
        public void insertFill(MetaObject metaObject) {
            try {
                Object createTime = metaObject.getValue(CREATE_TIME);
                Object createBy = metaObject.getValue(CREATE_BY);
                if (ObjectUtils.isNull(createTime)) {
                    this.setFieldValByName(CREATE_TIME, Instant.now(), metaObject);
                }
                if (ObjectUtils.isNull(createBy)){
                    //后续可以通过获取当前登录对象进行获取账号并设置我这边演示就默认cr
                    this.setFieldValByName(CREATE_BY, "cr", metaObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 更新元对象字段填充（用于更新时对公共字段的填充）
         * @param metaObject
         */
        @Override
        public void updateFill(MetaObject metaObject) {
            try {
                Object updateTime = metaObject.getValue(UPDATE_TIME);
                Object updateBy = metaObject.getValue(UPDATE_BY);
                if (ObjectUtils.isNull(updateTime)) {
                    this.setFieldValByName(UPDATE_TIME, Instant.now(), metaObject);
                }
                if (ObjectUtils.isNull(updateBy)){
                    //后续可以通过获取当前登录对象进行获取账号并设置我这边演示就默认up
                    this.setFieldValByName(UPDATE_BY, "up", metaObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


