package engine.graphics.objects.gui;

public enum GuiObjectRole {

    TEXT(false),
    BACKGROUND(true),
    BACKGROUND_HOVER(true),
    BACKGROUND_PRESSED(true);

    private final boolean isBackground;

    GuiObjectRole(boolean isBackground) {
        this.isBackground = isBackground;
    }

    public boolean isBackground() {
        return isBackground;
    }

}
