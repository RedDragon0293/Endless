package net.minecraft.client.gui.loading;

import net.minecraft.client.gui.Gui;

public abstract class AbstractLoadingGui extends Gui {
    public boolean doesGuiPauseGame() {
        return true;
    }
}
