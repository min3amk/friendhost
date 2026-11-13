package com.friendhost;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;

public class JoinScreen extends Screen {
    private final Screen parent;
    private EditBox codeBox;
    private String error = "";

    public JoinScreen(Screen parent) {
        super(Component.literal("Join with code"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        codeBox = new EditBox(this.font, cx - 100, cy - 20, 200, 20, Component.literal("Invite code"));
        codeBox.setMaxLength(300);
        codeBox.setHint(Component.literal("FH-... or 1.2.3.4:25565"));
        addRenderableWidget(codeBox);
        setInitialFocus(codeBox);

        addRenderableWidget(Button.builder(Component.literal("Paste"),
                b -> codeBox.setValue(this.minecraft.keyboardHandler.getClipboard()))
                .bounds(cx - 100, cy + 5, 98, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Join"), b -> join())
                .bounds(cx + 2, cy + 5, 98, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Cancel"),
                b -> this.minecraft.setScreen(parent)).bounds(cx - 100, cy + 30, 200, 20).build());
    }

    private void join() {
        String address = InviteCode.decode(codeBox.getValue());
        if (address == null) {
            error = "That code is not valid.";
            return;
        }
        ServerData data = new ServerData("Friend's world", address, ServerData.Type.OTHER);
        ConnectScreen.startConnecting(this, this.minecraft,
                ServerAddress.parseString(address), data, false, null);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        int cx = this.width / 2;
        int cy = this.height / 2;
        g.drawCenteredString(this.font, this.title, cx, cy - 60, 0xFFFFFF);
        g.drawCenteredString(this.font, Component.literal("Paste the invite code from your friend"),
                cx, cy - 36, 0xA0A0A0);
        if (!error.isEmpty()) {
            g.drawCenteredString(this.font, Component.literal(error), cx, cy + 58, 0xFF5555);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}
