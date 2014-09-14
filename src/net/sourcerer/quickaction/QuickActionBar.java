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

    private ViewGroup buttonList;
    private ImageView topArrow;
    private ImageView bottomArrow;
    private HorizontalScrollView scroller;

    // Default orientation is bottom
    private int orientation = BOTTOM;
    // No maximum width set = null
    private Integer maxWidth = null;
    // Using screen width forces the bar to expand the full screen
    private boolean useScreenWidth = false;
    // Buttons list
    private List<ActionItem> actions = new ArrayList<ActionItem>();
    //
    private View boundaries;

    public QuickActionBar(Context context) {
        this(context, R.layout.qa_dialog);
    }

    public QuickActionBar(Context context, int layoutId) {
        super(context);

        View container = setContentView(layoutId);

        // Fetching Ids for view elements, changed identifier at the layout will lead to null pointer here!
        Resources res = container.getResources();
        int buttonListId = res.getIdentifier("qa_button_list", "id", container.getContext().getPackageName());
        int topArrowId = res.getIdentifier("qa_arrow_up", "id", container.getContext().getPackageName());
        int bottomArrowId = res.getIdentifier("qa_arrow_down", "id", container.getContext().getPackageName());
        int scrollerId = res.getIdentifier("qa_scroller", "id", container.getContext().getPackageName());

        buttonList = (ViewGroup) container.findViewById(buttonListId);
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

    /**
     * Force the bar to use screen width will override the max width value.
     */
    public void useScreenWidth(boolean useScreenWidth) {
        this.useScreenWidth = useScreenWidth;
    }

    /**
     * Restricts the dialog boundaries to the given views size.
     * The anchor on show(anchor) has to be inside the restricting view.
     */
    public void useRestriction(View boundaries) {
        this.boundaries = boundaries;
    }

    /**
     * Set position to anchor.
     * @param orientation options: TOP, BOTTOM, LEFT, RIGHT
     */
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

        View button = inflater.inflate(action.getLayoutId(), null);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action.isSticky()) {
                    action.setSelected(!action.isSelected());
                }

                if (action.hasOnClickListener()) {
                    action.getQuickActionClickListener().onClick(action, v);
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

        buttonList.addView(button, buttonList.getChildCount() - 1);

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
            if (action.hasOnOpenListener()) {
                action.getQuickActionOnOpenListener().onOpen(action);
            }
        }

        Point screenSize = getScreenSize();

        // if boundaries are given, take them as screen size
        if (boundaries != null) {
            screenSize = new Point(boundaries.getWidth(), boundaries.getHeight());
        }

        // Calculate dialogs size
        Point contentSize = calculateContentSize(anchor, screenSize);

        // Force maximum size
        popupWindow.setWidth(contentSize.x);

        // Calculate position and set arrow
        Point position = calculatePosition(anchor, contentSize, screenSize);

        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, position.x, position.y);
    }

    private Point calculatePosition(View anchor, Point contentSize, Point screenSize) {

        final int contentWidth = contentSize.x;
        final int contentHeight = contentSize.y;

        int posX = 0;
        int posY = 0;
        int arrowMargin = 0;

        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        final Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());

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

        return new Point(posX, posY);
    }

    private Point calculateContentSize(View anchor, Point screenSize) {
        content.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        int contentWidth = content.getMeasuredWidth();
        int contentHeight  = content.getMeasuredHeight();

        // Reduce popup to screen size
        if (contentWidth > screenSize.x) {
            contentWidth = screenSize.x;
        }

        // Reduce size so it still show the anchor
        if (orientation == LEFT || orientation == RIGHT) {
            if (contentWidth > screenSize.x - anchor.getWidth() || useScreenWidth) {
                contentWidth = screenSize.x - anchor.getWidth();
            }
        }
        else if (useScreenWidth) {
            contentWidth = screenSize.x;
        }

        // Set max width, if set
        if (maxWidth != null && maxWidth < contentWidth) {
            contentWidth = maxWidth;
        }

        return new Point(contentWidth, contentHeight);
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

        if (boundaries != null) {
            // Push position into boundaries
            if (posX < boundaries.getLeft()) {
                posX = boundaries.getLeft();
            }
        }

        return posX;
    }
}
