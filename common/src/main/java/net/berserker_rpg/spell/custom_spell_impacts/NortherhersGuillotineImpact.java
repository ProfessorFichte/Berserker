package net.berserker_rpg.spell.custom_spell_impacts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.more_rpg_classes.util.CustomMethods;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.event.SpellHandlers;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.compat.CriticalStrikeCompat;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellModifiers;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;

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

            boolean executeThreshold = livingTarget.getHealth() <= livingTarget.getMaxHealth() * tweaksConfig.value.northerners_guillotine_execute_health_threshold;
            if (executeThreshold) {
                forcedCriticalDamage(spell.value(), damageMultiplier, livingTarget, playerCaster);
            } else {
                CustomMethods.spellSchoolDamageCalculation(spell.value(), damageMultiplier, livingTarget, playerCaster);
            }
        }
        return new SpellHandlers.ImpactResult(true, false);
    }

    private static void forcedCriticalDamage(Spell spell, float damageMultiplication, LivingEntity target, LivingEntity caster) {
        var school = spell.school;
        var power = SpellPower.getSpellPower(school, caster);

        var registry = SpellRegistry.from(caster.getWorld());
        var spellId = registry.getId(spell);
        var spellEntry = spellId != null ? registry.getEntry(spellId).orElse(null) : null;

        if (spellEntry != null) {
            var bonusPower = 1F;
            var bonusCritChance = 0F;
            var bonusCritDamage = 0F;
            for (var modifier : SpellModifiers.of(caster, spellEntry, null)) {
                if (modifier.power_modifier != null) {
                    bonusPower += modifier.power_modifier.power_multiplier;
                    bonusCritChance += modifier.power_modifier.critical_chance_bonus;
                    bonusCritDamage += modifier.power_modifier.critical_damage_bonus;
                }
            }
            power = new SpellPower.Result(power.school(),
                    power.baseValue() * bonusPower,
                    power.criticalChance() + bonusCritChance,
                    power.criticalDamage() + bonusCritDamage);
        }

        var result = power.forcedCritical();
        float damageAmount = (float) result.amount() * damageMultiplication;

        var damageSource = SpellDamageSource.create(school, caster);
        CriticalStrikeCompat.setCriticalStrike(damageSource, (float) power.criticalDamage());
        target.damage(damageSource, damageAmount);
    }
}
