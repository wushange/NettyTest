package com.example.testsocket;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NumberUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.easysocket.EasySocket;
import com.easysocket.config.EasySocketOptions;
import com.easysocket.entity.OriginReadData;
import com.easysocket.entity.SocketAddress;
import com.easysocket.interfaces.conn.ISocketActionListener;
import com.easysocket.interfaces.conn.SocketActionListener;
import com.easysocket.utils.LogUtil;
import com.example.testsocket.message.GetDeviceIdRequestMessage;
import com.example.testsocket.message.GetVersionRequestMessage;
import com.example.testsocket.netty.NettyClient;
import com.example.testsocket.utils.ImageGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private Context mContext;
    private EditText editTextIp;
    private EditText editTextPort;
    private EditText editTextCommand;
    private Button  btnConnect;
    private  Button btnSend;

    private  Button btnClear;
    private  Button btnBitmap;
    private TextView textViewResponse;
    private Spinner protocolSpinner;
    private ArrayAdapter<String> protocolAdapter;
    private HashMap<String, String> protocolMap;

    private NettyClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        editTextIp = findViewById(R.id.editTextIP);
        editTextPort = findViewById(R.id.editTextPort);
        editTextCommand = findViewById(R.id.editTextCommand);

        btnConnect = findViewById(R.id.buttonConnect);
        btnSend = findViewById(R.id.buttonSend);
        btnClear = findViewById(R.id.buttonClear);
        btnBitmap = findViewById(R.id.buttonBitmap);
        textViewResponse = findViewById(R.id.textViewResponse);

        protocolSpinner = findViewById(R.id.protocol_spinner);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewResponse.setText("");
            }
        });
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = editTextIp.getText().toString();
                int port = Integer.parseInt(editTextPort.getText().toString());
                if (TextUtils.isEmpty(ip) ) {
                    ToastUtils.showShort("请输入ip地址和端口号");
                    return;
                }
                if (client != null) {
                    client.stop();
                }
                client = new NettyClient(ip, port);
                client.setHandler(handler);
                client.start();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg =  editTextCommand.getText().toString();
                textViewResponse.append("\r\n发送消息"+msg);
                if (client != null) {
                    client.sendData(msg);
                }
//                LogUtils.e("msg 1 : "+ msg);
//                byte[] bb = ConvertUtils.string2Bytes(msg);
//                LogUtils.e("msg byte[] : "+bb);
//                EasySocket.getInstance().upMessage(bb);
            }
        });
        btnBitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap  = ImageGenerator.generateImageWithTextAndIcon("张医生的听诊器", ImageUtils.getBitmap(R.drawable.ic_launcher_background));
                int[] bitmapbyte = ImageGenerator.convertBitmapToPixelData(bitmap);

                byte[] data = ImageGenerator.intArrayToByteArray(bitmapbyte);
                if (client != null) {
                    client.sendData(data);
                }
            }
        });
        // 初始化协议选项
        initializeProtocols();
        // 设置下拉框适配器
        protocolAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        protocolAdapter.addAll(protocolMap.keySet());
        protocolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        protocolSpinner.setAdapter(protocolAdapter);
        // 设置下拉框选择监听器
        protocolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String protocol = protocolAdapter.getItem(position);
                String commandFormat = protocolMap.get(protocol);
                editTextCommand.setText(commandFormat);
                LogUtils.e("Selected protocol: " + protocol + ", command format: " + commandFormat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        protocolSpinner.setSelection(0);
    }

    /**
     * 初始化EasySocket
     */
    private void initEasySocket(String SERVER_IP,int SERVER_PORT) {

        //socket配置
        EasySocketOptions options = new EasySocketOptions.Builder()
                .setSocketAddress(new SocketAddress(SERVER_IP, SERVER_PORT)) //主机地址
                // 强烈建议定义一个消息协议，方便解决 socket黏包、分包的问题
                // .setReaderProtocol(new DefaultMessageProtocol()) // 默认的消息协议
                .build();

        //初始化EasySocket
        EasySocket.getInstance().createConnection(options,mContext);
        EasySocket.getInstance().subscribeSocketAction(socketActionListener);
    }
    private void initializeProtocols() {
        // 在这里添加你需要调试的通信协议及其对应的命令格式
        protocolMap = new HashMap<>();
        protocolMap.put("查询听诊器 ID", new GetDeviceIdRequestMessage().buildMessage());
        protocolMap.put("查询终端版本号", new GetVersionRequestMessage().buildMessage());
        // 添加更多协议...
    }



    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            LogUtils.e(msg.obj);
            switch (msg.what) {
                case 203:
                    textViewResponse.append("\r\n客户端消息：" + msg.obj);
                    break;
                case 1:
                    String result = ConvertUtils.bytes2String((byte[]) msg.obj);
                    textViewResponse.append("\r\n服务端返回消息："+result);
                    break;
            }

        }
    };
    private ISocketActionListener socketActionListener = new SocketActionListener() {
        @Override
        public void onSocketConnSuccess(SocketAddress socketAddress) {
            super.onSocketConnSuccess(socketAddress);
            LogUtil.e("连接成功"+socketAddress.toString());
            textViewResponse.append("\r\n"+"连接成功"+socketAddress.getIp());
            ToastUtils.showLong("连接服务器成功！");
        }

        @Override
        public void onSocketConnFail(SocketAddress socketAddress, boolean isNeedReconnect) {
            super.onSocketConnFail(socketAddress, isNeedReconnect);
            textViewResponse.append("\r\n"+"连接失败"+socketAddress.getIp());
            LogUtil.e("onSocketConnFail"+socketAddress.toString());
        }

        @Override
        public void onSocketDisconnect(SocketAddress socketAddress, boolean isNeedReconnect) {
            super.onSocketDisconnect(socketAddress, isNeedReconnect);
            textViewResponse.append("\r\n"+"断开连接"+socketAddress.getIp());
            LogUtil.e("onSocketDisconnect"+socketAddress.toString());
        }

        @Override
        public void onSocketResponse(SocketAddress socketAddress, OriginReadData originReadData) {
            super.onSocketResponse(socketAddress, originReadData);
            LogUtil.e("onSocketResponse OriginReadData:"+originReadData.getBodyString());
            textViewResponse.append("\r\n"+originReadData.getBodyString());
        }

        @Override
        public void onSocketResponse(SocketAddress socketAddress, byte[] readData) {
            super.onSocketResponse(socketAddress, readData);
            LogUtil.e("onSocketResponse byte[] :"+  ConvertUtils.bytes2String(readData));
        }

        @Override
        public void onSocketResponse(SocketAddress socketAddress, String readData) {
            super.onSocketResponse(socketAddress, readData);
            LogUtil.e("onSocketResponse String :"+readData);
        }
    };
}
