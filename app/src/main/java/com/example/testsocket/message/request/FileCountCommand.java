package com.example.testsocket.message.request;

import com.example.testsocket.message.BaseCommand;
import com.example.testsocket.message.CommandType;

public class FileCountCommand extends BaseCommand {
    public FileCountCommand( ) {
        this.command = String.format("%s *", CommandType.MCU_GETFNUM);
    }

}
