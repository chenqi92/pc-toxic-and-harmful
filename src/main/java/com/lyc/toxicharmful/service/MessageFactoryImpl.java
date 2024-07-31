package com.lyc.toxicharmful.service;

import cn.allbs.hj212.model.CpData;
import cn.allbs.hj212.model.HjData;
import cn.allbs.hj212.model.Pollution;
import cn.allbs.influx.InfluxTemplate;
import com.lyc.toxicharmful.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.lyc.toxicharmful.config.constant.CacheConstant.TOXIC_AND_HARMFUL_DATA;
import static com.lyc.toxicharmful.config.constant.CommonConstant.DB_TOXIC_AND_HARMFUL_DATA;
import static com.lyc.toxicharmful.config.constant.CommonConstant.NORM_DATETIME_PATTERN;

/**
 * 类 MessageFactoryImpl
 *
 * @author ChenQi
 * @date 2024/7/29
 */
@Slf4j
@Service
public class MessageFactoryImpl implements MessageFactory {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private InfluxTemplate influxTemplate;

    @Override
    public void hj212DataSave(HjData hjData) {
        log.info("接收到的212数据{}", hjData);
        // 储存实时数据
        try {
            if (Optional.ofNullable(hjData).map(HjData::getCp).map(CpData::getPollution).isPresent()) {
                redisTemplate.opsForHash().put(TOXIC_AND_HARMFUL_DATA + hjData.getMn(), "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)));
                hjData.getCp().getPollution().forEach((key, value) -> redisTemplate.opsForHash().put(TOXIC_AND_HARMFUL_DATA + hjData.getMn(), key, Objects.requireNonNull(Optional.ofNullable(value).map(Pollution::getRtd).orElse(null))));
            }
        } catch (Exception e) {
            log.error("保存212实时数据失败,数据{},原因{}", hjData, e.getLocalizedMessage());
        }
        // 储存influxdb
        Map<String, String> tags = new HashMap<>();
        Map<String, Object> fields = new HashMap<>();
        tags.put("mn", Optional.ofNullable(hjData).map(HjData::getMn).orElse(""));
        tags.put("qnTime", Optional.ofNullable(hjData).map(a -> DateUtil.timeFormatWithMs(a.getQn())).orElse(""));
        tags.put("dataTime", Optional.ofNullable(hjData).map(HjData::getCp).map(CpData::getDataTime).map(DateUtil::timeFormat).orElse(""));
        if (Optional.ofNullable(hjData).map(HjData::getCp).map(CpData::getPollution).isPresent()) {
            redisTemplate.opsForHash().put(TOXIC_AND_HARMFUL_DATA + hjData.getMn(), "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)));
            hjData.getCp().getPollution().forEach((key, value) -> {
                Optional<BigDecimal> rtd = Optional.ofNullable(value.getRtd());
                Optional<BigDecimal> avg = Optional.ofNullable(value.getAvg());
                if (rtd.isPresent() || avg.isPresent()) {
                    fields.put(key, rtd.orElseGet(avg::get));
                }
                fields.put(key + "_flag", Optional.of(value).map(Pollution::getFlag).orElse(null));
                fields.put(key + "_SampleTime", Optional.of(value).map(Pollution::getSampleTime).orElse(null));
            });
        }
        influxTemplate.insert(DB_TOXIC_AND_HARMFUL_DATA, tags, fields);
    }

    @Override
    public void systemAction(HjData hjData) {
        log.info("执行系统指令{}", hjData);
    }
}
