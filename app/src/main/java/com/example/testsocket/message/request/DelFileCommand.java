package com.example.testsocket.message.request;

import com.example.testsocket.message.BaseCommand;
import com.example.testsocket.message.CommandType;

public class DelFileCommand extends BaseCommand {
    private String filename;
    public DelFileCommand(String filename) {
        this.filename = filename;
        this.command = String.format("%s %s*", CommandType.MCU_DELETEFILE, this.filename );
    }
}
