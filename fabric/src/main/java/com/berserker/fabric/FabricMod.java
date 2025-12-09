package com.berserker.fabric;

import net.berserker_rpg.BerserkerClassMod;
import net.fabricmc.api.ModInitializer;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        BerserkerClassMod.init();
        BerserkerClassMod.registerItems();
        BerserkerClassMod.registerSounds();
        BerserkerClassMod.registerEffects();
    }
}
