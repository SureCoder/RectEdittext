package com.huoqiu.shuoli.rectedittext;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by shuoli on 2017/1/5.
 */

public class RectangleBoxEditText extends EditText {
    //画笔
    private Paint yellowPaint;
    private Paint boxBackgroundPaint;
    private Paint numberPaint;

    private int mWidth;
    private int mHeight;
    //需要的自定义颜色
    private int boxStokeColor;          //每个box的边框颜色，这里是黄色
    private int boxBackgroundColor;     //每个box内部的颜色，这里是灰色
    private int numberTextColor;        //数字的颜色

    private int boxNumber;//box的数量，也就是验证码的长度
    private int boxPadding;

    private int averageBoxWidth; //每个Box的宽
    private String smsCode; //验证码
    private Rect smsRect = new Rect();
    private Paint.FontMetricsInt fm;

    public RectangleBoxEditText(Context context) {
        this(context, null);
    }

    public RectangleBoxEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectangleBoxEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RectangleBoxEditText, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.RectangleBoxEditText_boxBackgroundColor:
                    boxBackgroundColor = a.getColor(R.styleable.RectangleBoxEditText_boxBackgroundColor, Color.RED);//默认是白色
                    break;
                case R.styleable.RectangleBoxEditText_boxStokeColor:
                    boxStokeColor = a.getColor(attr, Color.GRAY);//默认是灰色
                    break;
                case R.styleable.RectangleBoxEditText_numberTextColor:
                    numberTextColor = a.getColor(attr, Color.BLACK);//默认黑色
                    break;
                case R.styleable.RectangleBoxEditText_boxCount:
                    boxNumber = a.getInt(attr, 6);//默认为6个Box
                    break;
                case R.styleable.RectangleBoxEditText_boxPadding:
                    boxPadding = a.getDimensionPixelSize(attr, 0);//默认为没间距
                    break;
            }
        }
        a.recycle();
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        averageBoxWidth = (mWidth - (boxNumber - 1) * boxPadding) / boxNumber;

    }

    private void init() {
        yellowPaint = new Paint();
        yellowPaint.setAntiAlias(true);
        yellowPaint.setColor(boxStokeColor);
        yellowPaint.setStyle(Paint.Style.STROKE);
        yellowPaint.setStrokeWidth(6);
        numberPaint = new Paint();
        numberPaint.setFakeBoldText(true);
        numberPaint.setTextSize(20);
        numberPaint.setAntiAlias(true);
        numberPaint.setColor(numberTextColor);
        numberPaint.setStyle(Paint.Style.FILL);
        numberPaint.setTextSize(getTextSize());
        numberPaint.setTextAlign(Paint.Align.CENTER);
        boxBackgroundPaint = new Paint();
        boxBackgroundPaint.setAntiAlias(true);
        boxBackgroundPaint.setColor(boxBackgroundColor);
        boxBackgroundPaint.setStyle(Paint.Style.FILL);
        fm = numberPaint.getFontMetricsInt();

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        for (int i = 0; i < boxNumber; i++) {
            int left = i * averageBoxWidth + i * boxPadding;
            int top = 0;
            int right = (i + 1) * averageBoxWidth + i * boxPadding;
            int bottom = mHeight;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(left, top, right, bottom, DisplayUtils.dp2px(getContext(), 4), DisplayUtils.dp2px(getContext(), 4), boxBackgroundPaint);
            } else {
                canvas.drawRect(left, top, right, bottom, boxBackgroundPaint);
            }
        }
        if (!TextUtils.isEmpty(smsCode)) {
            for (int i = 0; i < smsCode.length(); i++) {
                numberPaint.getTextBounds(smsCode, i, i + 1, smsRect);
                canvas.drawText(smsCode, i, i + 1, (averageBoxWidth / 2) + averageBoxWidth * i + i * boxPadding, mHeight / 2 - fm.descent + (fm.bottom - fm.top) / 2, numberPaint);
                int left = i * averageBoxWidth + i * boxPadding;
                int top = 0;
                int right = (i + 1) * averageBoxWidth + i * boxPadding;
                int bottom = mHeight;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    canvas.drawRoundRect(left+DisplayUtils.dp2px(getContext(), 2), top+DisplayUtils.dp2px(getContext(), 2), right-DisplayUtils.dp2px(getContext(), 2), bottom-DisplayUtils.dp2px(getContext(), 2), DisplayUtils.dp2px(getContext(), 2), DisplayUtils.dp2px(getContext(), 2), yellowPaint);
                } else {
                    canvas.drawRect(left, top, right, bottom, yellowPaint);
                }
            }

        }

    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (text.length() != 0) {
            smsCode = text.toString();
        } else {
            smsCode = "";
        }
        if (text.length() == boxNumber) {
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
        }
        postInvalidate();
    }
}
