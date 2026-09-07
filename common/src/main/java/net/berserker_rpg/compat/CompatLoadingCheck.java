package net.berserker_rpg.compat;

import net.spell_engine.Platform;

public class CompatLoadingCheck {
    public static boolean armoryLoadCheck(){
        return Platform.util().isModLoaded("armory_rpgs") | Platform.util().isDevelopmentEnvironment();
    }
}

