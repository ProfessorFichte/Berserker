package net.berserker_rpg.client;

import mod.azure.azurelibarmor.common.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.common.render.armor.AzArmorRendererRegistry;
import net.berserker_rpg.client.armor.CustomArmorRenderer;
import net.berserker_rpg.client.effect.RageParticles;
import net.berserker_rpg.client.effect.RageRenderer;
import net.berserker_rpg.effect.BerserkerEffects;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.spell.BerserkerSpells;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.*;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.client.gui.SpellTooltip;

import java.util.List;
import java.util.function.Supplier;

import static net.berserker_rpg.compat.CompatLoadingCheck.armoryLoadCheck;

public class BerserkerClient {
    public static void  init(){
        for (var entry: BerserkerSpells.entries) {
            if (entry.mutator() != null) {
                SpellTooltip.addDescriptionMutator(entry.id(), entry.mutator());
            }
        }
        CustomModels.registerModelIds(List.of(
                RageRenderer.modelIdRage
        ));

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
