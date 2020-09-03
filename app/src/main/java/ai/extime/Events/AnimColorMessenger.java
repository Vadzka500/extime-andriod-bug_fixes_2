package ai.extime.Events;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import com.extime.R;

public class AnimColorMessenger {

    public AnimColorMessenger(){

    }

    public static void ActionDown(int colorFrom, int colorTo, Drawable image, CircleImageView circle){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(45); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                Drawable color = new ColorDrawable((int) animator.getAnimatedValue());
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                circle.setImageDrawable(ld);
            }
        });
        colorAnimation.start();
    }

    public static void ActionUp(int colorFrom, int colorTo, Drawable image,CircleImageView circle){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(700);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                Drawable color = new ColorDrawable((int) animator.getAnimatedValue());
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                circle.setImageDrawable(ld);
            }
        });
        colorAnimation.start();
    }

    public static void ActionCancel(int colorFrom, int colorTo, Drawable image,CircleImageView circle){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(700); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                Drawable color = new ColorDrawable((int) animator.getAnimatedValue());
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                circle.setImageDrawable(ld);
            }
        });
        colorAnimation.start();
    }

    public static boolean ActionMove(int colorFrom, int colorTo, Drawable image, CircleImageView circle, MotionEvent motionEvent, View view){
        int[] location = new int[2];
        circle.getLocationInWindow(location);
        int leftX = 0;
        int rightX = leftX + circle.getWidth();
        int topY = 0;
        int bottomY = topY + circle.getHeight();
        float xCurrent = motionEvent.getX();
        float yCurrent = motionEvent.getY();

        if (xCurrent > rightX || xCurrent < leftX || yCurrent > bottomY || yCurrent < topY) {
            ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation2.setDuration(700); // milliseconds
            colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    Drawable color = new ColorDrawable((int) animator.getAnimatedValue());
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    circle.setImageDrawable(ld);
                }
            });
            colorAnimation2.start();
            return true;
        }
        return false;
    }


    //==============================


    public static void ActionDown_(int colorFrom, int colorTo, Drawable image, ImageView circle){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(45); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                Drawable color = new ColorDrawable((int) animator.getAnimatedValue());
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                circle.setImageDrawable(ld);
            }
        });
        colorAnimation.start();
    }

    public static void ActionUp_(int colorFrom, int colorTo, Drawable image,ImageView circle){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(700);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                Drawable color = new ColorDrawable((int) animator.getAnimatedValue());
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                circle.setImageDrawable(ld);
            }
        });
        colorAnimation.start();
    }

    public static void ActionCancel_(int colorFrom, int colorTo, Drawable image,ImageView circle){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(700); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                Drawable color = new ColorDrawable((int) animator.getAnimatedValue());
                LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                circle.setImageDrawable(ld);
            }
        });
        colorAnimation.start();
    }

    public static boolean ActionMove_(int colorFrom, int colorTo, Drawable image, ImageView circle, MotionEvent motionEvent, View view){
        int[] location = new int[2];
        circle.getLocationInWindow(location);
        int leftX = 0;
        int rightX = leftX + circle.getWidth();
        int topY = 0;
        int bottomY = topY + circle.getHeight();
        float xCurrent = motionEvent.getX();
        float yCurrent = motionEvent.getY();

        if (xCurrent > rightX || xCurrent < leftX || yCurrent > bottomY || yCurrent < topY) {
            ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation2.setDuration(500); // milliseconds
            colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    Drawable color = new ColorDrawable((int) animator.getAnimatedValue());
                    LayerDrawable ld = new LayerDrawable(new Drawable[]{color, image});
                    circle.setImageDrawable(ld);
                }
            });
            colorAnimation2.start();
            return true;
        }
        return false;
    }

}
