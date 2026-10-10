package com.vixel.autogambler;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class VixelAutoGambler implements ClientModInitializer {

    private int amount = 2;

    @Override
    public void onInitializeClient() {

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String chat = message.getString().toLowerCase();

            Minecraft client = Minecraft.getInstance();

            if (client.player == null || client.getConnection() == null) {
                return;
            }

            if (chat.contains("heads")) {
                // Win: reset the amount.
                amount = 2;
                sendCommand(client);

            } else if (chat.contains("tails")) {
                // Loss: send the current amount.
                sendCommand(client);

                // Double the amount for the next flip.
                if (amount <= Integer.MAX_VALUE / 2) {
                    amount *= 2;
                }
            }
        });
    }

    private void sendCommand(Minecraft client) {
        client.execute(() -> {
            if (client.player != null) {
                ClientPacketListener handler = client.getConnection();
                if (handler != null) {
                    handler.sendCommand("coinflip " + amount);
                }
            }
        });
    }
}