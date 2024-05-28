package com.example.testsocket.message.request;

import com.example.testsocket.message.BaseCommand;
import com.example.testsocket.message.CommandType;

public class ReadFileCommand extends BaseCommand {

    public ReadFileCommand(String filename ) {
        this.command = String.format("%s %s*", CommandType.MCU_READFILE,filename);
    }

}
