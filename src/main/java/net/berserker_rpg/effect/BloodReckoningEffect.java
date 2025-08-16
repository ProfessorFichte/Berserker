package net.berserker_rpg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

import static net.berserker_rpg.BerserkerClassMod.effectsConfig;
import static net.berserker_rpg.BerserkerClassMod.tweaksConfig;

public class BloodReckoningEffect extends StatusEffect {
    protected BloodReckoningEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
        final float absorption = (float) entity.getAttributeValue(EntityAttributes.GENERIC_MAX_ABSORPTION);
        final float base_heal = tweaksConfig.value.blood_reckoning_base_heal;
        entity.heal(base_heal + (absorption * tweaksConfig.value.blood_reckoning_absoprtion_to_heal));
        if(entity.hasStatusEffect(BerserkerEffects.RAGE.entry)){
            final int amp_rage = entity.getStatusEffect(BerserkerEffects.RAGE.entry).getAmplifier();
            final int dura_rage = entity.getStatusEffect(BerserkerEffects.RAGE.entry).getDuration();
            entity.addStatusEffect(new StatusEffectInstance(BerserkerEffects.RAGE.entry, (int) ((dura_rage +absorption) * 2),amp_rage,false,false,true));
        }
        entity.setAbsorptionAmount(0);
    }
}
