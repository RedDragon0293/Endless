package net.minecraft.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Gui {
    public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
    public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    protected float zLevel;

    /**
     * Draw a 1 pixel wide horizontal line. Args: x1, x2, y, color
     */
    protected void drawHorizontalLine(int startX, int endX, int y, int color) {
        if (endX < startX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        drawRect(startX, y, endX + 1, y + 1, color);
    }

    /**
     * Draw a 1 pixel wide vertical line. Args : x, y1, y2, color
     */
    protected void drawVerticalLine(int x, int startY, int endY, int color) {
        if (endY < startY) {
            int i = startY;
            startY = endY;
            endY = i;
        }

        drawRect(x, startY + 1, x + 1, endY, color);
    }

    /**
     * Draws a solid color rectangle with the specified coordinates and color (ARGB format). Args: x1, y1, x2, y2, color
     */
    public static void drawRect(int left, int top, int right, int bottom, int color) {
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /**
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float startA = (startColor >> 24 & 255) / 255.0F;
        float startR = (startColor >> 16 & 255) / 255.0F;
        float startG = (startColor >> 8 & 255) / 255.0F;
        float startB = (startColor & 255) / 255.0F;
        float endA = (endColor >> 24 & 255) / 255.0F;
        float endR = (endColor >> 16 & 255) / 255.0F;
        float endG = (endColor >> 8 & 255) / 255.0F;
        float endB = (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, this.zLevel).color(startR, startG, startB, startA).endVertex();
        worldrenderer.pos(left, top, this.zLevel).color(startR, startG, startB, startA).endVertex();
        worldrenderer.pos(left, bottom, this.zLevel).color(endR, endG, endB, endA).endVertex();
        worldrenderer.pos(right, bottom, this.zLevel).color(endR, endG, endB, endA).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    /**
     * Renders the specified text to the screen, center-aligned. Args : renderer, string, x, y, color
     */
    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawStringWithShadow(text, (float) (x - fontRendererIn.getStringWidth(text) / 2), (float) y, color);
    }

    /**
     * Renders the specified text to the screen. Args : renderer, string, x, y, color
     */
    public void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
        fontRendererIn.drawStringWithShadow(text, (float) x, (float) y, color);
    }

    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, this.zLevel).tex((float) textureX * f, (float) (textureY + height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, this.zLevel).tex((float) (textureX + width) * f, (float) (textureY + height) * f1).endVertex();
        worldrenderer.pos(x + width, y, this.zLevel).tex((float) (textureX + width) * f, (float) textureY * f1).endVertex();
        worldrenderer.pos(x, y, this.zLevel).tex((float) textureX * f, (float) textureY * f1).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a textured rectangle using the texture currently bound to the TextureManager
     */
    public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(xCoord, yCoord + maxV, this.zLevel).tex(minU * f, (minV + maxV) * f1).endVertex();
        worldrenderer.pos(xCoord + maxU, yCoord + maxV, this.zLevel).tex((minU + maxU) * f, (minV + maxV) * f1).endVertex();
        worldrenderer.pos(xCoord + maxU, yCoord, this.zLevel).tex((minU + maxU) * f, minV * f1).endVertex();
        worldrenderer.pos(xCoord, yCoord, this.zLevel).tex(minU * f, minV * f1).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a texture rectangle using the texture currently bound to the TextureManager
     */
    public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(xCoord, yCoord + heightIn, this.zLevel).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
        worldrenderer.pos(xCoord + widthIn, yCoord + heightIn, this.zLevel).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
        worldrenderer.pos(xCoord + widthIn, yCoord, this.zLevel).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
        worldrenderer.pos(xCoord, yCoord, this.zLevel).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a textured rectangle at z = 0
     *
     * @param x      Start x position on the screen
     * @param y      Start y position on the screen
     * @param startX Start x position in the texture
     * @param startY Start y position in the texture
     */
    public static void drawModalRectWithCustomSizedTexture(int x, int y, float startX, float startY, int width, int height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D)
                .tex(startX * f, (startY + (float) height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D)
                .tex((startX + (float) width) * f, (startY + (float) height) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D)
                .tex((startX + (float) width) * f, startY * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D)
                .tex(startX * f, startY * f1).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a scaled, textured, tiled modal rect at z = 0. This method isn't used anywhere in vanilla code.
     */
    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + (float) uWidth) * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + (float) uWidth) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }
}
