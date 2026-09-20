package com.berserker.forge.client;

import net.berserker_rpg.client.BerserkerClient;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class ForgeClient {
    private ForgeClient() { }

    public static void register(IEventBus modBus) {
        modBus.addListener(EventPriority.NORMAL, false, FMLClientSetupEvent.class, ForgeClient::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        BerserkerClient.init();
    }
}
