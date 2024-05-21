package com.example.testsocket.utils;

import android.util.Base64;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.example.testsocket.netty.NettyClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class FileDownload {

    private final NettyClient client;
    private FileOutputStream     fos;
    private String fileName;

    public FileDownload(NettyClient client,String fileName, String filePath) throws IOException {
        this.client = client;
        this.fileName = fileName;
        FileUtils.createOrExistsFile(filePath);
        fos = new FileOutputStream(filePath);

    }

    public void readFile() throws IOException, InterruptedException {
        String command = buildCommand( fileName);
        client.sendData(command);
    }
    public void sendNextChunk(String state,String offset) throws IOException, InterruptedException {
        String command = buildNextCommand( state,offset);
        client.sendData(command);
    }



    public void onAckReceived(String result) {
        LogUtils.e("ACK received.");
        String[] r = result.split(" ");
        String cmd = r[0];
        String params = r[1];
        String state = params.split(",")[0];
        String fileName = params.split(",")[1];
        String offset = params.split(",")[2];
        String base64Data = params.split(",")[3];

        LogUtils.e("解析数据："+cmd +" "+ state + " " + fileName + " " + offset + " " + base64Data);
        if (state.equals("end")) {
            try {
                fos.close();
                sendNextChunk("ok",offset);
                LogUtils.e("File download completed.");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                LogUtils.e("文件偏移："+ offset);
                fos.write(EncodeUtils.base64Decode(base64Data));
                sendNextChunk("ok",offset);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onErrorReceived() {
        LogUtils.e("Error received from server");
        // Handle error, e.g., retry logic or abort transfer
    }

    private String buildCommand( String fileName) {
        String command = String.format("$READFILE %s*", fileName);
        long checksum = calculateChecksum(command);
        return command + checksum +"\r\n";
    }

    private String buildNextCommand( String state,String offset) {
        String command = String.format("$READACK %s,%s*", state,offset);
        long checksum = calculateChecksum(command);
        return command + checksum+"\r\n";
    }


    private long calculateChecksum(String command) {
        CRC32 crc32 = new CRC32();
        crc32.update(command.getBytes());
        return crc32.getValue();
    }
}
