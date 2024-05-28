package com.example.testsocket.message.request;


import com.example.testsocket.message.BaseCommand;
import com.example.testsocket.message.CommandType;

public class VersionCommand extends BaseCommand {

    public VersionCommand( ) {
        this.command = String.format("%s *", CommandType.MCU_GETDVER);
    }

}
