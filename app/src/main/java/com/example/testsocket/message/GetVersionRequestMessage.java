package com.example.testsocket.message;


import com.example.testsocket.utils.BBCUtil;

import java.io.Serializable;

public class GetVersionRequestMessage extends BaseMessage   {

    public GetVersionRequestMessage( ) {
        this.cmdname=MessageType.MCU_GETDVER;
        this.checksum = BBCUtil.checkXor(this.cmdname  );
    }

    @Override
    public String buildMessage() {
        return this.cmdname +" "+"*"+ this.checksum + this.lastCmd;
    }
}
