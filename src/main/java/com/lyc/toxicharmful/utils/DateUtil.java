package com.lyc.toxicharmful.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import static com.lyc.toxicharmful.config.constant.CommonConstant.NORM_DATETIME_PATTERN;
import static com.lyc.toxicharmful.config.constant.CommonConstant.PURE_DATETIME_PATTERN;

/**
 * 类 DateUtil
 *
 * @author ChenQi
 * &#064;date 2024/7/29
 */
public class DateUtil {

    /**
     * 时间转化 yyyyMMddHHmmss -> yyyy-MM-dd HH:mm:ss
     *
     * @param originalString 原始时间字符串
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String timeFormat(String originalString) {
        // 构建支持毫秒的解析器
        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern(PURE_DATETIME_PATTERN);
        // 将原始字符串解析为LocalDateTime对象
        LocalDateTime dateTime = LocalDateTime.parse(originalString, originalFormatter);
        // 定义新的日期时间格式
        DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN);
        // 将LocalDateTime对象格式化为新的字符串格式
        return dateTime.format(newFormatter);
    }

    /**
     * 时间转化 yyyyMMddHHmmssSSS -> yyyy-MM-dd HH:mm:ss
     *
     * @param originalString 原始时间字符串
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String timeFormatWithMs(String originalString) {
        // 构建支持毫秒的解析器
        DateTimeFormatter originalFormatter = new DateTimeFormatterBuilder()
                .appendPattern(PURE_DATETIME_PATTERN)
                .appendValue(ChronoField.MILLI_OF_SECOND, 3)
                .toFormatter();

        // 将原始字符串解析为LocalDateTime对象
        LocalDateTime dateTime = LocalDateTime.parse(originalString, originalFormatter);
        // 定义新的日期时间格式
        DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN);
        // 将LocalDateTime对象格式化为新的字符串格式
        return dateTime.format(newFormatter);
    }
}
