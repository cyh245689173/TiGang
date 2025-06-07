package com.copico.config.mybatis;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.copico.common.base.Constant;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 陈玉皓
 * @date 2025/6/4 21:10
 * @description: TODO
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 自动填充
     * 填充原理是直接给entity的性设值!!!
     * 注解则是指定该属性在对应情况下必有值，如果无值则入库会是null
     * MetaobiectHandler提供的默认方法的策略均为:如果属性有值则不覆盖,如果填充值为null则不填充
     * 字段必须声明TableField注解,属性fill选择对应策略,该声明告知Mybatis-Plus需要预留注入SQL字段
     * 填充处理器MyMeta0bjectHandler在 Spring Boot 中需要声明@Component或oBean注入
     * 要想根据注解FieldFill.xxx和字段名以及字段类型来区分必须使用父类的strictInsertFill或者strictupdateFill方法
     * 不需要根据任何来区分可以使用父类的fillstrategy万法
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "deleteFlag", Integer.class, Constant.DELETE_FLAG_NO);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());

    }
}