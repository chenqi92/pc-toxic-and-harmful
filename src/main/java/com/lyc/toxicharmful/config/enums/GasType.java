package com.lyc.toxicharmful.config.enums;

import lombok.Getter;

/**
 * 自定义气体枚举
 *
 * @author ChenQi
 * &#064;date 2024/7/29
 */
@Getter
public enum GasType {
    METHANOL_TOLUENE_STYRENE_ACRYLIC_LPG_ETHYLENE("a30001", "甲醇、甲苯、苯乙烯、丙烯酸、LPG、乙烯", "%LEL", 25, 50),
    STYRENE_ETHYL_ACETATE_METHANOL_ACRYLIC_CHLOROPROPYLENE_TOLUENE("a25003", "苯乙烯、乙酸乙酯、甲醇、丙烯酸、氯丙烯、甲苯", "%LEL", 25, 50),
    LPG_ETHYLENE_PROPYLENE_PROPANE("a66007", "LPG、乙烯、丙烯、丙烷", "%LEL", 25, 50),
    DIMETHYL_SULFATE("a66002", "硫酸二甲酯", "ppm", 0.097, 0.194),
    CHLOROMETHYL_METHYL_ETHER("a66001", "氯甲基甲醚", "ppm", 0.0025, 0.005),
    AMMONIA("a21001", "氨气", "ppm", 1.435, 2.87),
    ACRYLONITRILE("a66003", "丙烯腈", "ppm", 9.22, 18.44);

    private final String code;
    private final String name;
    private final String unit;
    private final double levelOneThreshold;
    private final double levelTwoThreshold;

    GasType(String code, String name, String unit, double levelOneThreshold, double levelTwoThreshold) {
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.levelOneThreshold = levelOneThreshold;
        this.levelTwoThreshold = levelTwoThreshold;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - 一级阈值: %s %s, 二级阈值: %s %s", name, code, levelOneThreshold, unit, levelTwoThreshold, unit);
    }
}
