package com.example.testsocket.message.request;

import com.example.testsocket.message.BaseMessage;
import com.example.testsocket.message.MessageType;
import com.example.testsocket.utils.BBCUtil;

public class SetDateTimeRequestMessage extends BaseMessage {

//    year,month,day,hour,minute,sec
    private String year,month,day,hour,minute,sec;
    public SetDateTimeRequestMessage( String year,  String month,  String day,  String hour,  String minute,  String sec) {
        this.cmdname= MessageType.MCU_SETDT;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute  = minute;
        this.sec = sec;
        this.checksum = BBCUtil.checkXor(this.cmdname+" "+ this.year+","+ this.month+","+ this.day+","+ this.hour+","+ this.minute+","+ this.sec );
    }

    @Override
    public String buildMessage() {
        return this.cmdname +" "+"*"+ this.checksum + this.lastCmd;
    }
}
