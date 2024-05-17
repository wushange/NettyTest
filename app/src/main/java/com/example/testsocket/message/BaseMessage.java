package com.example.testsocket.message;

public abstract class BaseMessage {

    public String cmdname;
    public String status;
    public int errornum;
    public String checksum;
    public String lastCmd = "\r\n";

    public abstract String buildMessage();
}
