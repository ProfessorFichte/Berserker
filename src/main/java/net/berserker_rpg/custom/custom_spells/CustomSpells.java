package net.berserker_rpg.custom.custom_spells;

import net.berserker_rpg.damage.BerserkerSpellCostSource;
import net.berserker_rpg.effect.Effects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.more_rpg_classes.effect.MRPGCEffects;
import net.spell_engine.api.spell.CustomSpellHandler;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.SpellInfo;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.utils.TargetHelper;

import java.util.List;
import java.util.function.Predicate;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;
import static net.berserker_rpg.BerserkerClassMod.effectsConfig;
import static net.more_rpg_classes.util.CustomMethods.clearNegativeEffects;
import static net.spell_engine.internals.SpellRegistry.getSpell;

public class CustomSpells {
    public static void register() {
        float spellcost_soulaxe_drain = 1.0f;
        int wild_rage_duration = 600;

        /// BLOODY STRIKE
        CustomSpellHandler.register(Identifier.of(MOD_ID, "bloody_strike"), (data) -> {
            SpellInfo spellinfo = new SpellInfo(getSpell(Identifier.of(MOD_ID, "bloody_strike")),Identifier.of(MOD_ID));
            Spell.Impact[] impacts = getSpell(Identifier.of(MOD_ID, "bloody_strike")).impact;
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            float modifier = getSpell(Identifier.of(MOD_ID, "bloody_strike")).impact[0].action.damage.spell_power_coefficient;
            var attack_damage = data1.caster().getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            float actual_health_player = data1.caster().getHealth();
            double amount = modifier * attack_damage;
            float self_damage_calc = (float) (amount * effectsConfig.value.bloody_strike_self_damage);

            for (Entity entity : data1.targets()) {
                if (entity instanceof LivingEntity living) {
                    if(actual_health_player <= 0.5F){
                        data1.caster().addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 300,2,false,false,true));
                        data1.caster().addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 300,2,false,false,true));
                        entity.damage(living.getDamageSources().playerAttack(data1.caster()),(float) amount/2);
                    }else{
                        SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, entity, spellinfo,impacts ,data1.impactContext());
                        EntityType<?> type = ((Entity) living).getType();
                        if(!type.isIn(EntityTypeTags.UNDEAD)){
                            ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(MRPGCEffects.BLEEDING.registryEntry,100));
                        }
                        if(self_damage_calc > actual_health_player){
                            data1.caster().setHealth(0.5F);
                        }else{
                            data1.caster().setHealth(actual_health_player- self_damage_calc);
                        }
                    }
                    return true;
                }
            }
            return true;
        });

        /// WILD RAGE
        CustomSpellHandler.register(Identifier.of(MOD_ID, "wild_rage"), (data) -> {
            SpellInfo spellinfo = new SpellInfo(getSpell(Identifier.of(MOD_ID, "wild_rage")),Identifier.of(MOD_ID));
            Spell.Impact[] impacts = getSpell(Identifier.of(MOD_ID, "wild_rage")).impact;
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            Predicate<Entity> selectionPredicate = (target2) -> {
                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, data1.caster(), target2)
                );
            };
            if (!data1.caster().getWorld().isClient) {
                data1.caster().addStatusEffect(new StatusEffectInstance(Effects.RAGE.registryEntry, wild_rage_duration));
                List<Entity> list = data1.caster().getWorld().getOtherEntities(data1.caster(), data1.caster().getBoundingBox().expand(getSpell(Identifier.of(MOD_ID, "wild_rage")).range), selectionPredicate);
                for (Entity entity : list) {
                    SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, entity, spellinfo,impacts, data1.impactContext());
                }
            }
            return true;
        });

        /// OUTRAGE
        CustomSpellHandler.register(Identifier.of(MOD_ID, "outrage"), (data) -> {
            SpellInfo spellinfo = new SpellInfo(getSpell(Identifier.of(MOD_ID, "outrage")),Identifier.of(MOD_ID));
            Spell.Impact[] impacts = getSpell(Identifier.of(MOD_ID, "outrage")).impact;
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            if (!data1.caster().getWorld().isClient) {
                for (Entity entity : data1.targets()) {
                    SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, entity, spellinfo,impacts ,data1.impactContext());
                    if (data1.caster().hasStatusEffect(Effects.RAGE.registryEntry)) {
                        final int amp_rage = data1.caster().getStatusEffect(Effects.RAGE.registryEntry).getAmplifier();
                        final int dura_rage = data1.caster().getStatusEffect(Effects.RAGE.registryEntry).getDuration();
                        int rage_amplifier_max = effectsConfig.value.rage_max_amplifier_stack - 1;
                        if(amp_rage == rage_amplifier_max){
                            clearNegativeEffects(data1.caster(),true);
                            data1.caster().addStatusEffect(new StatusEffectInstance(Effects.RAGE.registryEntry, dura_rage + 10,rage_amplifier_max,false,false,true));
                        }
                    }
                }
            }
            return false;
        });

        /// SOULAXE DRAIN
        CustomSpellHandler.register(Identifier.of(MOD_ID,"soulaxe_drain"),(data) -> {
            SpellInfo spellinfo = new SpellInfo(getSpell(Identifier.of(MOD_ID, "soulaxe_drain")),Identifier.of(MOD_ID));
            Spell.Impact[] impacts = getSpell(Identifier.of(MOD_ID, "soulaxe_drain")).impact;
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            Predicate<Entity> selectionPredicate = (target2) -> {
                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, data1.caster(), target2)
                );
            };
            if (!data1.caster().getWorld().isClient) {
                data1.caster().damage(new BerserkerSpellCostSource(data1.caster().getDamageSources().starve().getTypeRegistryEntry()), spellcost_soulaxe_drain);
                List<Entity> list = data1.caster().getWorld().getOtherEntities(data1.caster(), data1.caster().getBoundingBox().expand(getSpell(Identifier.of(MOD_ID, "soulaxe_drain")).range), selectionPredicate);
                for (Entity entity : list) {
                    SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, entity, spellinfo, impacts,data1.impactContext());
                }
            }
            return false;
        });
        /// RUMBLING SWING
        CustomSpellHandler.register(Identifier.of(MOD_ID,"rumbling_swing"),(data) -> {
            SpellInfo spellinfo = new SpellInfo(getSpell(Identifier.of(MOD_ID, "rumbling_swing")),Identifier.of(MOD_ID));
            Spell.Impact[] impacts = getSpell(Identifier.of(MOD_ID, "rumbling_swing")).impact;
            float range = getSpell(Identifier.of(MOD_ID, "rumbling_swing")).range;
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            BlockHitResult result = data1.caster().getWorld().raycast(new RaycastContext(data1.caster().getEyePos(), data1.caster().getEyePos()
                    .add(data1.caster().getRotationVector().multiply(getSpell(Identifier.of(MOD_ID,"rumbling_swing")).range)),
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,data1.caster()));
            if(result.getPos() != null) {
                data1.caster().requestTeleport(result.getPos().getX(),result.getPos().getY(),result.getPos().getZ());
            }
            List<Entity> list = TargetHelper.targetsFromArea(data1.caster(),data1.caster().getEyePos(),range,new Spell.Release.Target.Area(), target -> TargetHelper.allowedToHurt(data1.caster(),target) );
            for(Entity entity : list){
                SpellHelper.performImpacts(data1.caster().getWorld(),data1.caster(),entity,data1.caster(), spellinfo,impacts,data1.impactContext());
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
