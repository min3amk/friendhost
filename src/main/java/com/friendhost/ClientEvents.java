package com.friendhost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class ClientEvents {
    private ClientEvents() {}

    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        Minecraft mc = Minecraft.getInstance();

        if (screen instanceof PauseScreen && mc.getSingleplayerServer() != null) {
            event.addListener(Button.builder(
                    Component.literal("Host for friends"),
                    b -> mc.setScreen(new HostScreen(screen)))
                    .bounds(6, 6, 120, 20).build());
        } else if (screen instanceof TitleScreen) {
            event.addListener(Button.builder(
                    Component.literal("Join with code"),
                    b -> mc.setScreen(new JoinScreen(screen)))
                    .bounds(6, 6, 100, 20).build());
        }
    }
}
