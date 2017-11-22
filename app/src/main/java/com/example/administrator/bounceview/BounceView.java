package com.example.administrator.bounceview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by Administrator on 2017/11/22.
 */

public class BounceView extends View {


    private float xr = 0;
    float r_b = 0 ;
    private boolean isfirstCicle = true;
    private boolean isfirstBounce = true;
    private boolean isfirstRect = true;

    public BounceView(Context context) {
        this(context,null);
    }
    public BounceView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }
    public BounceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    Path path1;
    Paint paint;

    private float r = 50;

    private void init() {
        path1 = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    int countTimes  = 0;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(getMeasuredWidth()/2,getMeasuredHeight()/2);

        if (r_b>=10&&isfirstRect) {
            rectAnimation();
            isfirstRect = false;
        }

        RectF f = new RectF(-10-rect_position_x,-10-rect_position_y,10-rect_position_x,10-rect_position_y);

        switch (countTimes)
        {
            case 0:
                drawCicle(canvas);
                break;
            case 1:
                bounceLine(canvas);
                break;
        }

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(f,paint);

    }

    private void drawCicle(Canvas canvas) {

        int lay = canvas.saveLayer(-r,-r,r,r,paint,Canvas.ALL_SAVE_FLAG);

        paint.setColor(Color.parseColor("#944B1E2E"));
        canvas.drawCircle(0,0,r,paint);
        paint.setXfermode(xfermode);

        paint.setColor(Color.parseColor("#FF4081"));
        canvas.drawCircle(0,0,xr,paint);
        paint.setXfermode(null);
        canvas.restoreToCount(lay);

        if (isfirstCicle)
        {
            cicleAnimation();
            isfirstCicle = false;
        }

    }

    private void cicleAnimation() {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,50);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                xr = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                countTimes = 1;
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.start();

    }

    private void bounceLine(Canvas canvas) {
        path1.moveTo(0,r-r_b);
        path1.cubicTo(67,r-r_b,67,-r+r_bounce,0+r_bounce*4,-r+r_bounce);

        path1.moveTo(0-r_bounce*4,-r+r_bounce);
        path1.cubicTo(-67,-r+r_bounce,-67,r-r_b,0,r-r_b);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        canvas.drawPath(path1,paint);
        path1.reset();
        if (isfirstBounce)
        {
            bounceAnimation();
            isfirstBounce = false;
        }
    }

    float r_bounce = 0 ;
    private void bounceAnimation() {
        ValueAnimator a = ValueAnimator.ofFloat(0,r);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                r_bounce = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        a.setDuration(500);
        a.setInterpolator(new AccelerateDecelerateInterpolator());

        ValueAnimator v = ValueAnimator.ofFloat(0,70,30,60,40,r);
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                r_b = (float) animation.getAnimatedValue();

                invalidate();
            }


        });
        v.setDuration(1000);
        v.setInterpolator(new AccelerateDecelerateInterpolator());
        a.start();
        v.start();

    }
    float rect_position_y = 0;
    float rect_position_x = 0;
    private void rectAnimation() {
        ValueAnimator a = ValueAnimator.ofFloat(0,120,30);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rect_position_y = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        a.setDuration(800);
        a.setInterpolator(new AccelerateDecelerateInterpolator());

        ValueAnimator v = ValueAnimator.ofFloat(0,4*r);
        v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rect_position_x = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        v.setDuration(800);
        v.setInterpolator(new AccelerateDecelerateInterpolator());
        a.start();
        v.start();
    }

}
