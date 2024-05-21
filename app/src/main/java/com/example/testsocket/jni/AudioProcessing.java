package com.example.testsocket.jni;

public class AudioProcessing {
    static {
        System.loadLibrary("audio_processing");  // 加载 C/C++ 库
    }

    public native double[] getSpectrum(String filePath);
}
