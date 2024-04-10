package com.ycs.servicetest.common;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ycs.servicetest.R;

public class CustomImageDialog {
    private final Context context;
    private Dialog dialog;
    private final Display display;
    private ImageView image;
    private int mwidth;
    private int mheight;

    public CustomImageDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();

    }

    public CustomImageDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.layout_imagedialog, null);
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        image = view.findViewById(R.id.image);
        mwidth = image.getLayoutParams().width;
        mheight = image.getLayoutParams().height;
//        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
//        image.setOnClickListener(v -> dialog.dismiss());
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.86);
        return this;
    }


    public void setAni(int id) {
        image.setImageResource(id);
        final AnimatorSet animatorSet = new AnimatorSet();
        final ViewWrapper wrapper = new ViewWrapper(image);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(image, "translationY", -1500, 20, -10, 0);
        ObjectAnimator animator1 = ObjectAnimator.ofInt(wrapper, "width", 10, mwidth);
        ObjectAnimator animator2 = ObjectAnimator.ofInt(wrapper, "height", 10, mheight);
        animatorSet.play(animator4);
        animatorSet.setDuration(600).start();
    }

    public static class ViewWrapper {
        private View mTarget;

        public ViewWrapper(View target) {
            mTarget = target;
        }

        public int getWidth() {
            return mTarget.getLayoutParams().width;
        }

        public void setWidth(int width) {
            mTarget.getLayoutParams().width = width;
            mTarget.requestLayout();
        }

        public int getHeight() {
            return mTarget.getLayoutParams().width;
        }

        public void setHeight(int height) {
            mTarget.getLayoutParams().height = height;
            mTarget.requestLayout();
        }
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void show() {
        dialog.show();
        final AnimatorSet animatorSet = new AnimatorSet();
        final ViewWrapper wrapper = new ViewWrapper(image);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(image, "translationY", -1500, 20, -10, 0);

        animatorSet.play(animator4);
        animatorSet.setDuration(600).start();
    }
}
