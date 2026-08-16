package net.berserker_rpg.custom.custom_spells;

import net.berserker_rpg.damage.BerserkerSpellCostSource;
import net.berserker_rpg.effect.Effects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.more_rpg_classes.effect.MRPGCEffects;
import net.more_rpg_classes.entity.attribute.MRPGCEntityAttributes;
import net.spell_engine.api.spell.CustomSpellHandler;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.SpellInfo;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.utils.TargetHelper;

import java.util.List;
import java.util.function.Predicate;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;
import static net.berserker_rpg.BerserkerClassMod.effectsConfig;
import static net.more_rpg_classes.util.CustomMethods.removeEffects;
import static net.spell_engine.internals.SpellRegistry.getSpell;

public class CustomSpells {
    public static void register() {
        float spellcost_soulaxe_drain = 1.0f;
        int wild_rage_duration = 600;

        /// BLOODY STRIKE
        CustomSpellHandler.register(new Identifier(MOD_ID, "bloody_strike"), (data) -> {
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            float modifier = getSpell(new Identifier(MOD_ID, "bloody_strike")).impact[0].action.damage.spell_power_coefficient;
            float rage_attr = (float) ((data1.caster().getAttributeValue(MRPGCEntityAttributes.RAGE_MODIFIER)-100));
            float actual_absorption = data1.caster().getAbsorptionAmount();
            var attack_damage = data1.caster().getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            float actual_health_player = data1.caster().getHealth();
            double amount = modifier * attack_damage;
            float maxabsoprtion = data1.caster().getMaxHealth() * (effectsConfig.value.bloody_strike_absorption_limit_max_health + ((rage_attr/100) * effectsConfig.value.bloody_strike_rage_absorption_boost_multiplier));
            float absorptionamount = ((float) amount * effectsConfig.value.bloody_strike_damage_to_absorption)+ actual_absorption;
            float self_damage_calc = (float) (amount * effectsConfig.value.bloody_strike_self_damage);

            for (Entity entity : data1.targets()) {
                if (entity instanceof LivingEntity living) {
                    if(actual_health_player <= 0.5F){
                        data1.caster().addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 160,0,false,false,true));
                        data1.caster().addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 160,0,false,false,true));
                    }else{
                        if(self_damage_calc > actual_health_player){
                            data1.caster().setHealth(0.5F);
                        }else{
                            data1.caster().setHealth(actual_health_player- self_damage_calc);
                        }
                        if(absorptionamount < maxabsoprtion){
                            data1.caster().setAbsorptionAmount(absorptionamount);
                        }else {
                            data1.caster().setAbsorptionAmount(maxabsoprtion);
                        }
                    }

                    if (living.isUndead()) {
                        entity.damage(living.getDamageSources().playerAttack(data1.caster()),(float) amount);
                    } else {
                        SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, entity, new SpellInfo(getSpell(new Identifier(MOD_ID, "bloody_strike")),new Identifier(MOD_ID)), data1.impactContext());
                        ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(MRPGCEffects.BLEEDING,6));
                    }
                    return true;
                }
            }
            return true;
        });

        /// WILD RAGE
        CustomSpellHandler.register(new Identifier(MOD_ID, "wild_rage"), (data) -> {
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            Predicate<Entity> selectionPredicate = (target2) -> {
                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, data1.caster(), target2)
                );
            };
            if (!data1.caster().getWorld().isClient) {
                data1.caster().addStatusEffect(new StatusEffectInstance(Effects.RAGE, wild_rage_duration));
                List<Entity> list = data1.caster().getWorld().getOtherEntities(data1.caster(), data1.caster().getBoundingBox().expand(getSpell(new Identifier(MOD_ID, "wild_rage")).range), selectionPredicate);
                for (Entity entity : list) {
                    SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, entity, new SpellInfo(getSpell(new Identifier(MOD_ID, "wild_rage")),new Identifier(MOD_ID)), data1.impactContext());
                }
            }
            return true;
        });

        /// OUTRAGE
        CustomSpellHandler.register(new Identifier(MOD_ID, "outrage"), (data) -> {
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            if (!data1.caster().getWorld().isClient) {
                for (Entity entity : data1.targets()) {
                    SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, entity, new SpellInfo(getSpell(new Identifier(MOD_ID, "outrage")),new Identifier(MOD_ID)), data1.impactContext());
                    if (data1.caster().hasStatusEffect(Effects.RAGE)) {
                        final int amp_rage = data1.caster().getStatusEffect(Effects.RAGE).getAmplifier();
                        final int dura_rage = data1.caster().getStatusEffect(Effects.RAGE).getDuration();
                        int rage_amplifier_max = effectsConfig.value.rage_max_amplifier_stack - 1;
                        if(amp_rage == rage_amplifier_max){
                            removeEffects(data1.caster(),true,false);
                            data1.caster().addStatusEffect(new StatusEffectInstance(Effects.RAGE, dura_rage + 10,rage_amplifier_max,false,false,true));
                        }
                    }
                }
            }
            return false;
        });

        /// SOULAXE DRAIN
        CustomSpellHandler.register(new Identifier(MOD_ID,"soulaxe_drain"),(data) -> {
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            Predicate<Entity> selectionPredicate = (target2) -> {
                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, data1.caster(), target2)
                );
            };
            if (!data1.caster().getWorld().isClient) {
                data1.caster().damage(new BerserkerSpellCostSource(data1.caster().getDamageSources().starve().getTypeRegistryEntry()), spellcost_soulaxe_drain);
                List<Entity> list = data1.caster().getWorld().getOtherEntities(data1.caster(), data1.caster().getBoundingBox().expand(getSpell(new Identifier(MOD_ID, "soulaxe_drain")).range), selectionPredicate);
                for (Entity entity : list) {
                    SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, entity, new SpellInfo(getSpell(new Identifier(MOD_ID, "soulaxe_drain")),new Identifier(MOD_ID)), data1.impactContext());
                }
            }
            return false;
        });
        /// RUMBLING SWING
        CustomSpellHandler.register(new Identifier(MOD_ID,"rumbling_swing"),(data) -> {
            float range = getSpell(new Identifier(MOD_ID, "rumbling_swing")).range;
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            BlockHitResult result = data1.caster().getWorld().raycast(new RaycastContext(data1.caster().getEyePos(), data1.caster().getEyePos()
                    .add(data1.caster().getRotationVector().multiply(getSpell(new Identifier(MOD_ID,"rumbling_swing")).range)),
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,data1.caster()));
            if(result.getPos() != null) {
                data1.caster().requestTeleport(result.getPos().getX(),result.getPos().getY(),result.getPos().getZ());
            }
            List<Entity> list = TargetHelper.targetsFromArea(data1.caster(),data1.caster().getEyePos(),range,new Spell.Release.Target.Area(), target -> TargetHelper.allowedToHurt(data1.caster(),target) );
            for(Entity entity : list){
                SpellHelper.performImpacts(data1.caster().getWorld(),data1.caster(),entity,data1.caster(), new SpellInfo(getSpell(new Identifier(MOD_ID, "rumbling_swing")),new Identifier(MOD_ID)),data1.impactContext());
            }
            return true;
        });

        /*
        /// NORDIC STORM
        CustomSpellHandler.register(new Identifier(MOD_ID, "nordic_storm"), (data) -> {
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;

            Predicate<Entity> selectionPredicate = (target2) -> {
                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, data1.caster(), target2)
                );
            };
                if (!data1.caster().getWorld().isClient) {
                    List<Entity> list = data1.caster().getWorld().getOtherEntities(data1.caster(), data1.caster().getBoundingBox().expand(getSpell(new Identifier(MOD_ID, "nordic_storm")).range), selectionPredicate);
                    ParticleHelper.sendBatches(data1.caster(), getSpell(new Identifier(MOD_ID, "nordic_storm")).release.particles);
                    for (Entity entity : list) {
                        SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, entity , new SpellInfo(getSpell(new Identifier(MOD_ID, "nordic_storm")),new Identifier(MOD_ID)), data1.impactContext());
                        SoundHelper.playSound(data1.caster().getWorld(), entity, getSpell(new Identifier(MOD_ID, "nordic_storm")).impact[0].sound);
                    }
                }
            return false;
        });
        */
    }
}
