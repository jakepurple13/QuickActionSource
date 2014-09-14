package net.sourcerer.quickaction;

import net.sourcerer.android.R;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 *
 * @author Sourcerer
 *
 */
public class QuickActionWindow {

    protected LayoutInflater inflater;

    protected Context context;
    protected PopupWindow popupWindow;
    protected WindowManager windowManager;
    protected View content;

    public QuickActionWindow(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        popupWindow = new PopupWindow(context);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        // Dismiss window on any touch outside
        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                }
                return false;
            }
        });

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     *
     * @return Returns the size as android point.
     */
    protected Point getScreenSize() {
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);

        return size;
    }

    protected void renderWindow() {
        if (content == null) {
            throw new RuntimeException("Can't open a popup without content!");
        }

        content.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        popupWindow.setContentView(content);
    }

    public void setContentView(View content) {
        this.content = content;
    }

    public View setContentView(int layoutResID) {
        View content = inflater.inflate(layoutResID, null);
        setContentView(content);

        return content;
    }

    public View getContent() {
        return content;
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        popupWindow.setOnDismissListener(listener);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    /**
     * Determine the the correct popup animation.
     */
    protected void setPopupAnimation(boolean onTop, int arrowMargin, int contentWidth) {
        if ((float)arrowMargin / (float)contentWidth < 0.30f) {
            if (onTop) {
                popupWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Left);
            }
            else {
                popupWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Left);
            }
        }
        else if ((float)arrowMargin / (float)contentWidth < 0.70f) {
            if (onTop) {
                popupWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Center);
            }
            else {
                popupWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Center);
            }
        }
        else {
            if (onTop) {
                popupWindow.setAnimationStyle(R.style.Animations_PopUpMenu_Right);
            }
            else {
                popupWindow.setAnimationStyle(R.style.Animations_PopDownMenu_Right);
            }
        }
    }
}
