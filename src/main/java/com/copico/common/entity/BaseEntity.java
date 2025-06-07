package com.copico.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.copico.common.base.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author 陈玉皓
 * @date 2025/6/7 10:58
 * @description: 所有数据库实体的顶级实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEntity<T extends Model<T>> extends Model<T> {


    @TableId(type = IdType.AUTO)
    private long id;

    /*
     *只对自动注入的sql起效
     *
     * 插入:不作限制
     * 查询:追加where条件过滤掉已删除数据，且使用wrapper.entity生成的where会忽略该字段
     * 更新:追加where条件防止更新到已删除数据，且使用wrapper.entity生成的where会忽略该字段
     * 删除:转变为 更新
     */
    @TableLogic(delval = Constant.DELETE_FLAG_YES + "", value = Constant.DELETE_FLAG_NO + "")
    @TableField(fill = FieldFill.INSERT)
    private Integer deleteFlag;

    @TableField
    private String updateUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @TableField
    private String createUser;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
