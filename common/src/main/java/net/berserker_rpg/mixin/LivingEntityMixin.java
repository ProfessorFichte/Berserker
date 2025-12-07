package net.berserker_rpg.mixin;

import net.berserker_rpg.item.weapons.BerserkerAxeItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"))
    private void beforeDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity target = (LivingEntity) (Object) this;

        if (target.getWorld().isClient) {
            return;
        }

        if (source.getAttacker() instanceof LivingEntity attacker) {
            var weapon = attacker.getMainHandStack();

            if (weapon.getItem() instanceof BerserkerAxeItem) {
                boolean hasShield = target.getMainHandStack().isOf(Items.SHIELD) ||
                                   target.getOffHandStack().isOf(Items.SHIELD);
                if (hasShield) {
                    boolean isOnCooldown = false;
                    if (target instanceof PlayerEntity player) {
                        isOnCooldown = player.getItemCooldownManager().isCoolingDown(Items.SHIELD);
                    }
                    if (!isOnCooldown) {

                        if (target instanceof PlayerEntity player) {
                            player.getItemCooldownManager().set(Items.SHIELD, 100);
                        }
                        target.getWorld().sendEntityStatus(target, (byte) 30);
                    }
                }
            }
        }
    }
}
