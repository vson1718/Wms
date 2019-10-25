package com.drsports.wms;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;


/**
 * @author vson
 */
public class SuspensionWindowUtil {

    private SuspensionView suspensionView;
    private WindowManager.LayoutParams layoutParams;
    private ValueAnimator valueAnimator;
    private int direction;
    private final int LEFT = 0;
    private final int RIGHT = 1;
    private Context context;
    private WindowManager mWindowManager;

    public SuspensionWindowUtil(Context context) {
        this.context = context;
        this.mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

    }

    /**
     * 显示SuspensionView
     */
    public void showSuspensionView() {
        hiddenSuspensionView();
        suspensionView = new SuspensionView(context);
        if (layoutParams == null) {
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = suspensionView.width;
            layoutParams.height = suspensionView.height;
            //gravity
            //初始化x，y坐标
            layoutParams.x += ScreenSizeUtil.getScreenWidth();
            layoutParams.y += ScreenSizeUtil.getScreenHeight() / 2;
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;

            //flags
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

            layoutParams.format = PixelFormat.RGBA_8888;
            //设置window type
            //悬浮窗口
            //系统级别 type
            //应用级别
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            }
        }
        //WindowManager:通过WindowManager添加View
        suspensionView.setOnTouchListener(touchListener);
        mWindowManager.addView(suspensionView, layoutParams);
    }


    /**
     * 隐藏SuspensionView
     */
    public void hiddenSuspensionView() {
        if (suspensionView != null) {
            mWindowManager.removeView(suspensionView);
            stopAnim();
            suspensionView = null;
        }
    }

    /**
     * touch事件
     */
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        float startX;
        float startY;
        float moveX;
        float moveY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    startY = event.getRawY();

                    moveX = event.getRawX();
                    moveY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getRawX() - moveX;
                    float y = event.getRawY() - moveY;
                    //计算偏移量，刷新视图
                    layoutParams.x += x;
                    layoutParams.y += y;
                    mWindowManager.updateViewLayout(suspensionView, layoutParams);
                    moveX = event.getRawX();
                    moveY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    //判断松手时View的横坐标是靠近屏幕哪一侧，将View移动到依靠屏幕
                    float endX = event.getRawX();
                    float endY = event.getRawY();
                    if (endX < ScreenSizeUtil.getScreenWidth() / 2) {
                        direction = LEFT;
                        endX = 0;
                    } else {
                        direction = RIGHT;
                        endX = ScreenSizeUtil.getScreenWidth() - suspensionView.width;
                    }
                    if (moveX != startX) {
                        starAnim((int) moveX, (int) endX, direction);
                    }
                    //如果初始落点与松手落点的坐标差值超过5个像素，则拦截该点击事件
                    //否则继续传递，将事件交给OnClickListener函数处理
                    if (Math.abs(startX - moveX) > 5) {
                        return true;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    /**
     * 开始动画
     *
     * @param startX    开始
     * @param endX      结束
     * @param direction 方向
     */
    private void starAnim(int startX, int endX, final int direction) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
        valueAnimator = ValueAnimator.ofInt(startX, endX);
        valueAnimator.setDuration(500);
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (direction == LEFT) {
                    layoutParams.x = (int) animation.getAnimatedValue() - suspensionView.width / 2;
                } else {
                    layoutParams.x = (int) animation.getAnimatedValue();
                }
                if (suspensionView != null) {
                    mWindowManager.updateViewLayout(suspensionView, layoutParams);
                }
            }
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
    }

    /**
     * 停止动画
     */
    private void stopAnim() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
    }


}
