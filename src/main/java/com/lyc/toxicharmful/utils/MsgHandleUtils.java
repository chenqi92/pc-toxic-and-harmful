package com.lyc.toxicharmful.utils;

import io.netty.channel.ChannelHandlerContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 功能: 报文处理工具类
 *
 * @author ChenQi
 * &#064;date 2024/7/29
 */
public class MsgHandleUtils {


    /**
     * 整型数据转二进制 (8位)
     *
     * @param num
     * @return
     */
    public static String toBinaryString(Integer num) {
        StringBuffer result = new StringBuffer(Integer.toBinaryString(num));
        if (result.length() < 8) {
            for (int i = result.length(); i < 8; i++) {
                result.insert(0, "0");
            }
        }

        return result.toString();
    }

    /**
     * 长度不足四位，前面补零
     *
     * @param length
     */
    public static String getLengthStr(int length) {
        StringBuilder lenStr = new StringBuilder(String.valueOf(length));
        if (lenStr.length() < 4) {
            for (int i = lenStr.length(); i < 4; i++) {
                lenStr.insert(0, "0");
            }
        }
        return lenStr.toString();
    }

    /**
     * 计算CRC16校验和
     *
     * @param msg
     * @param length
     * @return
     */
    public static String calculateCrc(String msg, int length) {
        int i, j, crc_reg, check;
        crc_reg = 0xFFFF;
        char[] c = msg.toCharArray();
        for (i = 0; i < length; i++) {

            crc_reg = (crc_reg >> 8) ^ c[i];
            for (j = 0; j < 8; j++) {
                check = crc_reg & 0x0001;
                crc_reg >>= 1;
                if (check == 0x0001) {
                    crc_reg ^= 0xA001;
                }
            }
        }
        return Integer.toHexString(crc_reg);
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date strToDate(String str, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取报文长度
     *
     * @param lengthStr
     * @return
     */
    public static String getNumFromStrStartWithZero(String lengthStr) {
        int index = 0;
        if (!lengthStr.startsWith("0")) {
            return lengthStr;
        } else {
            char[] letters = lengthStr.toCharArray();
            for (int i = 0; i < letters.length; i++) {
                char letter = letters[i];
                if (letter != '0') {
                    index = i;
                    break;
                }
            }
            return lengthStr.substring(index);
        }
    }

    /**
     * 获取channel的ip信息
     *
     * @param ctx
     * @return
     */
    public static String getIPString(ChannelHandlerContext ctx) {
        String ipString = "";
        String socketString = ctx.channel().remoteAddress().toString();
        System.out.println("ds:" + socketString);
        int colonAt = socketString.indexOf(":");
        ipString = socketString.substring(1, colonAt);
        return ipString;
    }

    /**
     * 获取channel的ip、port信息
     *
     * @param ctx
     * @return
     */
    public static String getIPPortString(ChannelHandlerContext ctx) {
        String socketString = ctx.channel().remoteAddress().toString();
        return socketString;
    }


    /**
     * 获取日期当月的总天数
     *
     * @param date
     * @return
     */
    public static int getDaysInMonth(Date date) {
        Calendar a = Calendar.getInstance();
        a.setTime(date);
        //把日期设置为当月第一天
        a.set(Calendar.DATE, 1);
        //日期回滚一天，也就是最后一天
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        System.out.println("maxDate:" + maxDate);
        return maxDate;
    }

    public static boolean isNotBlankAndEmptyHigh(Object value) {
        return value != null && value != "" && value != "null" && !String.valueOf(value).isEmpty();
    }
}
