package com.huan.WaveView.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * -----------------------------------------------------------------------------------Author Info---
 * Company Name:          xjyy.
 * Author:                Liu Huan.
 * Email:                 771383629@qq.com.
 * Date:                  2016/11/13 19:39.
 * -----------------------------------------------------------------------------------Message-------
 * If the following code to run properly, it is coding by Liu Huan.
 * otherwise I don't know.
 * -----------------------------------------------------------------------------------Class Info----
 * ClassName:             WaveCircleView.
 * -----------------------------------------------------------------------------------Describe------
 * Function: 代码开源转载请注明出处谢谢
 * -----------------------------------------------------------------------------------Modify--------
 * 2016/11/15 23:06     Modified By liuhuan.
 * -----------------------------------------------------------------------------------End-----------
 */
public class WaveCircleView extends View {

    /**
     * 默认宽高
     */
    private static final int DEFAULT_SIZE = 600;
    private static final int OFFSET_Y = 0;
    private static final int WAVE_SMOOTH_UP = 11;
    private static final int WAVE_SMOOTH_DOWN = 12;
    private static final int WAVE_SMOOTH_DOWN_UP = 13;
    private static final int WAVE_SMOOTH_DOWN_UP_2 = 14;
    private static final int ELLIPSE_RECTF_REFRESH_1 = 15;
    private static final int ELLIPSE_RECTF_REFRESH_2 = 16;
    private static final int ELLIPSE_RECTF_REFRESH_3 = 17;
    private static final int TOTAL = 100;//总份数
    //----------------------------------------------------------------------------------------------
    private float mERWidthOffsetMax = 50.0f;//椭圆左偏移量(加圆半径等于椭圆长半径)
    private float mERHeightOffsetMax = 30.0f;//椭圆上偏移量//等于短半径
    private float mStretchFactorA = 20; // y = Asin(wx+b)+h
    private float mCurrentProgress = 10f;//当前值
    private int mCViewHeight;//布局的高
    private int mCViewWidth;//布局的宽
    private Paint mCirclePaint;//圆的画笔
    private int mCircleColor = Color.parseColor("#00BFFF");//圆环颜色
    private int mCircleRadius = 120;//圆的半径
    private int mCircleStrokeWidth = 8;//圆的笔宽度
    private Paint mCircleRingPaint;//圆环的画笔
    private int mCircleRingColor = Color.parseColor("#F0F8FF");//外环颜色
    private int mCircleRingStrokeWidth = 20;//圆环的笔宽度
    private int mCircleRingRadius = mCircleRadius + mCircleStrokeWidth / 2 + mCircleRingStrokeWidth / 2;//圆环的半径
    private Paint mArcPaint;//扇形画笔
    private int mArcColor = Color.parseColor("#FFFFFF");
    private Paint mWavePaint;//水波画笔
    private int mWaveColor = Color.GREEN;// 波纹颜色
    private int mXSpeed1 = 8;// 第一条水波移动速度
    private int mXSpeed2 = 5;// 第二条水波移动速度
    private float mCycleFactorW;
    private int mTotalWidth, mTotalHeight;
    private float[] mYPositions;
    private float[] mResetOneYPositions;
    private float[] mResetTwoYPositions;
    private int mXOffsetSpeedOne;
    private int mXOffsetSpeedTwo;
    private int mXOneOffset;
    private int mXTwoOffset;
    private RectF mProgressOval; //扇形
    private Bitmap image = null;//用户传的图片
    private Paint imagePaint;//图片画笔
    private DrawFilter mDrawFilter;
    private Bitmap bitmap = null;
    private int mCircleRingAlpha = 50;
    private Canvas cvs;
    private Canvas cvsmask;
    private Bitmap mask;
    private Bitmap bm;
    private Paint mEllipsePaint;//椭圆画笔
    private RectF mEllipseRectF;
    private RectF mEllipseRectF2;
    private RectF mEllipseRectF3;
    private boolean isDrawFilter = false;
    private boolean isShowEllipse = false;
    private float mPercent;
    private Paint mWaveXfermodePaint;
    private float mErOffset;
    private int mErLeft;
    private int mErTop;
    private int mErRight;
    private int mRisingSpeedValue = 2;//水波默认上升速度
    private int mDescentSpeedValue = 3;//水波默认下降速度
    private float mBaseEllipseValue = 20;//椭圆效果基础偏移量
    private float mEllipseValue2 = 10;//椭圆效果偏移量2
    private float mEllipseValue3 = 5;//椭圆效果偏移量3
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WAVE_SMOOTH_UP:
                    if (msg.arg1 > mCurrentProgress) {
                        mCurrentProgress = mCurrentProgress + mRisingSpeedValue > 100 ? 100 : mCurrentProgress + mRisingSpeedValue;
                        Message ms = Message.obtain();
                        ms.what = WAVE_SMOOTH_UP;
                        ms.arg1 = msg.arg1;
                        mHandler.sendMessage(ms);
                    }
                    break;
                case WAVE_SMOOTH_DOWN:
                    if (msg.arg1 < mCurrentProgress) {
                        mCurrentProgress = mCurrentProgress - mDescentSpeedValue < 0 ? 0 : mCurrentProgress - mDescentSpeedValue;
                        Message ms = Message.obtain();
                        ms.what = WAVE_SMOOTH_DOWN;
                        ms.arg1 = msg.arg1;
                        mHandler.sendMessage(ms);
                    }
                    break;
                case WAVE_SMOOTH_DOWN_UP:
                    if (mCurrentProgress > 0) {
                        mCurrentProgress = mCurrentProgress - mDescentSpeedValue < 0 ? 0 : mCurrentProgress - mDescentSpeedValue;
                        Message ms = Message.obtain();
                        ms.what = WAVE_SMOOTH_DOWN_UP;
                        ms.arg1 = msg.arg1;
                        mHandler.sendMessage(ms);
                    } else {
                        Message ms = Message.obtain();
                        ms.what = WAVE_SMOOTH_DOWN_UP_2;
                        ms.arg1 = msg.arg1;
                        mHandler.sendMessage(ms);
                    }
                    break;
                case WAVE_SMOOTH_DOWN_UP_2:
                    if (msg.arg1 > mCurrentProgress) {
                        mCurrentProgress = mCurrentProgress + mRisingSpeedValue > 100 ? 100 : mCurrentProgress + mRisingSpeedValue;
                        Message ms = Message.obtain();
                        ms.what = WAVE_SMOOTH_UP;
                        ms.arg1 = msg.arg1;
                        mHandler.sendMessage(ms);
                    }
                    break;
                case ELLIPSE_RECTF_REFRESH_1:

                    if (msg.arg1 + mERHeightOffsetMax >= 0) {
                        Message ms = Message.obtain();
                        ms.what = ELLIPSE_RECTF_REFRESH_1;
                        mEllipseRectF = new RectF(
                                mErLeft - mERWidthOffsetMax - mErOffset * msg.arg1,//左-
                                mErTop - mERHeightOffsetMax - msg.arg1,//上-
                                mErRight + mERWidthOffsetMax + mErOffset * msg.arg1,//右+
                                mErTop + mERHeightOffsetMax + msg.arg1);//下+
                        ms.arg1 = (msg.arg1 - 2);
                        mHandler.sendMessageDelayed(ms, 10);
                    } else {
                        Message ms = Message.obtain();
                        ms.what = ELLIPSE_RECTF_REFRESH_1;
                        ms.arg1 = (int) mBaseEllipseValue;
                        mHandler.sendMessageDelayed(ms, 10);
                    }
                    break;
                case ELLIPSE_RECTF_REFRESH_2:
                    if (msg.arg1 + mERHeightOffsetMax >= 0) {
                        Message ms = Message.obtain();
                        ms.what = ELLIPSE_RECTF_REFRESH_2;
                        mEllipseRectF2 = new RectF(
                                mErLeft - mERWidthOffsetMax - mErOffset * msg.arg1,//左-
                                mErTop - mERHeightOffsetMax - msg.arg1,//上-
                                mErRight + mERWidthOffsetMax + mErOffset * msg.arg1,//右+
                                mErTop + mERHeightOffsetMax + msg.arg1);//下+
                        ms.arg1 = (msg.arg1 - 2);
                        mHandler.sendMessageDelayed(ms, 10);
                    } else {
                        Message ms = Message.obtain();
                        ms.what = ELLIPSE_RECTF_REFRESH_2;
                        ms.arg1 = (int) mEllipseValue2;
                        mHandler.sendMessageDelayed(ms, 10);
                    }
                    break;
                case ELLIPSE_RECTF_REFRESH_3:
                    if (msg.arg1 + mERHeightOffsetMax >= 0) {
                        Message ms = Message.obtain();
                        ms.what = ELLIPSE_RECTF_REFRESH_3;
                        mEllipseRectF3 = new RectF(
                                mErLeft - mERWidthOffsetMax - mErOffset * msg.arg1,//左-
                                mErTop - mERHeightOffsetMax - msg.arg1,//上-
                                mErRight + mERWidthOffsetMax + mErOffset * msg.arg1,//右+
                                mErTop + mERHeightOffsetMax + msg.arg1);//下+
                        ms.arg1 = (msg.arg1 - 2);
                        mHandler.sendMessageDelayed(ms, 10);
                    } else {
                        Message ms = Message.obtain();
                        ms.what = ELLIPSE_RECTF_REFRESH_3;
                        ms.arg1 = (int) mEllipseValue3;
                        mHandler.sendMessageDelayed(ms, 10);
                    }
                    break;
            }
        }
    };
    private Context mContext;
    //
    public WaveCircleView(Context context) {
        this(context, null);
    }
    public WaveCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public WaveCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        //初始化一些东西
        //圆画笔
        mCirclePaint = new Paint();
        mCirclePaint.setStyle(Paint.Style.STROKE);//模式
        mCirclePaint.setAntiAlias(true);//抗锯齿
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);//笔宽
        mCirclePaint.setColor(mCircleColor);

        //椭圆画笔
        mEllipsePaint = new Paint();
        mEllipsePaint.setAntiAlias(true);
        mEllipsePaint.setStrokeWidth(4);//笔宽
        mEllipsePaint.setColor(mCircleColor);
        mEllipsePaint.setStyle(Paint.Style.STROKE);//模式


        //圆环画笔
        mCircleRingPaint = new Paint();
        mCircleRingPaint.setStyle(Paint.Style.STROKE);//模式
        mCircleRingPaint.setAntiAlias(true);//抗锯齿
        mCircleRingPaint.setStrokeWidth(mCircleRingStrokeWidth);//笔宽
        mCircleRingPaint.setColor(mCircleRingColor);
        mCircleRingPaint.setAlpha(mCircleRingAlpha);

        //扇形画笔
        mArcPaint = new Paint();
        mArcPaint.setStyle(Paint.Style.FILL);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(mArcColor);
        mArcPaint.setStrokeWidth(1);//
        //用来画扇形和水纹交汇模式的画笔
        mWaveXfermodePaint = new Paint();
        //扇形与水纹层重合处理模式
        mWaveXfermodePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));

        //图片画笔
        imagePaint = new Paint();
        imagePaint.setAntiAlias(true);
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        // 波纹画笔
        mWavePaint = new Paint();
        mWavePaint.setStrokeWidth(1.0F);
        mWavePaint.setAntiAlias(true);// 去除画笔锯齿
        mWavePaint.setStyle(Paint.Style.STROKE); // 设置风格为实线
        mWavePaint.setColor(mWaveColor); // 设置画笔颜色

        // 将dp转化为px，用于控制不同分辨率上移动速度基本一致
        mXOffsetSpeedOne = dipToPx(context, mXSpeed1);
        mXOffsetSpeedTwo = dipToPx(context, mXSpeed2);
    }

    private int dipToPx(Context context, int dip) {
        return (int) (dip * getScreenDensity(context) + 0.5f);
    }

    private float getScreenDensity(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                    .getMetrics(dm);
            return dm.density;
        } catch (Exception e) {
            return DisplayMetrics.DENSITY_DEFAULT;
        }
    }

    public void setERHeightOffsetMax(float heightOffset) {
        this.mERHeightOffsetMax = heightOffset;
        reSetOffset();


    }

    private void reSetOffset() {
        //高度等于高度的偏移量mERHeightOffsetMax
        mErOffset = (mERWidthOffsetMax + mCircleRadius) / mERHeightOffsetMax;//椭圆的长短半径的比例
        mErLeft = mCViewWidth / 2 - mCircleRadius;//这减掉一个圆的半径是为了让椭圆的左侧超出圆的半径。或者可以直接减一个值(该值大于圆的半径)
        mErTop = mCViewHeight / 2 + mCircleRadius;
        mErRight = mCViewWidth / 2 + mCircleRadius;
        //必须满足value+mERHeightOffsetMax>=0
        mEllipseRectF = new RectF(
                mErLeft - mERWidthOffsetMax - mErOffset * mBaseEllipseValue,//左-
                mErTop - mERHeightOffsetMax - mBaseEllipseValue,//上-
                mErRight + mERWidthOffsetMax + mErOffset * mBaseEllipseValue,//右+
                mErTop + mERHeightOffsetMax + mBaseEllipseValue);//下+


        mEllipseRectF2 = new RectF(
                mErLeft - mERWidthOffsetMax - mErOffset * mEllipseValue2,//左-
                mErTop - mERHeightOffsetMax - mEllipseValue2,//上-
                mErRight + mERWidthOffsetMax + mErOffset * mEllipseValue2,//右+
                mErTop + mERHeightOffsetMax + mEllipseValue2);//下+

        mEllipseRectF3 = new RectF(
                mErLeft - mERWidthOffsetMax - mErOffset * mEllipseValue3,//左-
                mErTop - mERHeightOffsetMax - mEllipseValue3,//上-
                mErRight + mERWidthOffsetMax + mErOffset * mEllipseValue3,//右+
                mErTop + mERHeightOffsetMax + mEllipseValue3);//下+
    }

    public void ERWidthOffsetMax(float widthOffset) {
        this.mERWidthOffsetMax = widthOffset;
        reSetOffset();
    }

    public void setStretchFactorA(float a) {
        mStretchFactorA = a;
    }

    public void setCircleColor(int mCircleColor) {
        this.mCircleColor = mCircleColor;

        mCirclePaint.setColor(mCircleColor);
    }

    public void setCircleRingColor(int mCircleRingColor) {
        this.mCircleRingColor = mCircleRingColor;

        mCircleRingPaint.setColor(mCircleRingColor);
    }

    public void setCircleRingStrokeWidth(int mCircleRingStrokeWidth) {
        this.mCircleRingStrokeWidth = mCircleRingStrokeWidth;

        mCircleRingPaint.setStrokeWidth(mCircleRingStrokeWidth);//笔宽
    }

    public void setCircleRingRadius(int mCircleRingRadius) {
        this.mCircleRingRadius = mCircleRingRadius;


    }

    public void setWaveColor(int mWaveColor) {
        this.mWaveColor = mWaveColor;
        mWavePaint.setColor(mWaveColor); // 设置画笔颜色
    }

    public void setXSpeed1(int mXSpeed1) {
        this.mXSpeed1 = mXSpeed1;
        mXOffsetSpeedOne = dipToPx(mContext, mXSpeed1);

    }

    public void setXSpeed2(int mXSpeed2) {
        this.mXSpeed2 = mXSpeed2;
        mXOffsetSpeedTwo = dipToPx(mContext, mXSpeed2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mCViewWidth = measureDimension(DEFAULT_SIZE, widthMeasureSpec);
        mCViewHeight = measureDimension(DEFAULT_SIZE, heightMeasureSpec);
        setMeasuredDimension(mCViewWidth, mCViewHeight);
    }

    //测量
    public int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;   //UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * 设置是否开启DrawFilter(用于抗锯齿)
     *
     * @param isDrawFilter
     */
    public void setDrawFilter(boolean isDrawFilter) {
        this.isDrawFilter = isDrawFilter;
    }

    /**
     * 设置是否显示下方椭圆环
     *
     * @param isShowEllipse
     */
    public void setShowEllipse(boolean isShowEllipse) {
        this.isShowEllipse = isShowEllipse;
        if (isShowEllipse)
            startSendEllipseSizeChange();
    }

    private void startSendEllipseSizeChange() {
        Message ms1 = Message.obtain();
        ms1.what = ELLIPSE_RECTF_REFRESH_1;
        ms1.arg1 = (int) mBaseEllipseValue;
        mHandler.sendMessageDelayed(ms1, 50);

        Message ms2 = Message.obtain();
        ms2.what = ELLIPSE_RECTF_REFRESH_2;
        ms2.arg1 = (int) mEllipseValue2;
        mHandler.sendMessageDelayed(ms2, 50);

        Message ms3 = Message.obtain();
        ms3.what = ELLIPSE_RECTF_REFRESH_3;
        ms3.arg1 = (int) mEllipseValue3;
        mHandler.sendMessageDelayed(ms3, 50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDrawFilter)
            canvas.setDrawFilter(mDrawFilter);
        //------------------------------------------------------------------------------------------
        if (isShowEllipse) {
            canvas.drawOval(mEllipseRectF, mEllipsePaint);
            canvas.drawOval(mEllipseRectF2, mEllipsePaint);
            canvas.drawOval(mEllipseRectF3, mEllipsePaint);
        }
        //------------------------------------------------------------------------------------------
        mPercent = mCurrentProgress / TOTAL;
        float mPercentDouble = mPercent * 2;
        //用户设置的图片
        if (bitmap != null) {
            //他和扇形的左边距是一样的
            float imageLeft = (float) (mCViewWidth / 2.0 - mCircleRadius);
            //和扇形的上边距是一样的
            float imageTop = (float) (mCViewHeight / 2.0 - mCircleRadius);

            canvas.drawBitmap(bitmap, imageLeft, imageTop, mWavePaint);
        }
        float processValue = mCircleRadius * mPercentDouble;
        float rightAngleSide = mCircleRadius - processValue;
        double startAngle = Math.asin(rightAngleSide / mCircleRadius) * 180.0 / Math.PI;
        double endAngle = 2 * (90 - startAngle);

        cvs.drawArc(mProgressOval, (float) startAngle, (float) endAngle, false, mArcPaint);//画一个扇形
        resetPositonY();
        for (int i = 0; i < mTotalWidth; i++) {
            int value = i + (int) (mCViewWidth / 2.0 - mCircleRadius);
            //动态的改变percent2从而形成波纹上升下降效果

            // 绘制第一条水波纹
            cvsmask.drawLine(value,
                    mTotalHeight + (int) (mCViewWidth / 2.0 - mCircleRadius) - mResetOneYPositions[i] + 10 - mCircleRadius * mPercentDouble + 12,
                    value,
                    (float) (mTotalHeight + (int) (mCViewWidth / 2.0 - mCircleRadius)),
                    mWavePaint);
            // 绘制第二条水波纹
            cvsmask.drawLine(value, mTotalHeight + (int) (mCViewWidth / 2.0 - mCircleRadius) - mResetTwoYPositions[i] - mCircleRadius * mPercentDouble + 25, value,
                    mTotalHeight + (int) (mCViewWidth / 2.0 - mCircleRadius),
                    mWavePaint);
        }


        //将水纹图层画到扇形图层上
        cvs.drawBitmap(mask, 0, 0, mWaveXfermodePaint);

        // 改变两条波纹的移动点
        mXOneOffset += mXOffsetSpeedOne;
        mXTwoOffset += mXOffsetSpeedTwo;

        // 如果已经移动到结尾处，则重头记录
        if (mXOneOffset >= mTotalWidth) {
            mXOneOffset = 0;
        }
        if (mXTwoOffset > mTotalWidth) {
            mXTwoOffset = 0;
        }


        canvas.drawBitmap(bm, 0, 0, imagePaint);
        mWavePaint.setXfermode(null);
        //开始画图
        //------------------------------------------------------------------------------------------
        //内圆
        canvas.drawCircle(mCViewWidth / 2, mCViewHeight / 2,
                mCircleRadius, mCirclePaint);
        //------------------------------------------------------------------------------------------
        //外圆圈
        canvas.drawCircle(mCViewWidth / 2, mCViewHeight / 2,
                mCircleRingRadius, mCircleRingPaint);
        //------------------------------------------------------------------------------------------
        cvs.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        cvsmask.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        postInvalidate();
    }

    private void resetPositonY() {
        // mXOneOffset代表当前第一条水波纹要移动的距离
        int yOneInterval = mYPositions.length - mXOneOffset;
        // 使用System.arraycopy方式重新填充第一条波纹的数据
        System.arraycopy(mYPositions, mXOneOffset, mResetOneYPositions, 0, yOneInterval);
        System.arraycopy(mYPositions, 0, mResetOneYPositions, yOneInterval, mXOneOffset);

        int yTwoInterval = mYPositions.length - mXTwoOffset;
        System.arraycopy(mYPositions, mXTwoOffset, mResetTwoYPositions, 0,
                yTwoInterval);
        System.arraycopy(mYPositions, 0, mResetTwoYPositions, yTwoInterval, mXTwoOffset);
    }

    /**
     * 设置内圆是否填充 默认false
     *
     * @param isFull
     */
    public void setCirclePaintStyleIsFull(boolean isFull) {
        if (isFull)
            mCirclePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 关闭硬件加速，防止异常unsupported operation exception
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 记录下view的宽高
        mTotalWidth = 2 * mCircleRadius;
        mTotalHeight = 2 * mCircleRadius;
        // 用于保存原始波纹的y值
        mYPositions = new float[mTotalWidth];
        // 用于保存波纹一的y值
        mResetOneYPositions = new float[mTotalWidth];
        // 用于保存波纹二的y值
        mResetTwoYPositions = new float[mTotalWidth];

        // 将周期定为view总宽度
        mCycleFactorW = (float) (2 * Math.PI / mTotalWidth);

        // 根据view总宽度得出所有对应的y值
        for (int i = 0; i < mTotalWidth; i++) {
            mYPositions[i] = (float) (mStretchFactorA * Math.sin(mCycleFactorW * i) + OFFSET_Y);
        }
        //初始化一些值
        //扇形左边距
        float ovalLeft = (float) (mCViewWidth / 2.0 - mCircleRadius);
        //扇形上边距
        float ovalTop = (float) (mCViewHeight / 2.0 - mCircleRadius);
        //扇形右边距
        float ovalRight = mCViewWidth - ovalLeft;
        //扇形下边距
        float ovalBottom = mCViewHeight - ovalTop;

        //画一个扇形
        mProgressOval = new RectF(ovalLeft, ovalTop, ovalRight, ovalBottom);
        //底层
        bm = Bitmap.createBitmap(mCViewWidth, mCViewHeight, Bitmap.Config.ARGB_8888);

        cvs = new Canvas(bm);
        //上层
        mask = Bitmap.createBitmap(mCViewWidth, mCViewHeight, Bitmap.Config.ARGB_8888);

        cvsmask = new Canvas(mask);
        //宽度等于mERWidthOffsetMax+圆的半径
        //高度等于高度的偏移量mERHeightOffsetMax
        reSetOffset();

        //
        if (isShowEllipse) {
            startSendEllipseSizeChange();
        }
    }

    /**
     * 设置背景圆
     *
     * @param icon
     */
    public void setWaveBackgroundRes(int icon) {
        image = BitmapFactory.decodeResource(getResources(), icon);//用户传的图片
        if (image != null) {
            //得到源图的宽高
            float souceImageWidth = image.getWidth();
            float souceImageHeight = image.getHeight();
            //得到目标缩放比例

            float heightRatio = 2 * mCircleRadius / souceImageHeight;
            float widthRatio = 2 * mCircleRadius / souceImageWidth;
            //新的比例图
            bitmap = scaleBitmap(image, widthRatio, heightRatio);
            image.recycle();
        }
    }

    /**
     * 缩放图片
     */
    private Bitmap scaleBitmap(Bitmap origin, float widthRatio, float heightRatio) {
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(widthRatio, heightRatio);
        Bitmap newBitmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBitmap.equals(origin)) {
            return newBitmap;
        }
        origin.recycle();
        origin = null;
        return newBitmap;
    }

    public void setWaveProgress(float progress) {
        setWaveProgressSmooth(progress, false);
    }

    /**
     * 设置水波纹上升下降移动平滑
     *
     * @param progress
     * @param smooth   true 平滑  false 不平滑
     */
    public void setWaveProgressSmooth(float progress, boolean smooth) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("WaveCircleView Progress Parameters must be between 0 - 100 ");
        }
        mHandler.removeMessages(WAVE_SMOOTH_UP);
        mHandler.removeMessages(WAVE_SMOOTH_DOWN);
        Message message = Message.obtain();
        if (smooth) {//平滑
            if (progress > mCurrentProgress) {
                message.what = WAVE_SMOOTH_UP;
                message.arg1 = (int) progress;
                mHandler.sendMessage(message);
            } else if (progress < mCurrentProgress) {
                message.what = WAVE_SMOOTH_DOWN;
                message.arg1 = (int) progress;
                mHandler.sendMessage(message);
            }
        } else {//直接设置值
            mCurrentProgress = progress;
        }
    }

    /**
     * 设置上升速度
     *
     * @param value
     */
    public void setWaveRisingSpeedValue(int value) {
        mRisingSpeedValue = value;
    }

    /**
     * 设置下降速度
     *
     * @param value
     */
    public void setWaveDescentSpeedValue(int value) {
        mDescentSpeedValue = value;
    }

    /**
     * 设置平滑先下后上
     *
     * @param progress
     * @param isDownUp
     */
    public void setWaveProgressDownUpSmooth(float progress, boolean isDownUp) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("WaveCircleView Progress Parameters must be between 0 - 100 ");
        }
        mHandler.removeMessages(WAVE_SMOOTH_UP);
        mHandler.removeMessages(WAVE_SMOOTH_DOWN);
        mHandler.removeMessages(WAVE_SMOOTH_DOWN_UP);
        Message message = Message.obtain();
        if (isDownUp) {//平滑
            message.what = WAVE_SMOOTH_DOWN_UP;
            message.arg1 = (int) progress;
            mHandler.sendMessage(message);

        } else {//直接设置值
            setWaveProgressSmooth(progress, true);
        }
    }
}
