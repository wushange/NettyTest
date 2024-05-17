package com.example.testsocket.message;

public class MessageType {
    /**
     * 查询听诊器 ID
     */
    public static final String MCU_GETREQI = "$GETREQI";
    public static final String MCU_RETREQI = "$RETREQI";

    /**
     * 查询终端版本号
     */
    public static final String MCU_GETDVER = "$GETDVER";
    public static final String MCU_RETDVER = "$RETDVER";

    /**
     * 设置日期时间
     */
    public static final String MCU_SETDT = "$SETDT";
    public static final String MCU_RETDT = "$RETDT";

    /**
     * 设置休眠等待时间
     */
    public static final String MCU_SETSLPT = "$SETSLPT";
    public static final String MCU_RETSLPT = "$RETSLPT";

    /**
     * 查询查询终端文件数量
     */
    public static final String MCU_GETFNUM = "$GETFNUM";
    public static final String MCU_RETFUNM = "$RETFUNM";

    /**
     * 查询文件列表
     */
    public static final String MCU_GETLIST = "$GETLIST";
    public static final String MCU_RETLIST = "$RETLIST";

    /**
     * 读取文件
     */
    public static final String MCU_READFILE = "$READFILE";
    public static final String MCU_RETREAD = "$RETREAD";

    /**
     * 写文件
     */
    public static final String MCU_WRITEFILE = "$WRITEFILE";
    public static final String MCU_RETWRITE = "$RETWRITE";
}
