package net.berserker_rpg.client.armor;

import net.minecraft.util.Identifier;
import net.rpg_foundation.armor_api.client.GeoArmorRenderer;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public final class CustomArmorRenderer {

    private CustomArmorRenderer() { }

    public static GeoArmorRenderer wildling_armor() {
        return make("wildling_armor", "wildling");
    }
    public static GeoArmorRenderer northling_armor() {
        return make("northling_armor", "northling");
    }
    public static GeoArmorRenderer netherite_northling_armor() {
        return make("northling_armor", "netherite_northling");
    }
    public static GeoArmorRenderer warlord_armor() {
        return make("warlord_armor", "warlord");
    }

    private static GeoArmorRenderer make(String modelName, String textureName) {
        return GeoArmorRenderer.of(
                Identifier.of(MOD_ID, "geo/" + modelName + ".geo.json"),
                Identifier.of(MOD_ID, "textures/armor/" + textureName + ".png"));
    }
}
