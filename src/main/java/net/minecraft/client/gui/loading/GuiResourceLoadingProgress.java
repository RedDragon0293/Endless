package net.minecraft.client.gui.loading;

import cn.asone.endless.utils.ColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class GuiResourceLoadingProgress extends AbstractLoadingGui {
    private static final ResourceLocation MOJANG_LOGO_LOCATION = new ResourceLocation("textures/gui/title/mojang.png");
    private static final int BRAND_BACKGROUND = ColorHelper.getColorInt(255, 239, 50, 61);
    private static final int BRAND_BACKGROUND_NO_ALPHA = BRAND_BACKGROUND & 0xFFFFFF;
    private final Minecraft mc;

    public GuiResourceLoadingProgress(Minecraft mc) {
        this.mc = mc;
    }
}
