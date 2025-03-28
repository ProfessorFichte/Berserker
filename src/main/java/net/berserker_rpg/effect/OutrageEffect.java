package net.berserker_rpg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.more_rpg_classes.util.CustomMethods.clearNegativeEffects;

public class OutrageEffect extends StatusEffect {
    protected OutrageEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.hasStatusEffect(Effects.RAGE.registryEntry)) {
            final int amp_rage = entity.getStatusEffect(Effects.RAGE.registryEntry).getAmplifier();
            final int dura_rage = entity.getStatusEffect(Effects.RAGE.registryEntry).getDuration();
            int rage_amplifier_max = 9;
            if(amp_rage == rage_amplifier_max && !entity.getWorld().isClient() && entity instanceof ServerPlayerEntity){
                clearNegativeEffects(entity,true);
                entity.addStatusEffect(new StatusEffectInstance(Effects.RAGE.registryEntry, dura_rage + 10,rage_amplifier_max,false,false,true));
            }
        }
        return true;
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
