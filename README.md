# 有毒有害气体接收程序

作为tcp服务端，接口端口`6000`，启动端口`6789`，启动端口因不涉及web服务，可要可不要。

# 协议hj212-2017

含部分自定义因子编码，全部因子名称及编码定义如下

| 因子名称     | 因子编码   | 单位   | 阈值(一级)                | 阈值(二级)              |
|----------|--------|------|-----------------------|---------------------|
| 甲醇、甲苯、苯乙烯、丙烯酸、LPG、乙烯  | a30001 | %LEL | 25%LEL                | 50%LEL              |
| 苯乙烯、乙酸乙酯、甲醇、丙烯酸、氯丙烯、甲苯  | a25003 | %LEL | 25%LEL                | 50%LEL              |
| LPG、乙烯、丙烯、丙烷 | a66007 | %LEL | 25%LEL                | 50%LEL              |
| 硫酸二甲酯    | a66002 | ppm  | 0.5mg/m³(0.097ppm)    | 1mg/m³(0.194ppm)    |
| 氯甲基甲醚         | a66001 | ppm  | 0.005mg/m³(0.0025ppm) | 0.01mg/m³(0.005ppm) |
| 氨气       | a21001 | ppm  | 1mg/m³  (1.435ppm)    | 2mg/m³(2.87ppm)     |
| 丙烯腈   | a66003 | ppm  | 20mg/m³(9.22ppm)      | 40mg/m³(18.44ppm)      |

![](https://nas.allbs.cn:9006/cloudpic/2024/07/5fd718340e26b4ea80f194591547ed61.png)
