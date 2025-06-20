package com.copico.model.enums;


import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum TrainingTypeEnum {

    EASY(1, "简单难度"),
    MEDIUM(2, "中等难度"),
    HIGH(3, "高级难度"),
    HELL(4, "地域难度");

    private final Integer code;
    private final String desc;

    TrainingTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;

    }

    /**
     * 根据 code 获取枚举
     *
     * @param code 枚举值的 code
     * @return 枚举值
     */
    public static TrainingTypeEnum getEnumByCode(Integer code) {
        if (ObjUtil.isEmpty(code)) {
            return null;
        }
        for (TrainingTypeEnum typeEnum : TrainingTypeEnum.values()) {
            if (typeEnum.code.equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}
