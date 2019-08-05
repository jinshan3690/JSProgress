package com.js.smart.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class JSProgressBar extends View {

    /**
     * 进度条所占用的角度
     */
    private float ARC_FULL_DEGREE;
    /**
     * 弧线的宽度
     */
    private float STROKE_WIDTH;
    /**
     * 按钮的宽度
     */
    private float BUTTON_WIDTH;
    /**
     * 顶部偏移
     */
    private int OFFSET_TOP;
    /**
     * 进度条最大值和当前进度值
     */
    private float max, progress;
    /**
     * 分段精度值
     */
    private float[] steps;
    /**
     * 分段进度
     */
    private float stepProgress;
    /**
     * 分段的刻度长
     */
    private float STEP_LINE_LENGTH;
    /**
     * 分段的刻度宽
     */
    private float STEP_LINE_WIDTH;
    /**
     * 分段的刻度偏移
     */
    private float STEP_LINE_OFFSET;
    /**
     * 是否允许拖动进度条
     */
    private boolean draggingEnabled;
    /**
     * 进度条颜色
     */
    private String[] progressColorsStr = new String[0];
    /**
     * 进度条颜色
     */
    private int[] progressColors = new int[0];
    /**
     * 进度条颜色梯度
     */
    private float[] progressGradient = new float[0];
    /**
     * 进度条颜色发光
     */
    private boolean progressLight;
    /**
     * 进度条颜色圆角
     */
    private boolean progressRound;
    /**
     * 进度条颜色
     */
    private String[] progressBackgroundColorsStr = new String[0];
    /**
     * 进度条颜色
     */
    private int[] progressBackgroundColors = new int[0];

    /**
     * 进度条颜色梯度
     */
    private float[] progressBackgroundGradient = new float[0];
    /**
     * 进度条文字颜色
     */
    private String[] progressTextColorsStr = new String[0];
    /**
     * 进度条文字颜色
     */
    private int[] progressTextColors = new int[0];
    /**
     * 进度条颜色梯度
     */
    private float[] progressTextGradient = new float[0];
    /**
     * 进度条文字大小
     */
    private float progressTextSize;
    /**
     * 进度条提示文字
     */
    private String progressTextHint;
    /**
     * 进度条提示文字大小
     */
    private float progressTextHintSize;

    /**
     * 组件的宽，高
     */
    private int width, height;
    /**
     * 当前角度
     */
    private float lastDegree;
    /**
     * 绘制弧线的矩形区域
     */
    private RectF circleRectF;
    /**
     * 文字的矩形区域
     */
    private Rect textBounds = new Rect();
    /**
     * 绘制弧线的画笔
     */
    private Paint progressPaint;
    /**
     * 绘制分段刻度画笔
     */
    private Paint stepProgressPaint;
    /**
     * 绘制弧线背景的画笔
     */
    private Paint progressBackgroundPaint;
    /**
     * 绘制文字的画笔
     */
    private Paint textPaint;
    /**
     * 绘制当前进度值的画笔
     */
    private Paint thumbPaint;
    /**
     * 圆弧的半径
     */
    private int circleRadius;
    /**
     * 圆弧圆心位置
     */
    private int centerX, centerY;
    /**
     * 是否拖拽
     */
    private boolean isDragging = false;


    public JSProgressBar(Context context) {
        this(context, null);
    }


    public JSProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public JSProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JSProgressBar);

        ARC_FULL_DEGREE = typedArray.getInteger(R.styleable.JSProgressBar_js_pb_degree, 180);
        STROKE_WIDTH = typedArray.getDimension(R.styleable.JSProgressBar_js_pb_stroke_width, 0);
        BUTTON_WIDTH = typedArray.getDimension(R.styleable.JSProgressBar_js_pb_button_width, 0);
        OFFSET_TOP = (int) typedArray.getDimension(R.styleable.JSProgressBar_js_pb_offset_top, 0);
        max = typedArray.getInteger(R.styleable.JSProgressBar_js_pb_max, 100);
        progress = typedArray.getInteger(R.styleable.JSProgressBar_js_pb_progress, 0);
        String step = typedArray.getString(R.styleable.JSProgressBar_js_pb_steps);
        steps = stringToFloatArray(step);
        STEP_LINE_LENGTH = typedArray.getDimension(R.styleable.JSProgressBar_js_pb_step_line_length, 0);
        STEP_LINE_WIDTH = typedArray.getDimension(R.styleable.JSProgressBar_js_pb_step_line_width, 0);
        STEP_LINE_OFFSET = typedArray.getDimension(R.styleable.JSProgressBar_js_pb_step_line_offset, 0);
        draggingEnabled = typedArray.getBoolean(R.styleable.JSProgressBar_js_pb_drag, false);
        String colors = typedArray.getString(R.styleable.JSProgressBar_js_pb_colors);
        if (!TextUtils.isEmpty(colors)) {
            progressColorsStr = colors.split(",");
            progressColors = stringToColors(colors);
            progressGradient = stringToGradient(colors);
        }
        progressLight = typedArray.getBoolean(R.styleable.JSProgressBar_js_pb_color_light, true);
        progressRound = typedArray.getBoolean(R.styleable.JSProgressBar_js_pb_round, true);
        colors = typedArray.getString(R.styleable.JSProgressBar_js_pb_background_colors);
        if (!TextUtils.isEmpty(colors)) {
            progressBackgroundColorsStr = colors.split(",");
            progressBackgroundColors = stringToColors(colors);
            progressBackgroundGradient = stringToGradient(colors);
        }
        colors = typedArray.getString(R.styleable.JSProgressBar_js_pb_text_colors);
        if (!TextUtils.isEmpty(colors)) {
            progressTextColorsStr = colors.split(",");
            progressTextColors = stringToColors(colors);
            progressTextGradient = stringToGradient(colors);
        }

        progressTextSize = typedArray.getDimension(R.styleable.JSProgressBar_js_pb_text_size, 0);
        progressTextHint = typedArray.getString(R.styleable.JSProgressBar_js_pb_text_hint);
        if (TextUtils.isEmpty(progressTextHint))
            progressTextHint = "";
        progressTextHintSize = typedArray.getDimension(R.styleable.JSProgressBar_js_pb_text_hint_size, 0);

        lastDegree = ARC_FULL_DEGREE * (progress / max);
        ARC_FULL_DEGREE = ARC_FULL_DEGREE >= 360 ? 359 : ARC_FULL_DEGREE;
        if (steps != null)
            stepProgress = max / steps.length;
    }


    private void init() {
        //进度条画笔
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(STROKE_WIDTH);
        progressPaint.setStrokeCap(progressRound ? Paint.Cap.ROUND : Paint.Cap.SQUARE);
        if (progressLight)
            progressPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));//设置发光

        //进度条背景画笔
        progressBackgroundPaint = new Paint();
        progressBackgroundPaint.setAntiAlias(true);
        progressBackgroundPaint.setStyle(Paint.Style.STROKE);
        progressBackgroundPaint.setStrokeWidth(STROKE_WIDTH);
        progressBackgroundPaint.setStrokeCap(progressRound ? Paint.Cap.ROUND : Paint.Cap.SQUARE);

        //分段画笔
        stepProgressPaint = new Paint();
        stepProgressPaint.setAntiAlias(true);
        stepProgressPaint.setStyle(Paint.Style.STROKE);
        stepProgressPaint.setStrokeCap(progressRound ? Paint.Cap.ROUND : Paint.Cap.SQUARE);
        stepProgressPaint.setStrokeWidth(STEP_LINE_WIDTH);
        if (progressLight)
            stepProgressPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));//设置发光

        //文字画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);

        //按钮画笔
        thumbPaint = new Paint();
        thumbPaint.setAntiAlias(true);

        if (progressColors.length != 0)
            if (progressColors.length > 2) {
                LinearGradient gradient = new LinearGradient(STROKE_WIDTH / 2f, OFFSET_TOP, STROKE_WIDTH / 2f, height + OFFSET_TOP,
                        progressColors, progressGradient, Shader.TileMode.REPEAT);

                progressPaint.setShader(gradient);
                textPaint.setShader(gradient);
            } else {
                progressPaint.setColor(progressColors[0]);
                textPaint.setColor(progressColors[0]);
            }


        if (progressBackgroundColors.length != 0)
            if (progressBackgroundColors.length > 2) {
                LinearGradient gradient = new LinearGradient(STROKE_WIDTH / 2f, OFFSET_TOP, STROKE_WIDTH / 2f, height+ OFFSET_TOP,
                        progressBackgroundColors, progressBackgroundGradient, Shader.TileMode.REPEAT);

                stepProgressPaint.setShader(gradient);
                progressBackgroundPaint.setShader(gradient);
            } else {
                stepProgressPaint.setColor(progressBackgroundColors[0]);
                progressBackgroundPaint.setColor(progressBackgroundColors[0]);
            }

        if (progressTextColors.length != 0) {
            textPaint.setShader(null);
            textPaint.setColor(0);
            if (progressTextColors.length > 2) {
                LinearGradient gradient = new LinearGradient(STROKE_WIDTH / 2f, OFFSET_TOP, STROKE_WIDTH / 2f, height + OFFSET_TOP,
                        progressTextColors, progressTextGradient, Shader.TileMode.REPEAT);
                textPaint.setShader(gradient);
            } else {
                textPaint.setColor(progressTextColors[0]);
            }
        }

    }

    /**
     * 初始化参数
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (width == 0 || height == 0) {
            width = w;
            height = h;

            //计算圆弧半径和圆心点
            circleRadius = Math.min(width, height) / 2;
            if (STROKE_WIDTH == 0)
                STROKE_WIDTH = circleRadius / 12;
            if (BUTTON_WIDTH == 0)
                BUTTON_WIDTH = STROKE_WIDTH * 2.0f;
            if (STEP_LINE_LENGTH == 0)
                STEP_LINE_LENGTH = circleRadius / 6;
            if (STEP_LINE_WIDTH == 0)
                STEP_LINE_WIDTH = STEP_LINE_LENGTH / 8;
            if (STEP_LINE_OFFSET == 0)
                STEP_LINE_OFFSET = STROKE_WIDTH;

            circleRadius -= BUTTON_WIDTH > STROKE_WIDTH ? BUTTON_WIDTH : STROKE_WIDTH;

            centerX = width / 2;
            centerY = height / 2 +OFFSET_TOP;

            //圆弧所在矩形区域
            circleRectF = new RectF();
            circleRectF.left = centerX - circleRadius;
            circleRectF.top = centerY - circleRadius;
            circleRectF.right = centerX + circleRadius;
            circleRectF.bottom = centerY + circleRadius;

            init();
        }
    }

    /**
     * 界面绘制
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float start = 90 + ((int) (360 - ARC_FULL_DEGREE) >> 1); //进度条起始点
        float sweep1 = ARC_FULL_DEGREE * (progress / max); //进度划过的角度
        float sweep2 = ARC_FULL_DEGREE - sweep1; //剩余的角度

        //绘制进度条
        canvas.drawArc(circleRectF, start, sweep1, false, progressPaint);
        //绘制进度条背景
        canvas.drawArc(circleRectF, start + sweep1, sweep2, false, progressBackgroundPaint);

        //绘制分段
        if (steps.length > 0) {
            float drawDegree = ARC_FULL_DEGREE * 1.0f / (steps.length);
            while (drawDegree < ARC_FULL_DEGREE) {
                double a = (180 - ARC_FULL_DEGREE /2  + drawDegree) / 180 * Math.PI;
                float lineStartX = centerX - (circleRadius -STEP_LINE_OFFSET) * (float) Math.sin(a);
                float lineStartY = centerY + (circleRadius -STEP_LINE_OFFSET) * (float) Math.cos(a);
                float lineStopX = lineStartX + STEP_LINE_LENGTH * (float) Math.sin(a);
                float lineStopY = lineStartY - STEP_LINE_LENGTH * (float) Math.cos(a);

                canvas.drawLine(lineStartX, lineStartY, lineStopX, lineStopY, stepProgressPaint);

                drawDegree += ARC_FULL_DEGREE * 1.0f / (steps.length);
            }
        }

        //上一行文字
        textPaint.setTextSize(progressTextSize == 0 ? circleRadius >> 1 : progressTextSize);
        String text = (int) (100 * progress / max) + "";
        float textLen = textPaint.measureText(text);
        //计算文字高度
        textPaint.getTextBounds("8", 0, 1, textBounds);
        float h1 = textBounds.height();
        //% 前面的数字水平居中，适当调整
        float extra = text.startsWith("1") ? -textPaint.measureText("1") / 2 : 0;
        canvas.drawText(text, centerX - textLen / 2 + extra, centerY - 30 + h1 / 2, textPaint);

        //百分号
        textPaint.setTextSize(progressTextSize == 0 ? circleRadius >> 2 : progressTextSize);
        canvas.drawText("%", centerX + textLen / 2 + extra + 5, centerY - 30 + h1 / 2, textPaint);

        //下一行文字
        textPaint.setTextSize(progressTextHintSize == 0 ? circleRadius / 5 : progressTextHintSize);
        textLen = textPaint.measureText(progressTextHint);
        textPaint.getTextBounds(progressTextHint, 0, progressTextHint.length(), textBounds);
        float h2 = textBounds.height();
        canvas.drawText(progressTextHint, centerX - textLen / 2, centerY + h1 / 2 + h2, textPaint);

        //绘制进度位置，也可以直接替换成一张图片
        if (draggingEnabled) {
            float progressRadians = (float) (((360.0f - ARC_FULL_DEGREE) / 2 + sweep1) / 180 * Math.PI);
            float thumbX = centerX - circleRadius * (float) Math.sin(progressRadians);
            float thumbY = centerY + circleRadius * (float) Math.cos(progressRadians);
            thumbPaint.setColor(getThumbColor(33));
            canvas.drawCircle(thumbX, thumbY, BUTTON_WIDTH == 0 ? STROKE_WIDTH * 2.0f : BUTTON_WIDTH * 1.0f, thumbPaint);
            thumbPaint.setColor(getThumbColor(99));
            canvas.drawCircle(thumbX, thumbY, BUTTON_WIDTH == 0 ? STROKE_WIDTH * 1.4f : BUTTON_WIDTH * 0.7f, thumbPaint);
            thumbPaint.setColor(Color.WHITE);
            canvas.drawCircle(thumbX, thumbY, BUTTON_WIDTH == 0 ? STROKE_WIDTH * 0.8f : BUTTON_WIDTH * 0.4f, thumbPaint);
        }
    }


    /**
     * 事件
     */
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!draggingEnabled) {
            return super.onTouchEvent(event);
        }

        //处理拖动事件
        float currentX = event.getX();
        float currentY = event.getY();

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //判断是否在进度条thumb位置
                if (checkOnArc(currentX, currentY)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    float newProgress = calDegreeByPosition(currentX, currentY) / ARC_FULL_DEGREE * max;
                    setProgressSync(newProgress);
                    isDragging = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    //判断拖动时是否移出去了
                    if (checkOnArc(currentX, currentY)) {
                        float currentDegree = calDegreeByPosition(currentX, currentY);

                        if (Math.abs(currentDegree - lastDegree) < 180) {
                            setProgressSync(currentDegree / ARC_FULL_DEGREE * max);
                            lastDegree = currentDegree;
                        } else {
                            setProgressSync(Math.round(lastDegree / ARC_FULL_DEGREE) >= 1 ? 359 * max : 0);
                        }
                    } else {//取消状态
//                        isDragging = false;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                isDragging = false;
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }

        return true;
    }

    /**
     * 计算
     */
    //按钮颜色
    private int getThumbColor(int alpha) {
        if (progressColorsStr.length > 0) {
            String color = progressColorsStr[0];
            if (color.length() > 7) {
                color = String.format("#%s", color.substring(3));
            }
            color = color.replace("#", "#" + alpha);
            return Color.parseColor(color);
        } else {
            return 0;
        }
    }

    //string转int[]
    private float[] stringToFloatArray(String str) {
        float[] result = new float[0];
        if (!TextUtils.isEmpty(str)) {
            String[] list = str.split(",");
            result = new float[list.length];
            for (int i = 0; i < list.length; i++) {
                result[i] = Float.valueOf(list[i]);
            }
        }
        return result;
    }

    //string转int[]
    private int[] stringToColors(String str) {
        String[] list = str.split(",");
        int[] result = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            result[i] = Color.parseColor(list[i]);
        }
        return result;
    }

    //string转float[]
    private float[] stringToGradient(String str) {
        String[] list = str.split(",");
        float[] result = new float[list.length];
        float degree = ARC_FULL_DEGREE  /(list.length -1);
        for (int i = 0; i < list.length; i++) {
            result[i] = degree * i / 360f;
        }
        return result;
    }

    //距离圆心
    private float calDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    //判断该点是否在弧线上（附近）
    private boolean checkOnArc(float currentX, float currentY) {
        float distance = calDistance(currentX, currentY, centerX, centerY);
        float degree = calDegreeByPosition(currentX, currentY);
        return distance > circleRadius - STROKE_WIDTH * 5 && distance < circleRadius + STROKE_WIDTH * 5
                && (degree >= -8 && degree <= ARC_FULL_DEGREE + 8);
    }

    //根据当前位置，计算出进度条已经转过的角度。
    private float calDegreeByPosition(float currentX, float currentY) {
        float a1 = (float) (Math.atan(1.0f * (centerX - currentX) / (currentY - centerY)) / Math.PI * 180);
        if (currentY < centerY) {
            a1 += 180;
        } else if (currentY > centerY && currentX > centerX) {
            a1 += 360;
        }

        return a1 - (360 - ARC_FULL_DEGREE) / 2;
    }

    //保证progress的值位于[0,max]
    private float checkProgress(float progress) {
        if (progress < 0) {
            return 0;
        }

        return progress > max ? max : progress;
    }

    //动画效果
    private void startAnimation(float per) {
        float diff = per - progress;
        ValueAnimator valueAnimator = ValueAnimator
                .ofFloat(progress, progress + diff)
                .setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    /**
     * 外部方法
     */

    //最大值
    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    //设置进度 过渡
    public void setProgress(float progress) {
        final float validProgress = checkProgress(progress);

        startAnimation(validProgress);
    }

    //设置进度
    public void setProgressSync(float progress) {
        this.progress = checkProgress(progress);
        invalidate();
    }

    //设置分段进度
    public float setProgressStep(float step) {
        float progress = 0;
        for (int i = 0; i < steps.length; i++) {
            if (step <= steps[i]) {
                if (i == 0) {
                    progress = stepProgress * i + stepProgress * (step / steps[i]);
                } else {
                    progress = stepProgress * i + stepProgress * ((step - steps[i - 1]) / (steps[i] - steps[i - 1]));
                }
                break;
            } else {
                progress = max;
            }
        }
        setProgress(progress);
        return progress;
    }

    //开启拖拽
    public void setDraggingEnabled(boolean draggingEnabled) {
        this.draggingEnabled = draggingEnabled;
    }

    //设置分段值 [10,50,100]
    public void setSteps(float... steps) {
        this.steps = steps;
        this.stepProgress = max / steps.length;
    }

    public float getMax() {
        return max;
    }

    public float getProgress() {
        return progress;
    }
}