package com.example.testsocket.message.request;

import com.example.testsocket.message.BaseCommand;
import com.example.testsocket.message.CommandType;

public class SetSleepCommand extends BaseCommand {

    private int seconds;
    public SetSleepCommand(int seconds) {
        this.command = String.format("%s %s*", CommandType.MCU_SETSLPT,seconds);
    }
}
