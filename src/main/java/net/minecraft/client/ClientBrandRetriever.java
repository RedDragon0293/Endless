package net.minecraft.client;

import cn.asone.endless.features.special.FakeForge;

public class ClientBrandRetriever {
    public static String getClientModName() {
        if (FakeForge.enabled.get() && FakeForge.payload.get())
            return "fml,forge";
        else
            return "vanilla";
    }
}
