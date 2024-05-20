package com.example.testsocket.message.request;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.example.testsocket.message.BaseMessage;
import com.example.testsocket.message.MessageType;
import com.example.testsocket.utils.BBCUtil;

import java.util.PropertyResourceBundle;

public class WriteFileRequestMessage extends BaseMessage {

    //state,filename,offset,data
    private String state;
    private String filename;

    int offset;
    String data;

    public WriteFileRequestMessage(String state, String filename, int offset,String data) {
        this.state = state;
        this.filename = filename;
        this.offset = offset;
        this.data = data;
        this.cmdname= MessageType.MCU_WRITEFILE;
        this.checksum = BBCUtil.checkXor(this.cmdname + ","+ this.state + ","+ this.filename + ","+ this.offset + ","+ this.data);
    }
    //state,filename,offset,data
    @Override
    public String buildMessage() {
        String message = StringUtils.format("%s %s,%s,%s,%s*%s%s", this.cmdname,this.state,this.filename,this.offset,this.data, this.checksum, this.lastCmd );
        LogUtils.e("build cmd: " + message);
        return message;
    }
}
