package com.example.testsocket;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SDCardUtils;
import com.example.testsocket.jni.AudioProcessing;
import com.example.testsocket.view.SpectrumView;

public class MainView extends AppCompatActivity {

    private SpectrumView spectrumView;
    private Button loadButton;
    private AudioProcessing audioProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);

        spectrumView = findViewById(R.id.spectrumView);
        loadButton = findViewById(R.id.loadButton);
        audioProcessing = new AudioProcessing();
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        LogUtils.e("onGranted");
                    }

                    @Override
                    public void onDenied() {
                        LogUtils.e("onDenied");
                    }
                }).request();

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wavFilePath = SDCardUtils.getSDCardPathByEnvironment() + "/zzz.wav";
                double[] spectrum = audioProcessing.getSpectrum(wavFilePath);
                if (spectrum != null) {
                    spectrumView.setSpectrum(spectrum);
                } else {
                    LogUtils.e("Failed to load spectrum");
                }
            }
        });
    }
}