package net.berserker_rpg.spell.custom_spell_impacts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.internals.SpellHelper;
import net.spell_power.api.SpellPower;

import static net.berserker_rpg.BerserkerClassMod.tweaksConfig;

public class BloodReckoningImpact implements SpellHandlers.CustomImpact {

    @Override
    public SpellHandlers.ImpactResult onSpellImpact(
            RegistryEntry<Spell> spell,
            SpellPower.Result powerResult,
            LivingEntity caster,
            Entity target,
            SpellHelper.ImpactContext context
    ) {
        if(caster instanceof PlayerEntity playerEntity){
            final float absorption = (float) playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_ABSORPTION);
            final float base_heal = tweaksConfig.value.blood_reckoning_base_heal;
            playerEntity.heal(base_heal + (absorption * tweaksConfig.value.blood_reckoning_absoprtion_to_heal));
            playerEntity.setAbsorptionAmount(0);
        }
        return new SpellHandlers.ImpactResult(true, false);
    }
}