package com.example.testsocket.utils;

import android.util.Base64;

import com.blankj.utilcode.util.LogUtils;
import com.example.testsocket.netty.NettyClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

public class FileTransfer {

    private static final int BLOCK_SIZE = 1024;
    private final NettyClient client;
    private final String filePath;
    private FileInputStream fis;
    private long fileSize;
    private long offset;

    public FileTransfer(NettyClient client, String filePath) throws IOException {
        this.client = client;
        this.filePath = filePath;
        File file = new File(filePath);
        this.fis = new FileInputStream(file);
        this.fileSize = file.length();
        this.offset = 0;
    }

    public void sendFile() throws IOException, InterruptedException {
        sendNextChunk();
    }

    private void sendNextChunk() throws IOException, InterruptedException {
        byte[] buffer = new byte[BLOCK_SIZE];
        int bytesRead = fis.read(buffer);

        if (bytesRead == -1) {
            fis.close();
            LogUtils.e("File transfer completed.");
            return;
        }

        String state = (offset + bytesRead == fileSize) ? "end" : "data";
        String base64Data = Base64.encodeToString(buffer, 0, bytesRead, Base64.NO_WRAP);
        String command = buildCommand(state, new File(filePath).getName(), offset, base64Data);

        client.sendData(command);
        LogUtils.e("Sent command: " + command);
        offset += bytesRead;
    }

    public void onAckReceived() {
        LogUtils.e("ACK received.");
        try {
            sendNextChunk();
        } catch (IOException | InterruptedException e) {
            LogUtils.e("Error sending next chunk: " + e.getMessage());
        }
    }

    public void onErrorReceived() {
        LogUtils.e("Error received from server");
        // Handle error, e.g., retry logic or abort transfer
    }

    private String buildCommand(String state, String fileName, long offset, String data) {
        String command = String.format("$WRITEFILE %s,%s,%d,%s*", state, fileName, offset, data);
        long checksum = calculateChecksum(command);
        return command + checksum + "\r\n";
    }

    private long calculateChecksum(String command) {
        CRC32 crc32 = new CRC32();
        crc32.update(command.getBytes());
        return crc32.getValue();
    }
}
