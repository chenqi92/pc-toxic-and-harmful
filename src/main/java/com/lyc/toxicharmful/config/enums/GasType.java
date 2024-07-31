package com.lyc.toxicharmful.config.enums;

/**
 * 自定义气体枚举
 *
 * @author ChenQi
 * @date 2024/7/29
 */
public enum GasType {
    COMBUSTIBLE_METHANOL_TOLUENE_1("可燃甲醇、甲苯", "a30001", "%LEL"),
    COMBUSTIBLE_METHANOL_TOLUENE_2("可燃甲醇、甲苯", "a25003", "%LEL"),
    COMBUSTIBLE_METHANOL_TOLUENE_ETHYLENE_LPG("可燃甲醇、甲苯、乙烯、LPG", "a66007", "%LEL"),
    COMBUSTIBLE_ETHYLENE_PROPYLENE_1("可燃乙烯、丙烯", "a66002", "ppm"),
    COMBUSTIBLE_ETHYLENE_PROPYLENE_2("可燃乙烯、丙烯", "a66001", "ppm"),
    TOXIC_AMMONIA("有毒氨气", "a21001", "ppm"),
    TOXIC_HYDROGEN_FLUORIDE("有毒氟化氢", "a66003", "ppm");

    private final String name;
    private final String code;
    private final String unit;

    GasType(String name, String code, String unit) {
        this.name = name;
        this.code = code;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return String.format("GasType{name='%s', code='%s', unit='%s'}", name, code, unit);
    }
}
