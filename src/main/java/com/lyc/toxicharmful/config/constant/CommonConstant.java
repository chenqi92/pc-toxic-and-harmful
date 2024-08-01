package com.lyc.toxicharmful.config.constant;

/**
 * 接口 CommonConstant 常量定义
 *
 * @author ChenQi
 * &#064;date 2024/7/29
 */
public interface CommonConstant {

    /**
     * influxdb中储存实时数据的表
     */
    String DB_TOXIC_AND_HARMFUL_DATA = "toxic_and_harmful_data";

    /**
     * influxdb中储存报警数据的表
     */
    String DB_TOXIC_AND_HARMFUL_ALARM = "toxic_and_harmful_alarm";

    /**
     * hj212中的时间格式
     */
    String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";

    /**
     * hj212中的时间格式
     */
    String PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS";

    /**
     * 常用时间格式
     */
    String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * influxdb常用时间格式
     */
    String UTC_MS_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
}
