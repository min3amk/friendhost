package com.friendhost;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HostScreen extends Screen {
    private final Screen parent;
    private EditBox addressBox;
    private EditBox portBox;
    private Button copyButton;
    private String localError = "";

    private static String lastAddress = "";
    private static String lastPort = "25565";

    public HostScreen(Screen parent) {
        super(Component.literal("Host for friends"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;

        addressBox = new EditBox(this.font, cx - 100, 45, 200, 20, Component.literal("Public address"));
        addressBox.setMaxLength(120);
        addressBox.setHint(Component.literal("Tunnel or domain (optional)"));
        addressBox.setValue(lastAddress);
        addRenderableWidget(addressBox);

        portBox = new EditBox(this.font, cx - 100, 85, 200, 20, Component.literal("Port"));
        portBox.setMaxLength(5);
        portBox.setValue(lastPort);
        addRenderableWidget(portBox);

        addRenderableWidget(Button.builder(Component.literal("Start hosting"), b -> onStart())
                .bounds(cx - 100, 115, 200, 20).build());

        copyButton = addRenderableWidget(Button.builder(Component.literal("Copy invite code"), b -> {
            String code = HostManager.getCode();
            if (code != null) this.minecraft.keyboardHandler.setClipboard(code);
        }).bounds(cx - 100, 180, 200, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Done"),
                b -> this.minecraft.setScreen(parent)).bounds(cx - 100, 205, 200, 20).build());
    }

    private void onStart() {
        int port;
        try {
            port = Integer.parseInt(portBox.getValue().trim());
        } catch (NumberFormatException e) {
            localError = "Port must be a number.";
            return;
        }
        if (port < 1024 || port > 65535) {
            localError = "Port must be between 1024 and 65535.";
            return;
        }
        localError = "";
        lastAddress = addressBox.getValue().trim();
        lastPort = String.valueOf(port);
        HostManager.start(port, lastAddress);
    }

    @Override
    public void tick() {
        super.tick();
        copyButton.active = HostManager.getCode() != null;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        int cx = this.width / 2;
        g.drawCenteredString(this.font, this.title, cx, 15, 0xFFFFFF);
        g.drawString(this.font, "Public address (leave empty to detect your IP)", cx - 100, 34, 0xA0A0A0);
        g.drawString(this.font, "Port", cx - 100, 74, 0xA0A0A0);

        String code = HostManager.getCode();
        if (code != null) {
            g.drawCenteredString(this.font, Component.literal("Invite code:"), cx, 145, 0xA0A0A0);
            g.drawCenteredString(this.font, Component.literal(code), cx, 157, 0x55FF55);
        }
        String msg = !localError.isEmpty() ? localError : HostManager.getStatus();
        if (!msg.isEmpty()) {
            g.drawCenteredString(this.font, Component.literal(msg), cx, 168,
                    localError.isEmpty() ? 0xFFFFFF : 0xFF5555);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}
