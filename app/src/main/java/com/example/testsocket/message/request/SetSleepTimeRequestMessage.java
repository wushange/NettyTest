package com.example.testsocket.message.request;

import com.example.testsocket.message.BaseMessage;
import com.example.testsocket.message.MessageType;
import com.example.testsocket.utils.BBCUtil;

public class SetSleepTimeRequestMessage extends BaseMessage {

    private int seconds;
    public SetSleepTimeRequestMessage( int seconds) {
        this.seconds = seconds;
        this.cmdname= MessageType.MCU_SETSLPT;
        this.checksum = BBCUtil.checkXor(this.cmdname +" " +this.seconds );
    }

    @Override
    public String buildMessage() {
        return this.cmdname +" "+"*"+ this.checksum + this.lastCmd;
    }
}
