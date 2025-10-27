package net.berserker_rpg.client.armor;

import mod.azure.azurelibarmor.rewrite.render.AzRendererConfig;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class CustomArmorRenderer extends AzArmorRenderer {
    public CustomArmorRenderer(AzRendererConfig<ItemStack> config) {
        super(config);
    }

    public static CustomArmorRenderer wildling_armor() {
        return new CustomArmorRenderer("wildling_armor", "wildling");
    }
    public static CustomArmorRenderer northling_armor() {
        return new CustomArmorRenderer("northling_armor", "northling");
    }
    public static CustomArmorRenderer netherite_northling_armor() {
        return new CustomArmorRenderer("northling_armor", "netherite_northling");
    }
    public static CustomArmorRenderer warlord_armor() {
        return new CustomArmorRenderer("warlord_armor", "warlord");
    }


    public CustomArmorRenderer(String modelName, String textureName) {
        super(AzArmorRendererConfig.builder(
                Identifier.of(MOD_ID, "geo/" + modelName + ".geo.json"),
                Identifier.of(MOD_ID, "textures/armor/" + textureName + ".png")
        ).build());
    }
}
