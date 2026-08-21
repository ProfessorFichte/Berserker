package net.berserker_rpg.client;

import mod.azure.azurelibarmor.common.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.common.render.armor.AzArmorRendererRegistry;
import net.berserker_rpg.client.armor.CustomArmorRenderer;
import net.berserker_rpg.client.effect.RageParticles;
import net.berserker_rpg.client.effect.RageRenderer;
import net.berserker_rpg.effect.BerserkerEffects;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.spell.BerserkerSpells;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.rpg_series.item.Armor;

import java.util.function.Supplier;

import static net.berserker_rpg.compat.CompatLoadingCheck.armoryLoadCheck;

public class BerserkerClient {
    public static void  init(){
        // Description values that aren't expressible as declarative `{token}`s (config-derived numbers).
        // `TooltipTokens` is server-safe; this is only called here because `BerserkerSpells` has no
        // other runtime touch point (it is otherwise datagen-only), so its statics need forcing.
        BerserkerSpells.registerTooltipTokens();

        registerArmorRenderer(Armors.wildlingArmorSet.armorSet(), CustomArmorRenderer::wildling_armor);
        registerArmorRenderer(Armors.northlingArmorSet.armorSet(), CustomArmorRenderer::northling_armor);
        registerArmorRenderer(Armors.netheriteNorthlingArmorSet.armorSet(), CustomArmorRenderer::netherite_northling_armor);
        if (armoryLoadCheck()) {
            registerArmorRenderer(Armors.warlordArmorSet.armorSet(), CustomArmorRenderer::warlord_armor);
        }
        CustomParticleStatusEffect.register(BerserkerEffects.RAGE.effect, new RageParticles(1));
        CustomModelStatusEffect.register(BerserkerEffects.RAGE.effect, new RageRenderer());
    }

    private static void registerArmorRenderer(Armor.Set set, Supplier<AzArmorRenderer> armorRendererSupplier) {
        AzArmorRendererRegistry.register(armorRendererSupplier, set.head, set.chest, set.legs, set.feet);
    }

}
