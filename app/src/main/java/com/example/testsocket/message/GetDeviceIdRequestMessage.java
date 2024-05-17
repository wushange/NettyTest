package com.example.testsocket.message;

import com.example.testsocket.utils.BBCUtil;

public class GetDeviceIdRequestMessage extends BaseMessage {
    public GetDeviceIdRequestMessage( ) {
        this.cmdname=MessageType.MCU_GETREQI;
        this.checksum = BBCUtil.checkXor(this.cmdname  );
    }

    @Override
    public String buildMessage() {
        return this.cmdname +" "+"*"+ this.checksum + this.lastCmd;
    }
}
