package net.berserker_rpg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

import static net.berserker_rpg.BerserkerClassMod.tweaksConfig;

public class BloodReckoningEffect extends StatusEffect {
    protected BloodReckoningEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
        float missing_health = entity.getMaxHealth() - entity.getHealth();
        float absorption = missing_health * tweaksConfig.value.blood_reckoning_missing_health_to_absorption;
        entity.setAbsorptionAmount(Math.max(entity.getAbsorptionAmount(), absorption));
    }
}
