package com.lyc.toxicharmful.config.constant;

/**
 * 接口 CacheConstant redis key定义
 *
 * @author ChenQi
 * &#064;date 2024/7/29
 */
public interface CacheConstant {

    /**
     * 项目
     */
    String PRODUCT = "pc:";

    /**
     * 有毒有害气体最新数据缓存
     */
    String TOXIC_AND_HARMFUL_DATA = PRODUCT + "th_data:";

    /**
     * 有毒有害气体报警数据缓存
     */
    String TOXIC_AND_HARMFUL_DATA_ALARM = PRODUCT + "th_alarm:";
}
