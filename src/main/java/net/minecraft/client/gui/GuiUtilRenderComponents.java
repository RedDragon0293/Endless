package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.List;

public class GuiUtilRenderComponents {
    public static String func_178909_a(String text, boolean p_178909_1_) {
        return !p_178909_1_ && !Minecraft.getMinecraft().gameSettings.chatColours ? EnumChatFormatting.getTextWithoutFormattingCodes(text) : text;
    }

    public static List<IChatComponent> splitText(IChatComponent chatComponent, int width, FontRenderer fontRenderer, boolean p_178908_3_, boolean p_178908_4_) {
        int i = 0;
        IChatComponent emptyComponent = new ChatComponentText("");
        List<IChatComponent> list = Lists.newArrayList();
        List<IChatComponent> list1 = Lists.newArrayList(chatComponent);

        for (int j = 0; j < list1.size(); ++j) {
            IChatComponent ichatcomponent1 = list1.get(j);
            String s = ichatcomponent1.getUnformattedTextForChat();
            boolean flag = false;

            if (s.contains("\n")) {
                int k = s.indexOf(10);
                String s1 = s.substring(k + 1);
                s = s.substring(0, k + 1);
                ChatComponentText chatcomponenttext = new ChatComponentText(s1);
                chatcomponenttext.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
                list1.add(j + 1, chatcomponenttext);
                flag = true;
            }

            String s4 = func_178909_a(ichatcomponent1.getChatStyle().getFormattingCode() + s, p_178908_4_);
            String s5 = s4.endsWith("\n") ? s4.substring(0, s4.length() - 1) : s4;
            int i1 = fontRenderer.getStringWidth(s5);
            ChatComponentText chatcomponenttext1 = new ChatComponentText(s5);
            chatcomponenttext1.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());

            if (i + i1 > width) {
                String s2 = fontRenderer.trimStringToWidth(s4, width - i, false);
                String s3 = s2.length() < s4.length() ? s4.substring(s2.length()) : null;

                if (s3 != null) {
                    int l = s2.lastIndexOf(" ");

                    if (l >= 0 && fontRenderer.getStringWidth(s4.substring(0, l)) > 0) {
                        s2 = s4.substring(0, l);

                        if (p_178908_3_) {
                            ++l;
                        }

                        s3 = s4.substring(l);
                    } else if (i > 0 && !s4.contains(" ")) {
                        s2 = "";
                        s3 = s4;
                    }

                    ChatComponentText chatcomponenttext2 = new ChatComponentText(s3);
                    chatcomponenttext2.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
                    list1.add(j + 1, chatcomponenttext2);
                }

                i1 = fontRenderer.getStringWidth(s2);
                chatcomponenttext1 = new ChatComponentText(s2);
                chatcomponenttext1.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
                flag = true;
            }

            if (i + i1 <= width) {
                i += i1;
                emptyComponent.appendSibling(chatcomponenttext1);
            } else {
                flag = true;
            }

            if (flag) {
                list.add(emptyComponent);
                i = 0;
                emptyComponent = new ChatComponentText("");
            }
        }

        list.add(emptyComponent);
        return list;
    }
}
