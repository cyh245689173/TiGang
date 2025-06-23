package com.copico.model.enums;

import lombok.Getter;

/**
 * 用户等级枚举
 */
@Getter
public enum UserRankEnum {
    NEWCOMER("提肛萌新", 0, 500),
    NOVICE("提肛小白", 501, 1500),
    APPRENTICE("提肛学徒", 1501, 3000),
    PRACTITIONER("提肛行者", 3001, 5000),
    ADEPT("提肛能手", 5001, 7500),
    EXPERT("提肛专家", 7501, 10000),
    MASTER("提肛大师", 10001, 15000),
    CHAMPION("提肛冠军", 15001, 20000),
    LEGEND("提肛传奇", 20001, 30000),
    GOD("提肛之神", 30001, Integer.MAX_VALUE);

    private final String desc;
    private final int minExp;
    private final int maxExp;

    UserRankEnum(String desc, int minExp, int maxExp) {
        this.desc = desc;
        this.minExp = minExp;
        this.maxExp = maxExp;
    }

    /**
     * 根据经验值获取对应的用户等级
     * @param exp 经验值
     * @return 用户等级枚举
     */
    public static UserRankEnum getRankByExp(int exp) {
        for (UserRankEnum rank : UserRankEnum.values()) {
            if (exp >= rank.minExp && exp <= rank.maxExp) {
                return rank;
            }
        }
        return NEWCOMER;
    }
}