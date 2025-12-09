package com.berserker.fabric.client;

import net.berserker_rpg.client.BerserkerClient;
import net.fabricmc.api.ClientModInitializer;

public final class FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BerserkerClient.init();
    }
}
