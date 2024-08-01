package com.lyc.toxicharmful.service;

import cn.allbs.hj212.model.CpData;
import cn.allbs.hj212.model.HjData;
import cn.allbs.hj212.model.Pollution;
import cn.allbs.influx.InfluxTemplate;
import com.lyc.toxicharmful.config.enums.GasType;
import com.lyc.toxicharmful.dto.AlarmFieldDTO;
import com.lyc.toxicharmful.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.lyc.toxicharmful.config.constant.CacheConstant.TOXIC_AND_HARMFUL_DATA;
import static com.lyc.toxicharmful.config.constant.CacheConstant.TOXIC_AND_HARMFUL_DATA_ALARM;
import static com.lyc.toxicharmful.config.constant.CommonConstant.*;

/**
 * 类 MessageFactoryImpl
 *
 * @author ChenQi
 * &#064;date 2024/7/29
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
        // 储存influxdb
        Map<String, String> tags = new HashMap<>();
        Map<String, Object> fields = new HashMap<>();
        tags.put("mn", Optional.ofNullable(hjData).map(HjData::getMn).orElse(""));
        tags.put("qnTime", Optional.ofNullable(hjData).map(a -> DateUtil.timeFormatWithMs(a.getQn())).orElse(""));
        tags.put("dataTime", Optional.ofNullable(hjData).map(HjData::getCp).map(CpData::getDataTime).map(DateUtil::timeFormat).orElse(""));
        redisTemplate.opsForHash().put(TOXIC_AND_HARMFUL_DATA + Optional.ofNullable(hjData).map(HjData::getMn).orElse(""), "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)));
        if (Optional.ofNullable(hjData).map(HjData::getCp).map(CpData::getPollution).isPresent()) {
            redisTemplate.opsForHash().put(TOXIC_AND_HARMFUL_DATA + hjData.getMn(), "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)));
            hjData.getCp().getPollution().forEach((key, value) -> {
                Optional<BigDecimal> rtd = Optional.ofNullable(value.getRtd());
                Optional<BigDecimal> avg = Optional.ofNullable(value.getAvg());
                if (rtd.isPresent() || avg.isPresent()) {
                    BigDecimal valueToCheck = rtd.orElseGet(avg::get);
                    fields.put(key, valueToCheck);
                    // redis储存实时数据
                    try {
                        redisTemplate.opsForHash().put(TOXIC_AND_HARMFUL_DATA + hjData.getMn(), key, valueToCheck);
                    } catch (Exception e) {
                        log.error("保存212实时数据失败,数据{},原因{}", hjData, e.getLocalizedMessage());
                    }
                    checkAndHandleAlarm(key, valueToCheck);
                }
                fields.put(key + "_flag", Optional.of(value).map(Pollution::getFlag).orElse(null));
                fields.put(key + "_SampleTime", Optional.of(value).map(Pollution::getSampleTime).orElse(null));
            });
        }
        influxTemplate.insert(DB_TOXIC_AND_HARMFUL_DATA, tags, fields);
    }

    /**
     * 检查是否报警
     *
     * @param key   key
     * @param value value
     */
    private void checkAndHandleAlarm(String key, BigDecimal value) {
        for (GasType factor : GasType.values()) {
            if (key.equals(factor.getCode())) {
                if (value.compareTo(BigDecimal.valueOf(factor.getLevelTwoThreshold())) >= 0) {
                    log.warn("报警: {} 超过二级阈值 {}", key, factor.getLevelTwoThreshold());
                    sendAlarmAsync(key, value, 2, factor);
                } else if (value.compareTo(BigDecimal.valueOf(factor.getLevelOneThreshold())) >= 0) {
                    log.warn("报警: {} 超过一级阈值 {}", key, factor.getLevelOneThreshold());
                    sendAlarmAsync(key, value, 1, factor);
                } else {
                    log.info("正常: {} 未超过阈值", key);
                    // 消警
                    clearAlarmAsync(key, factor.getName());
                }
                break;
            }
        }
    }

    /**
     * 发送报警数据
     *
     * @param key    因子编码
     * @param value  监测值
     * @param level  等级 1级 2级
     * @param factor 枚举
     */
    public void sendAlarmAsync(String key, BigDecimal value, Integer level, GasType factor) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN));
        try {
            if (Boolean.FALSE.equals(redisTemplate.hasKey(TOXIC_AND_HARMFUL_DATA_ALARM + key))) {
                redisTemplate.opsForHash().put(TOXIC_AND_HARMFUL_DATA_ALARM + key, "startTime", time);
                redisTemplate.opsForHash().put(TOXIC_AND_HARMFUL_DATA_ALARM + key, "name", factor.getName());
                redisTemplate.opsForHash().put(TOXIC_AND_HARMFUL_DATA_ALARM + key, "unit", factor.getUnit());
                // 不存在则新增influxdb数据
                // InfluxDB报警数据
                Map<String, String> tags = new HashMap<>();
                Map<String, Object> fields = new HashMap<>();
                tags.put("code", key);
                tags.put("name", factor.getName());
                fields.put("value", value);
                fields.put("alarmTime", time);
                fields.put("unit", factor.getUnit());
                fields.put("level", level);
                // 额外添加type, 0为报警 1为消警
                fields.put("type", 0);
                fields.put("threshold", factor.getLevelOneThreshold() * level);
                fields.put("content", String.format("监测点位%s编号%s于%s发生%s级报警,报警值%s%s,报警阈值%s%s", factor.getName(), key, time, level, value, factor.getUnit(), factor.getLevelOneThreshold() * level, factor.getUnit()));
                influxTemplate.insert(DB_TOXIC_AND_HARMFUL_ALARM, tags, fields);
            }
            // Redis报警数据
            redisTemplate.opsForHash().put(TOXIC_AND_HARMFUL_DATA_ALARM + key, "level", level);
            redisTemplate.opsForHash().put(TOXIC_AND_HARMFUL_DATA_ALARM + key, "value", value);
            redisTemplate.opsForHash().put(TOXIC_AND_HARMFUL_DATA_ALARM + key, "threshold", factor.getLevelOneThreshold() * level);

        } catch (Exception e) {
            log.error("发送报警数据失败, key: {}, value: {}, level: {}, 原因: {}", key, value, level, e.getLocalizedMessage());
        }
    }

    /**
     * 清除报警
     *
     * @param key key
     */
    public void clearAlarmAsync(String key, String name) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(TOXIC_AND_HARMFUL_DATA_ALARM + key))) {
            log.info("移除{}报警缓存，添加消警时间", key);
            // 移除redis报警
            redisTemplate.delete(TOXIC_AND_HARMFUL_DATA_ALARM + key);
            // 获取当前点位未消警的报警数据,删除后插入带有结束时间的数据
            String command = "SELECT * FROM " + DB_TOXIC_AND_HARMFUL_ALARM + "\n" +
                    "WHERE \"code\" = '" + key + "' AND \"type\" = 0\n" +
                    "ORDER BY time DESC\n" +
                    "LIMIT 1";
            List<AlarmFieldDTO> dataList = influxTemplate.queryBeanList(command, AlarmFieldDTO.class);
            if (!dataList.isEmpty()) {
                AlarmFieldDTO fieldDTO = dataList.get(0);
                Map<String, String> tags = new HashMap<>();
                tags.put("code", key);
                tags.put("name", name);
                Map<String, Object> fields = new HashMap<>();
                fields.put("value", fieldDTO.getValue());
                fields.put("alarmTime", fieldDTO.getAlarmTime());
                fields.put("unit", fieldDTO.getUnit());
                fields.put("level", fieldDTO.getLevel());
                fields.put("threshold", fieldDTO.getThreshold());
                fields.put("content", fieldDTO.getContent());
                fields.put("type", 1);
                fields.put("clearTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)));
                // 移除原数据
                influxTemplate.query("DELETE FROM " + DB_TOXIC_AND_HARMFUL_ALARM + " WHERE alarmTime = '" + fieldDTO.getAlarmTime() + "' AND code = '" + key + "'");
                // 增加数据
                influxTemplate.insert(DB_TOXIC_AND_HARMFUL_ALARM, tags, fields);
            }
        }
    }

    @Override
    public void systemAction(HjData hjData) {
        log.info("执行系统指令{}", hjData);
    }
}
