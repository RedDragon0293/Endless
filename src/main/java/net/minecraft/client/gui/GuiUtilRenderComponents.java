package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.List;

public class GuiUtilRenderComponents {
    public static String func_178909_a(String text, boolean chatColors) {
        return !chatColors && !Minecraft.getMinecraft().gameSettings.chatColours ? EnumChatFormatting.getTextWithoutFormattingCodes(text) : text;
    }

    /**
     * 将含有换行符的ChatComponent分割至多行
     *
     * @param colors 是否保留 Style 标识
     * @param width  目标宽度
     */
    public static List<IChatComponent> splitText(IChatComponent chatComponent, int width, FontRenderer fontRenderer, boolean removeSpace, boolean colors) {
        int currentWidth = 0;
        IChatComponent emptyComponent = new ChatComponentText("");
        List<IChatComponent> resultList = Lists.newArrayList();
        /*
        用于临时储存待处理的ChatComponent
         */
        List<IChatComponent> tempList = Lists.newArrayList(chatComponent);

        for (int index = 0; index < tempList.size(); ++index) {
            IChatComponent currentComponent = tempList.get(index);
            String currentTextUnformatted = currentComponent.getUnformattedTextForChat();
            boolean newLine = false;

            if (currentTextUnformatted.contains("\n")) {
                int k = currentTextUnformatted.indexOf(10 /* 换行符 */);
                /*
                换行符之后的内容
                 */
                String stringAfterNewLine = currentTextUnformatted.substring(k + 1);
                /*
                换行符之前的内容
                 */
                currentTextUnformatted = currentTextUnformatted.substring(0, k + 1);
                ChatComponentText afterNewLineComponent = new ChatComponentText(stringAfterNewLine);
                afterNewLineComponent.setChatStyle(currentComponent.getChatStyle().createShallowCopy());
                tempList.add(index + 1, afterNewLineComponent);
                newLine = true;
            }

            /*
            如果上一个if语句中已经分割换行符前后的内容, 则为换行符前的内容
             */
            String currentTextOrigin = func_178909_a(currentComponent.getChatStyle().getFormattingCode() + currentTextUnformatted, colors);
            /*
            去除末尾的换行符
             */
            String currentText = currentTextOrigin.endsWith("\n") ? currentTextOrigin.substring(0, currentTextOrigin.length() - 1) : currentTextOrigin;
            int width1 = fontRenderer.getStringWidth(currentText);
            ChatComponentText currentComponent1 = new ChatComponentText(currentText);
            currentComponent1.setChatStyle(currentComponent.getChatStyle().createShallowCopy());

            if (currentWidth + width1 > width) {
                String trimmed = fontRenderer.trimStringToWidth(currentTextOrigin, width - currentWidth, false);
                /*
                被裁切掉的部分
                 */
                String s3 = trimmed.length() < currentTextOrigin.length() ? currentTextOrigin.substring(trimmed.length()) : null;

                if (s3 != null) {
                    int l = trimmed.lastIndexOf(" ");

                    if (l >= 0 && fontRenderer.getStringWidth(currentTextOrigin.substring(0, l)) > 0) {
                        /*
                        如果裁切后的字符串 (trimmed) 内有空格, 则替换为空格前的内容
                        */
                        trimmed = currentTextOrigin.substring(0, l);

                        if (removeSpace) {
                            ++l;
                        }

                        s3 = currentTextOrigin.substring(l);
                    } else if (currentWidth > 0 && !currentTextOrigin.contains(" ")) {
                        trimmed = "";
                        s3 = currentTextOrigin;
                    }

                    //Forge: Fix chat formatting not surviving line wrapping.
                    s3 = FontRenderer.getFormatFromString(trimmed) + s3;

                    ChatComponentText chatcomponenttext2 = new ChatComponentText(s3);
                    chatcomponenttext2.setChatStyle(currentComponent.getChatStyle().createShallowCopy());
                    tempList.add(index + 1, chatcomponenttext2);
                }

                width1 = fontRenderer.getStringWidth(trimmed);
                currentComponent1 = new ChatComponentText(trimmed);
                currentComponent1.setChatStyle(currentComponent.getChatStyle().createShallowCopy());
                newLine = true;
            }

            if (currentWidth + width1 <= width) {
                currentWidth += width1;
                emptyComponent.appendSibling(currentComponent1);
            } else {
                newLine = true;
            }

            if (newLine) {
                resultList.add(emptyComponent);
                currentWidth = 0;
                emptyComponent = new ChatComponentText("");
            }
        }

        resultList.add(emptyComponent);
        return resultList;
    }
}
