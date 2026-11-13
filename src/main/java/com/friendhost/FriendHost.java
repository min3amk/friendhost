package com.friendhost;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = FriendHost.MODID, dist = Dist.CLIENT)
public class FriendHost {
    public static final String MODID = "friendhost";

    public FriendHost() {
        NeoForge.EVENT_BUS.addListener(ClientEvents::onScreenInit);
    }
}
