package com.example.testsocket;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.example.testsocket.databinding.ActivityMainBinding;
import com.example.testsocket.message.CommandType;
import com.example.testsocket.message.request.DelFileCommand;
import com.example.testsocket.message.request.FileCountCommand;
import com.example.testsocket.message.request.FileListCommand;
import com.example.testsocket.message.request.IdCommand;
import com.example.testsocket.message.request.SetSleepCommand;
import com.example.testsocket.message.request.SetTimeCommand;
import com.example.testsocket.message.request.VersionCommand;
import com.example.testsocket.netty.MessageType;
import com.example.testsocket.netty.NettyClient;
import com.example.testsocket.utils.FileDownload;
import com.example.testsocket.utils.FileTransfer;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 0;
    ActivityMainBinding binding;
    private Context mContext;

    FileTransfer fileTransfer;
    FileDownload fileDownload;
    private NettyClient client;

    List<String[]> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;
        PermissionUtils.permission(PermissionConstants.STORAGE, PermissionConstants.PHONE).request();
        binding.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (client != null) {
                    client.stop();
                }
                client = new NettyClient("192.168.4.1", 333);
                client.setHandler(handler);
                client.start();
            }
        });
        binding.btnGetId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IdCommand idCommand = new IdCommand();
                sendCommand(idCommand.buildCommand());
            }
        });
        binding.btnGetVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VersionCommand versionCommand = new VersionCommand();
                sendCommand(versionCommand.buildCommand());
            }
        });
        binding.btnSyncTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nowDate = TimeUtils.getNowString();
                String[] date = nowDate.split(" ");
                String[] ymd = date[0].split("-");
                String[] hms = date[1].split(":");
                String year = ymd[0].substring(2);
                String month = ymd[1];
                String day = ymd[2];
                String hour = hms[0];
                String minute = hms[1];
                String second = hms[2];
                SetTimeCommand setTimeCommand = new SetTimeCommand(year, month, day, hour, minute, second);
                sendCommand(setTimeCommand.buildCommand());

            }
        });

        binding.btnSetSleepTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetSleepCommand setSleepCommand = new SetSleepCommand(10);
                sendCommand(setSleepCommand.buildCommand());
            }
        });
        binding.btnGetFileCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileCountCommand fileCountCommand = new FileCountCommand();
                sendCommand(fileCountCommand.buildCommand());
            }
        });

        binding.btnGetFileList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileListCommand fileListCommand = new FileListCommand();
                sendCommand(fileListCommand.buildCommand());
            }
        });

        binding.btnUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(
                            Intent.createChooser(intent, "Select a file"),
                            FILE_SELECT_CODE
                    );
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    ToastUtils.showShort("Please install a File Manager.");
                }
            }
        });

        binding.btnDownloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fileList == null || fileList.size() <= 0) {
                    ToastUtils.showShort("请先获取文件列表");
                    return;
                }
                List<String> fileNames = new ArrayList<>();
                for (String[] strings : fileList) {
                    fileNames.add(strings[0]);
                }

                new XPopup.Builder(mContext)
                        .atView(view)
                        .asAttachList(fileNames.toArray(new String[0]), null, new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                String fileName = fileList.get(position)[0];
                                String filePath = PathUtils.getExternalAppFilesPath() + File.separator + fileName;
                                try {
                                    fileDownload = new FileDownload(client, fileName, filePath, new FileDownload.FileDownloadListener() {
                                        @Override
                                        public void onFileDownloadComplete() {
                                            ToastUtils.showShort("文件下载完成");
                                            binding.tvContent.setText(fileName + " : 文件下载完成-->>" + filePath);
                                        }
                                    });
                                    fileDownload.readFile();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }).show();


            }
        });
        binding.btnDeleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileList == null || fileList.size() <= 0) {
                    ToastUtils.showShort("请先获取文件列表");
                    return;
                }
                List<String> fileNames = new ArrayList<>();
                for (String[] strings : fileList) {
                    fileNames.add(strings[0]);
                }
                new XPopup.Builder(mContext)
                        .atView(view)
                        .asAttachList(fileNames.toArray(new String[0]), null, new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                String fileName = fileList.get(position)[0];
                                DelFileCommand delFileCommand = new DelFileCommand(fileName);
                                sendCommand(delFileCommand.buildCommand());
                            }
                        }).show();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                File file = UriUtils.uri2File(uri);
                String path = file.getAbsolutePath();

                try {
                    fileTransfer = new FileTransfer(client, path, new FileTransfer.FileTransferListener() {
                        @Override
                        public void onFileTransferComplete() {
                            ToastUtils.showShort("文件上传完成");
                            binding.tvContent.setText(file.getName() + " : 文件上传完成");
                        }
                    });
                    fileTransfer.sendFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ToastUtils.showShort("选择文件地址：" + path);
            }
        }
    }

    public void sendCommand(String command) {
        if (client != null) {
            client.sendData(command);
        } else {
            ToastUtils.showShort("请先连接服务端");
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MessageType.CLIENT_CONNECT_SUCCESS:
                    binding.tvContent.setText("\r\n客户端连接服务端成功");
                    ToastUtils.showShort("客户端连接服务端成功");
                    break;
                case MessageType.RECEIVE_DATA:
                    String result = ConvertUtils.bytes2String((byte[]) msg.obj);
                    String[] raw = result.split(" ");
                    String command = raw[0];
                    String data = raw[1].split("\\*")[0];
                    LogUtils.e("接收到数据：" + data);
                    switch (command) {
                        case CommandType.MCU_RETDVER:
                            binding.tvContent.setText("\r\n服务端返回版本：" + data);
                            break;
                        case CommandType.MCU_RETREQI:
                            binding.tvContent.setText("\r\n服务端返回ID：" + data);
                            break;
                        case CommandType.MCU_RETDT:
                            binding.tvContent.setText("\r\n服务端返回时间：" + data);
                            break;
                        case CommandType.MCU_RETSLPT:
                            binding.tvContent.setText("\r\n服务端返回休眠时间：" + data);
                            break;
                        case CommandType.MCU_RETFUNM:
                            binding.tvContent.setText("\r\n服务端返回文件数量：" + data);
                            break;
                        case CommandType.MCU_RETLIST:
                            String input = data;
                            List<String[]> resultList = new ArrayList<>();
                            input = input.replaceAll("\\[|\\]", ""); // Remove brackets
                            String[] items = input.split(",");

                            // Skip the first two elements (11,11)
                            for (int i = 2; i < items.length; i += 2) {
                                String[] pair = {items[i], items[i + 1]};
                                resultList.add(pair);
                            }
                            LogUtils.e("文件列表：" + resultList.toString());
                            fileList = resultList;

                            StringBuilder formattedString = new StringBuilder();
                            for (String[] items1 : resultList) {
                                formattedString.append(items1[0]).append(", ").append(items1[1]).append("\n");
                            }
                            String ok = formattedString.toString();
                            binding.tvContent.setText("\r\n服务端返回文件列表：" + ok);
                            break;
                        case CommandType.MCU_RETREAD:
                            binding.tvContent.setText("\r\n文件下载中：" + data);
                            fileDownload.onAckReceived(data);
                            break;
                        case CommandType.MCU_RETWRITE:
                            binding.tvContent.setText("\r\n上传文件中：" + data);
                            if (result.contains("ok")) {
                                LogUtils.e("继续发送文件");
                                if (fileTransfer != null) {
                                    fileTransfer.onAckReceived();
                                } else {
                                    ToastUtils.showShort("请先上传文件");
                                }
                            }
                            break;
                        case CommandType.MCU_RETDELETEFILE:
                            binding.tvContent.setText("\r\n删除文件：" + data);
                            break;

                    }
                    break;
                default:
                    break;
            }

        }
    };

    public List<String[]> parseString(String input) {
        List<String[]> resultList = new ArrayList<>();
        input = input.replace("[", "").replace("]", ""); // Remove brackets
        String[] pairs = input.split("(?<=\\}),"); // Split by comma followed by a closing bracket

        for (String pair : pairs) {
            String[] items = pair.split(",");
            resultList.add(items);
        }
        return resultList;
    }

    public String formatList(List<String[]> list) {
        StringBuilder formattedString = new StringBuilder();
        for (String[] items : list) {
            formattedString.append(items[0]).append(", ").append(items[1]).append("\n");
        }
        return formattedString.toString();
    }
}
