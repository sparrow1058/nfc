package com.example.hbp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class DrawView extends View {
	 
    public float currentX=40;
    public float currentY=50;
    
    //������дһ�����췽��
    public DrawView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    //��дonDraw����ͨ��Canvas�滭
    @Override
    protected void onDraw(Canvas canvas) {      
        super.onDraw(canvas);
        //��������
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        //����һ��СԲ
        canvas.drawCircle(currentX, currentY, 25, paint);

    }

}