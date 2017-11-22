# BounceView自定义view综合运用
 41
好久没写博客了，关于自定义view之前也写过很多，今天就来对自定义view做个总结，在自定义view中需要掌握哪些知识呢！在讨论之前首先来看看今天的demo效果


效果还是挺炫酷的，写这个例子的原因也是我看到网上别人写的一个效果，感觉不错就拿来模仿了一下。其实每个view实现的方法有很多种，根据自己的自定义view的知识量来写，实现方式可能都不相同，所以大家当看到比较炫酷的view的时候可以尝试去试一试，如果有些效果实现不了，可以去看看别人的思想。“写”也是一个“学”的过程。多了不扯，先来总结一下自定义view的知识点（如有遗漏大家下面补充）：

1.首先自定义view的构造方法的写法，自定义属性，view的测量onMusure（），ondraw（）这些就不多说了，这是必须掌握的。

2.既然要画图自然少不了;Paint画笔这个类，paint的相关方法你是否熟练，不熟悉的赶快去看看。

3.canvas 画布，开玩笑有画笔哪能少了画布。

4.Path路径这个牛逼了，要想实现比较酷酷的画面还真是不能少了他，二阶贝塞尔曲线和三阶贝塞尔曲线，这个还需要好好研究研究。

5.有了Path 我们自然会想到他的兄弟那就是 PathMesure ，为什么说是他兄弟，有了路径我们是不是要知道路径的长度，每个点的坐标，角度，没错PathMesure就是对Path进行测量的，这个类我之前博客里都有讲过。

6.有了这些是不是就够了呢？那可能我们的自定义view是个“残废”，为什么？因为他动不了，怎么让他动起来呢？那就是需要动画的参与了两个重要的动画类;ValueAnimator和ObjectAnimator 我们自定义view基本这两个动画足够用了，当然动画最重要的最难的还是自定义插值器，有些特殊的需求view移动的时候，可以自己定义速度已经点坐标。

7.有了这些基本你就能做出很悬的效果了，但是还有一个很重要的类;PorterDuffXfermode 恩恩这个也很重要，弄清楚他的16/18个图形展示的效果。

基本这些......应该就差不多了....夜深了，想不出来了，少的大家给个补充...............还有一个很重要的是要大胆去尝试，哪怕弄了一半搞不动了。。。


扯了半天，下面就来看看我们的这个综合的demo，涉及到的知识点还是比较多得。

首先我们先来分析一下这个动画的过程，大致分为三个过程：

1.是大圆被小圆吞噬的过程（PorterDuffXfermode ），主要我们的矩形是不能被覆盖的。

2.是大圆被吞噬完之后我们处理圆环的绘制，这个圆环采用的是三阶贝塞尔曲线（path），圆环变成直线的过程，以及回弹效果。

3.矩形被弹飞的过程（为了方便这里我直接用的动画，其实更好的做法是先给矩形设置一个抛物线状的移动的path路径）。

大致分为这几个过程，下面就一一说明：

过程一：是大圆被小圆吞噬的过程。这里就比较简单绘制两个圆，利用PorterDuffXfermode显示，

首先定义一个对象


PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
然后绘制圆：

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

这个过程基本就完了。

过程2;绘制圆环--》到直线，这个比较复杂一点需要移动的坐标比较多：
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

这是实现圆环变化到直线的过程、、、、、

我把实现的逻辑放出来，对于动画控制请看看源码把，贴出来太长了，把主要逻辑拿出来

过程三：绘制矩形的移动，这个比较简单，用动画控制矩形移动的坐标就行了

 RectF f = new RectF(-10-rect_position_x,-10-rect_position_y,10-rect_position_x,10-rect_position_y);

矩形的坐标控制动画就是;

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


大致的绘制过程就是这样，有问题的大家可以留言哟........
