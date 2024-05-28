package com.example.testsocket.message.request;

import com.example.testsocket.message.BaseCommand;
import com.example.testsocket.message.CommandType;

public class WriteFileCommand extends BaseCommand {

    //state,filename,offset,data
    public WriteFileCommand(String state, String filename, int offset, String data) {
        this.command = String.format("%s %s,%s,%s,%s*", CommandType.MCU_WRITEFILE,state,filename,offset,data);
    }
}
