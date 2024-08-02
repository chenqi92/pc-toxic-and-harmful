package com.lyc.toxicharmful.config.enums;

import cn.allbs.hj212.enums.Command;
import lombok.Getter;

import static com.lyc.toxicharmful.config.constant.CacheConstant.TOXIC_AND_HARMFUL_DATA;
import static com.lyc.toxicharmful.config.constant.CommonConstant.COLON;

/**
 * 枚举
 *
 * @author ChenQi
 * &#064;date 2024/8/2
 */
@Getter
public enum TimeUnitEnum {
    DAY("_day", "日", Command._2031),
    MINUTE("_minute", "分钟", Command._2051),
    HOUR("_hour", "小时", Command._2061),
    REAL_TIME("", "实时", Command._2011);

    private final String suffix;
    private final String suffixName;
    private final Command command;
    private final String cacheKey;

    TimeUnitEnum(String suffix, String suffixName, Command command) {
        this.suffix = suffix;
        this.suffixName = suffixName;
        this.command = command;
        this.cacheKey = TOXIC_AND_HARMFUL_DATA + suffix + COLON;
    }

    public static TimeUnitEnum fromCommand(String command) {
        for (TimeUnitEnum timeUnit : values()) {
            if (timeUnit.command != null && timeUnit.command.getValue().equals(command)) {
                return timeUnit;
            }
        }
        return REAL_TIME;
    }
}

