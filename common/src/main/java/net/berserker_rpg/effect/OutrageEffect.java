package net.berserker_rpg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

import static net.more_rpg_classes.util.CustomMethods.clearNegativeEffects;

public class OutrageEffect extends StatusEffect {
    protected OutrageEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
        clearNegativeEffects(entity,false);
        var effect = BerserkerEffects.RAGE.entry;
        if(entity.hasStatusEffect(effect)){
            StatusEffectInstance rageInstance = new StatusEffectInstance(effect,entity.getStatusEffect(effect).getDuration() * 2,
                    entity.getStatusEffect(effect).getAmplifier(),
                    true,false,true);
            entity.removeStatusEffect(effect);
            entity.addStatusEffect(rageInstance);
        }
        entity.setAbsorptionAmount(0);
    }
}
