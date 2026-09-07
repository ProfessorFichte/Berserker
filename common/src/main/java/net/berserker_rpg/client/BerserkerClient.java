package net.berserker_rpg.client;

import net.berserker_rpg.client.armor.CustomArmorRenderer;
import net.berserker_rpg.client.effect.RageParticles;
import net.berserker_rpg.client.effect.RageRenderer;
import net.berserker_rpg.effect.BerserkerEffects;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.spell.BerserkerSpells;
import net.rpg_foundation.armor_api.client.ArmorRenderers;
import net.rpg_foundation.armor_api.client.GeoArmorRenderer;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.rpg_series.item.Armor;

import static net.berserker_rpg.compat.CompatLoadingCheck.armoryLoadCheck;

public class BerserkerClient {
    public static void  init(){
        BerserkerSpells.registerTooltipTokens();

        registerArmorRenderer(Armors.wildlingArmorSet.armorSet(), CustomArmorRenderer.wildling_armor());
        registerArmorRenderer(Armors.northlingArmorSet.armorSet(), CustomArmorRenderer.northling_armor());
        registerArmorRenderer(Armors.netheriteNorthlingArmorSet.armorSet(), CustomArmorRenderer.netherite_northling_armor());
        if (armoryLoadCheck()) {
            registerArmorRenderer(Armors.warlordArmorSet.armorSet(), CustomArmorRenderer.warlord_armor());
        }
        CustomParticleStatusEffect.register(BerserkerEffects.RAGE.effect, new RageParticles(1));
        CustomModelStatusEffect.register(BerserkerEffects.RAGE.effect, new RageRenderer());
    }

    private static void registerArmorRenderer(Armor.Set set, GeoArmorRenderer renderer) {
        ArmorRenderers.register(renderer, set.head, set.chest, set.legs, set.feet);
    }

}
