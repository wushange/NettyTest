package com.example.testsocket.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class SpectrumView extends View {

    private Paint paint;
    private double[] spectrum;

    public SpectrumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
    }

    public void setSpectrum(double[] spectrum) {
        this.spectrum = spectrum;
        invalidate();  // 请求重新绘制
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (spectrum != null) {
            int width = getWidth();
            int height = getHeight();
            int barWidth = width / spectrum.length;

            for (int i = 0; i < spectrum.length; i++) {
                int barHeight = (int) (spectrum[i] * height);  // Scale to view height
                canvas.drawRect(i * barWidth, height - barHeight, (i + 1) * barWidth, height, paint);
            }
        }
    }
}
