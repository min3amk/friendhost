package com.friendhost;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;

public final class HostManager {
    private static IntegratedServer hostedServer;
    private static String publicIp;
    private static String override = "";
    private static int port;
    private static String code;
    private static String status = "";

    private HostManager() {}

    public static String getStatus() {
        return status;
    }

    public static boolean isActive() {
        IntegratedServer s = Minecraft.getInstance().getSingleplayerServer();
        return code != null && s != null && s == hostedServer && s.isPublished();
    }

    public static String getCode() {
        return isActive() ? code : null;
    }

    public static void start(int requestedPort, String addressOverride) {
        Minecraft mc = Minecraft.getInstance();
        IntegratedServer server = mc.getSingleplayerServer();
        if (server == null) {
            status = "Open a single-player world first.";
            return;
        }
        override = addressOverride == null ? "" : addressOverride.trim();

        if (server != hostedServer || !server.isPublished()) {
            code = null;
            publicIp = null;
            // Offline mode lets friends with or without a paid account join.
            server.setUsesAuthentication(false);
            if (!server.publishServer(server.getDefaultGameType(), false, requestedPort)) {
                server.setUsesAuthentication(true);
                status = "Could not open port " + requestedPort + ". Try another port.";
                return;
            }
            hostedServer = server;
        }
        port = server.getPort();
        resolveAddress();
    }

    private static void resolveAddress() {
        if (!override.isEmpty()) {
            applyAddress(override);
            return;
        }
        if (publicIp != null) {
            applyAddress(publicIp);
            return;
        }
        status = "Looking up your public IP...";
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.ipify.org"))
                .timeout(Duration.ofSeconds(5)).GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).whenComplete((resp, err) ->
                Minecraft.getInstance().execute(() -> {
                    if (err != null || resp == null || resp.statusCode() != 200) {
                        status = "Could not find your public IP. Type a tunnel address and press Start again.";
                        return;
                    }
                    publicIp = resp.body().trim();
                    applyAddress(publicIp);
                }));
    }

    private static void applyAddress(String host) {
        String full = host.contains(":") ? host : host + ":" + port;
        code = InviteCode.encode(full);
        status = "Hosting! Share the code. Friends outside your network need port " + port
                + " forwarded, or a tunnel address.";
    }
}
