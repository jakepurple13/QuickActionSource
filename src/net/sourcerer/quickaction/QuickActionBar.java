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

    private ViewGroup buttonList;
    private ImageView topArrow;
    private ImageView bottomArrow;
    private HorizontalScrollView scroller;

    // Default orientation is bottom
    private PopUpAlignment alignment = PopUpAlignment.BOTTOM;
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
     * Set alignment to anchor.
     * @param alignment options: TOP, BOTTOM, LEFT, RIGHT
     */
    public void setAlignment(PopUpAlignment alignment) {
        this.alignment = alignment;
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

        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        final Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());

        // Calculate position and set arrow
        Point position = calculatePosition(anchorRect, contentSize, screenSize);

        // Hide arrows
        topArrow.setVisibility(View.INVISIBLE);
        bottomArrow.setVisibility(View.INVISIBLE);

        // Set animation style and arrow position
        if (alignment.isHorizontal() == false) {
            boolean isOnTop = anchorRect.top > position.y;

            int arrowMargin = anchorRect.centerX() - position.x;
            setVerticalPopupAnimation(isOnTop, arrowMargin, contentSize.x);

            if (isOnTop) {
                showArrow(bottomArrow, arrowMargin);
            }
            else {
                showArrow(topArrow, arrowMargin);
            }
        }
        else if (alignment == PopUpAlignment.LEFT) {
            popupWindow.setAnimationStyle(R.style.Animations_Right);
        }
        else if (alignment == PopUpAlignment.RIGHT) {
            popupWindow.setAnimationStyle(R.style.Animations_Left);
        }

        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, position.x, position.y);
    }

    private Point calculatePosition(Rect anchorRect, Point contentSize, Point screenSize) {

        final int contentWidth = contentSize.x;
        final int contentHeight = contentSize.y;

        int posX = 0;
        int posY = 0;

        // Calculate popup position by alignment and size
        switch(alignment) {
            case TOP:
                // Popup is centered on the anchor by moving left to fit into the boundaries
                posX = getHorizontalPosition(anchorRect.centerX(), contentWidth, screenSize.x);
                if (contentHeight < anchorRect.top) {
                    posY = anchorRect.top - contentHeight;
                }
                else {
                    posY = anchorRect.bottom;
                }
                break;
            case BOTTOM:
                // Popup is centered on the anchor by moving left to fit into the boundaries
                posX = getHorizontalPosition(anchorRect.centerX(), contentWidth, screenSize.x);
                if (contentHeight > screenSize.y - anchorRect.bottom) {
                    posY = anchorRect.top - contentHeight;
                }
                else {
                    posY = anchorRect.bottom;
                }
                break;
            case LEFT:
                posX = anchorRect.left - contentWidth;
                posY = anchorRect.centerY() - (contentHeight / 2);
                break;
            case RIGHT:
                posX = anchorRect.right;
                posY = anchorRect.centerY() - (contentHeight / 2);
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
        if (alignment == PopUpAlignment.LEFT || alignment == PopUpAlignment.RIGHT) {
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

    private int getHorizontalPosition(int centerX, int contentWidth, int screenSizeX) {
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
