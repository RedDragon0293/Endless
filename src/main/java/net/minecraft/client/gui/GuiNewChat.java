package net.minecraft.client.gui;

import cn.asone.endless.utils.animation.SmoothHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.List;

public class GuiNewChat extends Gui {
    private static final Logger logger = LogManager.getLogger("MCChat");
    private final Minecraft mc;
    private final List<String> sentMessages = Lists.newArrayList();
    private final List<ChatLine> chatLines = Lists.newArrayList();
    private final List<ChatLine> drawnChatLines = Lists.newArrayList();
    private final SmoothHelper scrollHelper = new SmoothHelper();
    private boolean isScrolled;

    public GuiNewChat(Minecraft mcIn) {
        this.mc = mcIn;
    }

    public void drawChat(int updateCounter) {
        scrollHelper.tick();
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int lineCount = this.getLineCount();
            boolean chatOpen = false;
            /*
            已经渲染了的ChatLine总数
             */
            int drawnChatLineCount = 0;
            int drawnChatLines = this.drawnChatLines.size();
            float opacity = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            if (drawnChatLines > 0) {
                int chatLineHeight = this.mc.fontRendererObj.FONT_HEIGHT;
                if (this.getChatOpen()) {
                    chatOpen = true;
                }

                float chatScale = this.getChatScale();
                int rectWidth = MathHelper.ceiling_float_int((float) this.getChatWidth() / chatScale);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 20.0F, 0.0F);
                GlStateManager.scale(chatScale, chatScale, 1.0F);

                for (int index = 0; index + (int) (this.scrollHelper.getCurrentValue()) < this.drawnChatLines.size() && index < lineCount; ++index) {
                    ChatLine chatline = this.drawnChatLines.get(index + (int) (this.scrollHelper.get()));

                    if (chatline != null) {
                        int counter = updateCounter - chatline.getUpdatedCounter();

                        /*
                        渲染焦点外的ChatLine
                         */
                        if (counter < 200 || chatOpen) {
                            /*
                            用于计算ChatLine淡出
                             */
                            double percent = (double) counter / 200.0D;
                            percent = 1.0D - percent;
                            percent = percent * 10.0D;
                            percent = MathHelper.clamp_double(percent, 0.0D, 1.0D);
                            percent = percent * percent;
                            int alpha = (int) (255.0D * percent);

                            if (chatOpen) {
                                alpha = 255;
                            }

                            alpha = (int) ((float) alpha * opacity);
                            ++drawnChatLineCount;

                            if (alpha > 3) {
                                int startX = 0;
                                int bottomY = -index * chatLineHeight;
                                drawRect(startX, bottomY - chatLineHeight, startX + rectWidth + 4, bottomY, alpha / 2 << 24);
                                String s = chatline.getChatComponent().getFormattedText();
                                GlStateManager.enableBlend();
                                this.mc.fontRendererObj.drawStringWithShadow(s, (float) startX, (float) (bottomY - chatLineHeight + 1), 16777215 + (alpha << 24));
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                }

                /*
                scrolling滑条
                 */
                if (chatOpen) {
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int l2 = drawnChatLines * chatLineHeight + drawnChatLines;
                    int i3 = drawnChatLineCount * chatLineHeight + drawnChatLineCount;
                    int j3 = (int) (this.scrollHelper.get()) * i3 / drawnChatLines;
                    int k1 = i3 * i3 / l2;

                    if (l2 != i3) {
                        int k3 = j3 > 0 ? 170 : 96;
                        int l3 = this.isScrolled ? 13382451 : 3355562;
                        drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
                        drawRect(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24));
                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }

    /**
     * Clears the chat.
     */
    public void clearChatMessages() {
        this.drawnChatLines.clear();
        this.chatLines.clear();
        this.sentMessages.clear();
    }

    public void printChatMessage(IChatComponent component) {
        this.printChatMessageWithOptionalDeletion(component, 0);
    }

    /**
     * prints the ChatComponent to Chat. If the ID is not 0, deletes an existing Chat Line of that ID from the GUI
     */
    public void printChatMessageWithOptionalDeletion(IChatComponent component, int id) {
        this.setChatLine(component, id, this.mc.ingameGUI.getUpdateCounter(), false);
        logger.info(component.getUnformattedText());
    }

    private void setChatLine(IChatComponent component, int id, int updateCounter, boolean p_146237_4_) {
        if (id != 0) {
            this.deleteChatLine(id);
        }

        int i = MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale());
        List<IChatComponent> list = GuiUtilRenderComponents.splitText(component, i, this.mc.fontRendererObj, false, false);
        boolean flag = this.getChatOpen();

        for (IChatComponent ichatcomponent : list) {
            if (flag && this.scrollHelper.getCurrentValue() > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }

            this.drawnChatLines.add(0, new ChatLine(updateCounter, ichatcomponent, id));
        }

        while (this.drawnChatLines.size() > 100) {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }

        if (!p_146237_4_) {
            this.chatLines.add(0, new ChatLine(updateCounter, component, id));

            while (this.chatLines.size() > 100) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public void refreshChat() {
        this.drawnChatLines.clear();
        this.resetScroll();

        for (int i = this.chatLines.size() - 1; i >= 0; --i) {
            ChatLine chatline = this.chatLines.get(i);
            this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
        }
    }

    public List<String> getSentMessages() {
        return this.sentMessages;
    }

    /**
     * Adds this string to the list of sent messages, for recall using the up/down arrow keys
     */
    public void addToSentMessages(String p_146239_1_) {
        if (this.sentMessages.isEmpty() || !(this.sentMessages.get(this.sentMessages.size() - 1)).equals(p_146239_1_)) {
            this.sentMessages.add(p_146239_1_);
        }
    }

    /**
     * Resets the chat scroll (executed when the GUI is closed, among others)
     */
    public void resetScroll() {
        //this.scrollPos = 0;
        scrollHelper.setCurrentValue(0);
        this.isScrolled = false;
    }

    /**
     * Scrolls the chat by the given number of lines.
     */
    public void scroll(int wheel) {
        //this.scrollPos += wheel;
        scrollHelper.setCurrentValue(scrollHelper.getCurrentValue() + wheel);
        int i = this.drawnChatLines.size();

        if (this.scrollHelper.getCurrentValue() > i - this.getLineCount()) {
            //this.scrollPos = i - this.getLineCount();
            scrollHelper.setCurrentValue(i - this.getLineCount());
        }

        if (this.scrollHelper.getCurrentValue() <= 0) {
            //this.scrollPos = 0;
            scrollHelper.setCurrentValue(0);
            this.isScrolled = false;
        }
    }

    /**
     * Gets the chat component under the mouse
     */
    public IChatComponent getChatComponent(int p_146236_1_, int p_146236_2_) {
        if (this.getChatOpen()) {
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int i = scaledresolution.getScaleFactor();
            float f = this.getChatScale();
            int j = p_146236_1_ / i - 3;
            int k = p_146236_2_ / i - 27;
            j = MathHelper.floor_float((float) j / f);
            k = MathHelper.floor_float((float) k / f);

            if (j >= 0 && k >= 0) {
                int l = Math.min(this.getLineCount(), this.drawnChatLines.size());

                if (j <= MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRendererObj.FONT_HEIGHT * l + l) {
                    int i1 = k / this.mc.fontRendererObj.FONT_HEIGHT + (int) (this.scrollHelper.get());

                    if (i1 >= 0 && i1 < this.drawnChatLines.size()) {
                        ChatLine chatline = this.drawnChatLines.get(i1);
                        int j1 = 0;

                        for (IChatComponent ichatcomponent : chatline.getChatComponent()) {
                            if (ichatcomponent instanceof ChatComponentText) {
                                j1 += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText) ichatcomponent).getChatComponentText_TextValue(), false));

                                if (j1 > j) {
                                    return ichatcomponent;
                                }
                            }
                        }
                    }

                }
            }
        }
        return null;
    }

    /**
     * Returns true if the chat GUI is open
     */
    public boolean getChatOpen() {
        return this.mc.currentScreen instanceof GuiChat;
    }

    /**
     * finds and deletes a Chat line by ID
     */
    public void deleteChatLine(int p_146242_1_) {
        Iterator<ChatLine> iterator = this.drawnChatLines.iterator();

        while (iterator.hasNext()) {
            ChatLine chatline = iterator.next();

            if (chatline.getChatLineID() == p_146242_1_) {
                iterator.remove();
            }
        }

        iterator = this.chatLines.iterator();

        while (iterator.hasNext()) {
            ChatLine chatline1 = iterator.next();

            if (chatline1.getChatLineID() == p_146242_1_) {
                iterator.remove();
                break;
            }
        }
    }

    public int getChatWidth() {
        return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }

    public int getChatHeight() {
        return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    /**
     * Returns the chatscale from mc.gameSettings.chatScale
     */
    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(float p_146233_0_) {
        int i = 320;
        int j = 40;
        return MathHelper.floor_float(p_146233_0_ * (float) (i - j) + (float) j);
    }

    public static int calculateChatboxHeight(float p_146243_0_) {
        int i = 180;
        int j = 20;
        return MathHelper.floor_float(p_146243_0_ * (float) (i - j) + (float) j);
    }

    public int getLineCount() {
        return this.getChatHeight() / this.mc.fontRendererObj.FONT_HEIGHT;
    }
}
