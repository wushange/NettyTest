package com.example.testsocket.utils;

import android.util.Base64;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.example.testsocket.netty.NettyClient;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class FileDownload {

    private final NettyClient client;
    private FileOutputStream fos;
    private String fileName;

    public FileDownload(NettyClient client, String fileName, String filePath) throws IOException {
        this.client = client;
        this.fileName = fileName;
        FileUtils.createOrExistsFile(filePath);
        fos = new FileOutputStream(filePath);
    }

    public void readFile() throws IOException, InterruptedException {
        String command = buildCommand(fileName);
        client.sendData(command);
    }

    public void sendNextChunk(String state, String offset) throws IOException, InterruptedException {
        String command = buildNextCommand(state, offset);
        client.sendData(command);
    }

    public void onAckReceived(String result) {
        LogUtils.e("ACK received: " + result);
        try {
            String[] parts = result.split("\\*");
            if (parts.length != 2) {
                LogUtils.e("Invalid response format");
                return;
            }
            
            String payload = parts[0].substring("$RETWRITE ".length());
            String[] params = payload.split(",");
            
            if (params.length != 4) {
                LogUtils.e("Invalid parameter count");
                return;
            }

            String state = params[0];
            String fileName = params[1];
            long offset = Long.parseLong(params[2]);
            String base64Data = params[3];

            LogUtils.e("Parsed data: " + state + ", " + fileName + ", " + offset + ", " + base64Data);
            byte[] fileData = Base64.decode(base64Data, Base64.DEFAULT);

            if (state.equals("end")) {
                fos.write(fileData);
                fos.close();
                LogUtils.e("File download completed.");
            } else {
                fos.write(fileData);
                sendNextChunk("ok", String.valueOf(offset + fileData.length));
            }
        } catch (Exception e) {
            LogUtils.e("Error processing ACK: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onErrorReceived() {
        LogUtils.e("Error received from server");
        // Handle error, e.g., retry logic or abort transfer
    }

    private String buildCommand(String fileName) {
        String command = String.format("$READFILE %s*", fileName);
        long checksum = calculateChecksum(command);
        return command + checksum + "\r\n";
    }

    private String buildNextCommand(String state, String offset) {
        String command = String.format("$READACK %s,%s*", state, offset);
        long checksum = calculateChecksum(command);
        return command + checksum + "\r\n";
    }

    private long calculateChecksum(String command) {
        CRC32 crc32 = new CRC32();
        crc32.update(command.getBytes());
        return crc32.getValue();
    }
}
