package net.sourcerer.quickaction;

import net.sourcerer.android.R;
import android.graphics.drawable.Drawable;

public class CheckActionItem extends ActionItem {

    public CheckActionItem(String title) {
        this(title, null);
    }

    public CheckActionItem(Drawable icon) {
        this(null, icon);
    }

    public CheckActionItem(String title, Drawable icon) {
        this(title, icon, R.layout.qa_action_item_check);
    }

    public CheckActionItem(String title, Drawable icon, int layoutId) {
        super(title, icon, layoutId);
    }

    public boolean isChecked() {
        return isSelected();
    }
}
