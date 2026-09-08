package com.berserker.forge.client;

import net.berserker_rpg.client.BerserkerClient;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/// Client entrypoint. Only touched behind a `Dist.CLIENT` check in `ForgeMod`, and wired with an explicit
/// `addListener` instead of NeoForge's `@EventBusSubscriber(value = Dist.CLIENT)` (Forge 47's annotation
/// has a different shape and would classload this on a dedicated server).
public final class ForgeClient {
    private ForgeClient() { }

    public static void register(IEventBus modBus) {
        modBus.addListener(EventPriority.NORMAL, false, FMLClientSetupEvent.class, ForgeClient::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        BerserkerClient.init();
    }
}
