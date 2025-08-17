package net.berserker_rpg.client;

import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererRegistry;
import net.berserker_rpg.BerserkerClassMod;
import net.berserker_rpg.client.armor.CustomArmorRenderer;
import net.berserker_rpg.client.effect.RageParticles;
import net.berserker_rpg.client.effect.RageRenderer;
import net.berserker_rpg.client.particle.Particles;
import net.berserker_rpg.client.particle.SmallThunderParticle;
import net.berserker_rpg.effect.BerserkerEffects;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.armor.ArmoryCompat;
import net.berserker_rpg.spell.BerserkerSpells;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.particle.*;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.client.gui.SpellTooltip;

import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class BerserkerClient implements ClientModInitializer {
    public void  onInitializeClient(){
        for (var entry: BerserkerSpells.entries) {
            if (entry.mutator() != null) {
                SpellTooltip.addDescriptionMutator(entry.id(), entry.mutator());
            }
        }
        CustomModels.registerModelIds(List.of(
                RageRenderer.modelIdRage
        ));
        ParticleFactoryRegistry.getInstance().register(Particles.RAGE_PAR, DamageParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Particles.SMALL_THUNDER, SmallThunderParticle.Factory::new);

        registerArmorRenderer(Armors.wildlingArmorSet, CustomArmorRenderer::wildling_armor);
        registerArmorRenderer(Armors.northlingArmorSet, CustomArmorRenderer::northling_armor);
        registerArmorRenderer(Armors.netheriteNorthlingArmorSet, CustomArmorRenderer::netherite_northling_armor);
        /*
        if (FabricLoader.getInstance().isModLoaded("armory_rpgs") || BerserkerClassMod.tweaksConfig.value.ignore_items_required_mods) {
            registerArmorRenderer(ArmoryCompat.warlordArmorSet.armorSet(), CustomArmorRenderer::warlord_armor);
        }
        */

        CustomParticleStatusEffect.register(BerserkerEffects.RAGE.effect, new RageParticles(1));
        CustomModelStatusEffect.register(BerserkerEffects.RAGE.effect, new RageRenderer());
    }

    private static void registerArmorRenderer(Armor.Set set, Supplier<AzArmorRenderer> armorRendererSupplier) {
        AzArmorRendererRegistry.register(armorRendererSupplier, set.head, set.chest, set.legs, set.feet);
    }

}
