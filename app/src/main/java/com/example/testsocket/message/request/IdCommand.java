package com.example.testsocket.message.request;

import com.example.testsocket.message.BaseCommand;
import com.example.testsocket.message.CommandType;

public class IdCommand extends BaseCommand {
    public IdCommand( ) {
        this.command = String.format("%s *", CommandType.MCU_GETREQI);
    }

}
