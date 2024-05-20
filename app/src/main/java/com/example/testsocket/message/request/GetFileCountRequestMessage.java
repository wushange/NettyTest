package com.example.testsocket.message.request;

import com.example.testsocket.message.BaseMessage;
import com.example.testsocket.message.MessageType;
import com.example.testsocket.utils.BBCUtil;

public class GetFileCountRequestMessage extends BaseMessage {
    public GetFileCountRequestMessage( ) {
        this.cmdname= MessageType.MCU_GETFNUM;
        this.checksum = BBCUtil.checkXor(this.cmdname  );
    }

    @Override
    public String buildMessage() {
        return this.cmdname +" "+"*"+ this.checksum + this.lastCmd;
    }
}