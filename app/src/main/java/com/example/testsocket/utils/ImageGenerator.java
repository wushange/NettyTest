package com.example.testsocket.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.nio.ByteBuffer;

public class ImageGenerator {

    public static Bitmap generateImageWithTextAndIcon(String text, Bitmap icon) {
        // 图片宽度和高度
        int width = 200;
        int height = 100;

        // 创建一个 ARGB_8888 格式的 Bitmap 对象
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // 创建一个 Canvas 对象，用于在 Bitmap 上绘制内容
        Canvas canvas = new Canvas(bitmap);

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.BLACK); // 设置颜色为黑色
        paint.setTextSize(20); // 设置文字大小
        paint.setTypeface(Typeface.DEFAULT_BOLD); // 设置文字样式为粗体

        // 绘制文字
        canvas.drawText(text, 40, 50, paint); // 在 (40, 50) 处绘制文字

        // 绘制图标
        canvas.drawBitmap(icon, 10, 10, null); // 在 (10, 10) 处绘制图标

        return bitmap;
    }

    public static int[] convertBitmapToPixelData(Bitmap bitmap) {
        // 获取 Bitmap 的宽度和高度
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 创建一个与 Bitmap 大小相同的像素数组
        int[] pixels = new int[width * height];

        // 将 Bitmap 的像素数据复制到像素数组中
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // 返回像素数组
        return pixels;
    }

    public  static  byte[] intArrayToByteArray(int[] intArray) {
        // 计算需要的字节数
        int byteCount = intArray.length * Integer.BYTES;

        // 创建一个 ByteBuffer，用于存储字节数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(byteCount);

        // 将 int 数组中的每个元素转换为字节，并存储到 ByteBuffer 中
        for (int value : intArray) {
            byteBuffer.putInt(value);
        }

        // 获取 ByteBuffer 中的字节数组
        return byteBuffer.array();
    }
}
