package com.lyc.toxicharmful.service;

import cn.allbs.hj212.model.HjData;
import reactor.util.annotation.Nullable;

/**
 * 接口 MessageFactory
 *
 * @author ChenQi
 * @date 2024/7/29
 */
public interface MessageFactory {

    /**
     * 数据储存
     *
     * @param data 交互数据
     */
    @Nullable
    void hj212DataSave(HjData data);

    /**
     * 系统交互
     *
     * @param data 交互数据
     */
    @Nullable
    void systemAction(HjData data);
}
