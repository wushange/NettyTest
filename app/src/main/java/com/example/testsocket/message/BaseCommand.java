package com.example.testsocket.message;

import com.example.testsocket.utils.BBCUtil;

public abstract class BaseCommand {

    protected String command;

    protected String lastCmd = "\r\n";


    public String getChecksum(){
        return BBCUtil.checkXor(this.command);
    }
    public String buildCommand(){
        return this.command + this.getChecksum() + this.lastCmd;
    }
}
