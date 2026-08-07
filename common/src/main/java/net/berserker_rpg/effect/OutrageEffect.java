package net.berserker_rpg.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.berserker_rpg.BerserkerClassMod.tweaksConfig;

public class OutrageEffect extends StatusEffect {
    private static final Set<UUID> APPLYING_RAGE_BONUS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    protected OutrageEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
        if (entity.getWorld().isClient()) return;
        if (APPLYING_RAGE_BONUS.remove(entity.getUuid())) return;

        if (entity.hasStatusEffect(BerserkerEffects.RAGE.entry)) {
            var current = entity.getStatusEffect(BerserkerEffects.OUTRAGE.entry);
            if (current != null) {
                int bonusTicks = Math.round(tweaksConfig.value.outrage_rage_bonus_duration * 20F);
                APPLYING_RAGE_BONUS.add(entity.getUuid());
                entity.addStatusEffect(new StatusEffectInstance(BerserkerEffects.OUTRAGE.entry, current.getDuration() + bonusTicks, amplifier));
            }
        }
    }
}
