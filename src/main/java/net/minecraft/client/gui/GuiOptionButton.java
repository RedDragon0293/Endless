package net.minecraft.client.gui;

import net.minecraft.client.settings.GameSettings;

public class GuiOptionButton extends GuiButton {
    private final GameSettings.Options enumOptions;

    public GuiOptionButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, null, buttonText);
    }

    public GuiOptionButton(int buttonId, int x, int y, int width, int height, String buttonText) {
        super(buttonId, x, y, width, height, buttonText);
        this.enumOptions = null;
    }

    public GuiOptionButton(int buttonId, int x, int y, GameSettings.Options options, String buttonText) {
        super(buttonId, x, y, 150, 20, buttonText);
        this.enumOptions = options;
    }

    public GameSettings.Options returnEnumOptions() {
        return this.enumOptions;
    }
}
