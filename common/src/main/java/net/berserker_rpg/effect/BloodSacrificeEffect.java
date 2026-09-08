package net.berserker_rpg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

import static net.berserker_rpg.BerserkerClassMod.tweaksConfig;

public class BloodSacrificeEffect extends StatusEffect {
    private int healthPerStack;

    public BloodSacrificeEffect(StatusEffectCategory category, int color) {
        super(category, color);
        this.healthPerStack = 2  ;
    }

    /// 1.20.1 `applyUpdateEffect` returns **void**: the 1.21 `boolean` result (whose `false` ends the effect
    /// early) does not exist, and a status effect cannot remove itself from inside the tick loop
    /// (`LivingEntity#tickStatusEffects` iterates the live map). The 1.21.1 body was
    /// `entity.getAbsorptionAmount() > 0.0F || entity.getWorld().isClient` -- i.e. the effect ended as soon
    /// as the granted absorption was chewed through. **Port sacrifice:** here it always runs its full
    /// duration. Nothing rides on it besides the HUD icon (its max-absorption modifier has no 1.20.1
    /// counterpart, see BerserkerEffects), so the only visible difference is the icon lingering.
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        float modifier = 1.0F;
        if(entity instanceof PlayerEntity playerEntity && !playerEntity.isCreative()){
            var attack_damage = entity.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            float actual_health_player = entity.getHealth();
            double amount = modifier * attack_damage;
            float self_damage_calc = (float) (amount * tweaksConfig.value.bloody_strike_self_damage);
            if(actual_health_player > 1.0F){
                if(self_damage_calc > actual_health_player){
                    entity.setHealth(0.5F);
                }else{
                    entity.setHealth(actual_health_player- self_damage_calc);
                }
                entity.setAbsorptionAmount(Math.max(entity.getAbsorptionAmount(), healthPerStack  * (1 + amplifier )));
            }
        }
        super.onApplied(entity, attributes, amplifier);
    }

}