package com.example.testsocket.message.request;

import com.example.testsocket.message.BaseCommand;
import com.example.testsocket.message.CommandType;

public class SetTimeCommand extends BaseCommand {

//    year,month,day,hour,minute,sec
    public SetTimeCommand(String year, String month, String day, String hour, String minute, String sec) {
        this.command = String.format("%s %s,%s,%s,%s,%s,%s*", CommandType.MCU_SETDT,year,month,day,hour,minute,sec);
    }
}
