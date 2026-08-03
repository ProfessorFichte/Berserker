package net.berserker_rpg.spell.custom_spell_impacts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.more_rpg_classes.util.CustomMethods;
import net.spell_engine.api.effect.EntityActionsAllowed;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.compat.CriticalStrikeCompat;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellModifiers;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;

import java.util.List;

import static net.berserker_rpg.BerserkerClassMod.tweaksConfig;

public class NortherhersGuillotineImpact implements SpellHandlers.CustomImpact {
    public static final float BASE_DAMAGE_MULTIPLIER = 1.2F;

    @Override
    public SpellHandlers.ImpactResult onSpellImpact(
            RegistryEntry<Spell> spell,
            SpellPower.Result powerResult,
            LivingEntity caster,
            Entity target,
            SpellHelper.ImpactContext context
    ) {
        if (target instanceof LivingEntity livingTarget && caster instanceof PlayerEntity playerCaster) {
            int harmfulAmplifierSum = livingTarget.getStatusEffects().stream()
                    .filter(instance -> instance.getEffectType().value().getCategory() == StatusEffectCategory.HARMFUL)
                    .mapToInt(instance -> instance.getAmplifier() + 1)
                    .sum();

            float damageMultiplier = BASE_DAMAGE_MULTIPLIER + (harmfulAmplifierSum * tweaksConfig.value.northerners_guillotine_damage_per_amplifier);
            CustomMethods.spellSchoolDamageCalculation(spell.value().school, damageMultiplier,livingTarget,playerCaster);
        }
        return new SpellHandlers.ImpactResult(true, false);
    }
}
