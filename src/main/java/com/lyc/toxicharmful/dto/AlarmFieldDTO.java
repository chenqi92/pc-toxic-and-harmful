package com.lyc.toxicharmful.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 类 AlarmFieldDTO 有毒有害气体报警字段DTO 主要用于确定类型
 *
 * @author ChenQi
 * &#064;date 2024/8/1
 */
@Data
public class AlarmFieldDTO {

    private BigDecimal value;

    private String alarmTime;

    private String unit;

    private Integer level;

    private Double threshold;

    private String content;

    private Integer type;
}
