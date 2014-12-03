package net.sourcerer.quickaction;

public enum PopUpAlignment {
    TOP(false),
    BOTTOM(false),
    LEFT(true),
    RIGHT(true);

    private boolean isHorizontal;

    private PopUpAlignment(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }
}
