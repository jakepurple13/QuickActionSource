package net.sourcerer.quickaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourcerer.android.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

/**
 *
 * @author Sourcerer
 *
 */
public class QuickActionBar extends QuickActionWindow {

    // Orientation values
    public static final int TOP = 1;
    public static final int BOTTOM = 2;
    public static final int RIGHT = 3;
    public static final int LEFT = 4;

    private LayoutInflater inflater;
    private ViewGroup buttonView;
    private ImageView topArrow;
    private ImageView bottomArrow;
    private HorizontalScrollView scroller;

    // Default orientation is bottom
    private int orientation = BOTTOM;
    // null = no maxWidth
    private Integer maxWidth = null;
    // Buttons list
    private List<ActionItem> actions = new ArrayList<ActionItem>();

    public QuickActionBar(Context context) {
        this(context, R.layout.qa_dialog);
    }

    public QuickActionBar(Context context, int layoutId) {
        super(context);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View container = setContentView(layoutId);

        // Fetching Ids for view elements
        Resources res = container.getResources();
        int buttonViewId = res.getIdentifier("qa_buttons", "id", container.getContext().getPackageName());
        int topArrowId = res.getIdentifier("qa_arrow_up", "id", container.getContext().getPackageName());
        int bottomArrowId = res.getIdentifier("qa_arrow_down", "id", container.getContext().getPackageName());
        int scrollerId = res.getIdentifier("qa_scroller", "id", container.getContext().getPackageName());

        buttonView = (ViewGroup) container.findViewById(buttonViewId);
        topArrow = (ImageView) container.findViewById(topArrowId);
        bottomArrow = (ImageView) container.findViewById(bottomArrowId);
        scroller = (HorizontalScrollView) container.findViewById(scrollerId);
    }

    /**
     * Set a maximum width for the QuickAction dialog, null is no max width.
     */
    public void setMaxWidth(Integer width) {
        maxWidth = width;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    /**
     * Add action item
     *
     * @param action  {@link ActionItem}
     */
    public void addActionItem(final ActionItem... actions) {
        for (ActionItem item : actions) {
            addActionItem(item);
        }
    }

    /**
     * Add action item
     *
     * @param action  {@link ActionItem}
     */
    public void addActionItem(final ActionItem action) {
        actions.add(action);

        View button = inflater.inflate(R.layout.qa_action_item, null);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action.isSticky()) {
                    action.setSelected(!action.isSelected());
                }

                if (action.hasOnClickListner()) {
                    action.getQuickActionClickListner().onClick(action, v);
                }

                v.post(new Runnable() {
                    @Override
                    public void run() {
                        if (action.isSticky() == false) {
                            dismiss();
                        }
                    }
                });
            }
        });

        buttonView.addView(button, buttonView.getChildCount() - 1);

        action.init(button);
    }

    public List<ActionItem> getActionItems() {
        return Collections.unmodifiableList(actions);
    }

    /**
     * Shows the popup window on the given anchor.
     */
    public void show (View anchor) {
        renderWindow();

        // Move buttons to the left
        scroller.scrollTo(0, 0);

        for (ActionItem action : actions) {
            if (action.hasOnOpenListner()) {
                action.getQuickActionOnOpenListner().onOpen(action);
            }
        }

        int[] location = new int[2];
        anchor.getLocationOnScreen(location);

        Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());

        content.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        int contentWidth = content.getMeasuredWidth();
        int contentHeight  = content.getMeasuredHeight();

        Point screenSize = getScreenSize();

        // Reduce popup to screen size
        if (contentWidth > screenSize.x) {
            contentWidth = screenSize.x;
        }

        // reduce size so its still show the anchor
        if (orientation == LEFT || orientation == RIGHT) {
            if (contentWidth > screenSize.x - anchor.getWidth()) {
                contentWidth = screenSize.x - anchor.getWidth();
            }
        }

        if (maxWidth != null && maxWidth < contentWidth) {
            contentWidth = maxWidth;
        }

        // Force maximum size
        popupWindow.setWidth(contentWidth);

        int posX = 0;
        int posY = 0;
        int arrowMargin = 0;

        // Hide arrows
        topArrow.setVisibility(View.INVISIBLE);
        bottomArrow.setVisibility(View.INVISIBLE);

        // Set position
        switch(orientation) {
            case TOP:
                // Popup is centered on the anchor by moving left
                posX = calculatePositionX(anchorRect.centerX(), contentWidth, screenSize.x);

                arrowMargin = anchorRect.centerX() - posX;
                if (contentHeight < anchorRect.top) {
                    posY = anchorRect.top - contentHeight;
                    showArrow(bottomArrow, arrowMargin);
                    setPopupAnimation(true, arrowMargin, contentWidth);
                }
                else {
                    posY = anchorRect.bottom;
                    showArrow(topArrow, arrowMargin);
                    setPopupAnimation(false, arrowMargin, contentWidth);
                }

                break;
            case BOTTOM:
                // Popup is centered on the anchor by moving left
                posX = calculatePositionX(anchorRect.centerX(), contentWidth, screenSize.x);

                arrowMargin = anchorRect.centerX() - posX;
                if (contentHeight > screenSize.y - anchorRect.bottom) {
                    posY = anchorRect.top - contentHeight;
                    showArrow(bottomArrow, arrowMargin);
                    setPopupAnimation(true, arrowMargin, contentWidth);
                }
                else {
                    posY = anchorRect.bottom;
                    showArrow(topArrow, arrowMargin);
                    setPopupAnimation(false, arrowMargin, contentWidth);
                }

                break;
            case LEFT:
                posX = anchorRect.left - contentWidth;
                posY = anchorRect.centerY() - (contentHeight / 2);
                popupWindow.setAnimationStyle(R.style.Animations_Right);
                break;
            case RIGHT:
                posX = anchorRect.right;
                posY = anchorRect.centerY() - (contentHeight / 2);
                popupWindow.setAnimationStyle(R.style.Animations_Left);
                break;
        }


        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, posX, posY);
    }

    private void setPopupAnimation(boolean onTop, int arrowMargin, int contentWidth) {
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

    private void showArrow(ImageView arrow, int margin) {
        arrow.setVisibility(View.VISIBLE);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)arrow.getLayoutParams();
        param.leftMargin = margin - arrow.getMeasuredWidth() / 2;
    }

    private int calculatePositionX(int centerX, int contentWidth, int screenSizeX) {
        int posX = Math.max(centerX - contentWidth / 2, 0);
        // If pops the screen size, move it more left
        if (posX + contentWidth > screenSizeX) {
            posX = Math.max(posX - (posX + contentWidth - screenSizeX), 0);
        }

        return posX;
    }
}
