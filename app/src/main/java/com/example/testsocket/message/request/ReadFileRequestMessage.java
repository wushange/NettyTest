package com.example.testsocket.message.request;

import com.example.testsocket.message.BaseMessage;
import com.example.testsocket.message.MessageType;
import com.example.testsocket.utils.BBCUtil;

public class ReadFileRequestMessage extends BaseMessage {

    private String filename;
    public ReadFileRequestMessage( String filename ) {
        this.cmdname= MessageType.MCU_READFILE;
        this.filename = filename;
        this.checksum = BBCUtil.checkXor(this.cmdname +" "+ this.filename );
    }

    @Override
    public String buildMessage() {
        return this.cmdname +" "+"*"+ this.checksum + this.lastCmd;
    }
}
