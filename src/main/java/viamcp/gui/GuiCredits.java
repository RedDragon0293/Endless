package viamcp.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

public class GuiCredits extends GuiScreen
{
    private GuiScreen parent;

    public GuiCredits(GuiScreen parent)
    {
        this.parent = parent;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        buttonList.add(new GuiButton(1, width / 2 - 100, height - 25, 200, 20, "Back"));
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException
    {
        if (guiButton.id == 1)
        {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();

        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0, 2.0, 2.0);
        String title = EnumChatFormatting.BOLD + "Credits";
        //drawString(this.fontRendererObj, title, (this.width - (this.fontRendererObj.getStringWidth(title) * 2)) / 4, 5, -1);
        mc.fontRendererObj.drawString(title, (this.width - (mc.fontRendererObj.getStringWidth(title) * 2)) / 4, 5, -1);
        GlStateManager.popMatrix();

        int fixedHeight = ((5 + mc.fontRendererObj.FONT_HEIGHT) * 2) + 2;

        String viaVerTeam = EnumChatFormatting.GRAY + (EnumChatFormatting.BOLD + "ViaVersion Team");
        String florMich = EnumChatFormatting.GRAY + (EnumChatFormatting.BOLD + "FlorianMichael");
        String laVache = EnumChatFormatting.GRAY + (EnumChatFormatting.BOLD + "LaVache-FR");
        String hideri = EnumChatFormatting.GRAY + (EnumChatFormatting.BOLD + "Hiderichan / Foreheadchan");
        String contactInfo = EnumChatFormatting.GRAY + (EnumChatFormatting.BOLD + "Contact Info");

        drawString(mc.fontRendererObj, viaVerTeam, (this.width - mc.fontRendererObj.getStringWidth(viaVerTeam)) / 2, fixedHeight, -1);
        drawString(mc.fontRendererObj, "ViaVersion", (this.width - mc.fontRendererObj.getStringWidth("ViaVersion")) / 2, fixedHeight + mc.fontRendererObj.FONT_HEIGHT, -1);
        drawString(mc.fontRendererObj, "ViaBackwards", (this.width - mc.fontRendererObj.getStringWidth("ViaBackwards")) / 2, fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 2, -1);
        drawString(mc.fontRendererObj, "ViaRewind", (this.width - mc.fontRendererObj.getStringWidth("ViaRewind")) / 2, fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 3, -1);

        drawString(mc.fontRendererObj, florMich, (this.width - mc.fontRendererObj.getStringWidth(florMich)) / 2, fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 5, -1);
        drawString(mc.fontRendererObj, "ViaForge", (this.width - mc.fontRendererObj.getStringWidth("ViaForge")) / 2, fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 6, -1);

        drawString(mc.fontRendererObj, laVache, (this.width - mc.fontRendererObj.getStringWidth(laVache)) / 2, fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 8, -1);
        drawString(mc.fontRendererObj, "Original ViaMCP", (this.width - mc.fontRendererObj.getStringWidth("Original ViaMCP")) / 2, fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 9, -1);

        drawString(mc.fontRendererObj, hideri, (this.width - mc.fontRendererObj.getStringWidth(hideri)) / 2, fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 11, -1);
        drawString(mc.fontRendererObj, "ViaMCP Reborn", (this.width - mc.fontRendererObj.getStringWidth("ViaMCP Reborn")) / 2, fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 12, -1);

        drawString(mc.fontRendererObj, contactInfo, (this.width - mc.fontRendererObj.getStringWidth(contactInfo)) / 2, fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 14, -1);
        drawString(mc.fontRendererObj, "Discord: Hideri#9003", (this.width - mc.fontRendererObj.getStringWidth("Discord: Hideri#9003")) / 2, fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 15, -1);

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        drawString(mc.fontRendererObj, EnumChatFormatting.GRAY + "(https://github.com/ViaVersion/ViaVersion)", (this.width + mc.fontRendererObj.getStringWidth("ViaVersion ")), (fixedHeight + mc.fontRendererObj.FONT_HEIGHT) * 2 + mc.fontRendererObj.FONT_HEIGHT / 2, -1);
        drawString(mc.fontRendererObj, EnumChatFormatting.GRAY + "(https://github.com/ViaVersion/ViaBackward)", (this.width + mc.fontRendererObj.getStringWidth("ViaBackwards ")), (fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 2) * 2 + mc.fontRendererObj.FONT_HEIGHT / 2, -1);
        drawString(mc.fontRendererObj, EnumChatFormatting.GRAY + "(https://github.com/ViaVersion/ViaRewind)", (this.width + mc.fontRendererObj.getStringWidth("ViaRewind ")), (fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 3) * 2 + mc.fontRendererObj.FONT_HEIGHT / 2, -1);
        drawString(mc.fontRendererObj, EnumChatFormatting.GRAY + "(https://github.com/FlorianMichael/ViaForge)", (this.width + mc.fontRendererObj.getStringWidth("ViaForge ")), (fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 6) * 2 + mc.fontRendererObj.FONT_HEIGHT / 2, -1);
        drawString(mc.fontRendererObj, EnumChatFormatting.GRAY + "(https://github.com/LaVache-FR/ViaMCP)", (this.width + mc.fontRendererObj.getStringWidth("Original ViaMCP ")), (fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 9) * 2 + mc.fontRendererObj.FONT_HEIGHT / 2, -1);
        drawString(mc.fontRendererObj, EnumChatFormatting.GRAY + "(https://github.com/Foreheadchann/ViaMCP-Reborn)", (this.width + mc.fontRendererObj.getStringWidth("ViaMCP Reborn ")), (fixedHeight + mc.fontRendererObj.FONT_HEIGHT * 12) * 2 + mc.fontRendererObj.FONT_HEIGHT / 2, -1);
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
