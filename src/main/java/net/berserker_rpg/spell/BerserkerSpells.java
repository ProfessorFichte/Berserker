package net.berserker_rpg.spell;

import net.berserker_rpg.effect.BerserkerEffects;
import net.berserker_rpg.sounds.BerserkerSounds;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.more_rpg_classes.effect.MRPGCEffects;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.spell.ExternalSpellSchools;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.api.util.TriState;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
import net.spell_engine.internals.target.SpellTarget;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class BerserkerSpells {
    public record Entry(Identifier id, Spell spell, String title, String description,
                        @Nullable SpellTooltip.DescriptionMutator mutator) {
    }

    public static final List<Entry> entries = new ArrayList<>();

    private static Entry add(Entry entry) {
        entries.add(entry);
        return entry;
    }


    private static Spell.Impact damageImpact(float coefficient, float knockback) {
        var damage = new Spell.Impact();
        damage.action = new Spell.Impact.Action();
        damage.action.type = Spell.Impact.Action.Type.DAMAGE;
        damage.action.damage = new Spell.Impact.Action.Damage();
        damage.action.damage.spell_power_coefficient = coefficient;
        damage.action.damage.knockback = knockback;
        return damage;
    }
    private static void configureCooldown(Spell spell, float duration) {
        if (spell.cost == null) {
            spell.cost = new Spell.Cost();
        }
        spell.cost.cooldown = new Spell.Cost.Cooldown();
        spell.cost.cooldown.duration = duration;
    }
    private static Spell.Impact createEffectImpact(Identifier effectId, float duration) {
        var buff = new Spell.Impact();
        buff.action = new Spell.Impact.Action();
        buff.action.type = Spell.Impact.Action.Type.STATUS_EFFECT;
        buff.action.status_effect = new Spell.Impact.Action.StatusEffect();
        buff.action.status_effect.effect_id = effectId.toString();
        buff.action.status_effect.duration = duration;
        return buff;
    }

    private static Spell modifierSpellBase() {
        var spell = new Spell();
        spell.range = 0;
        spell.tier = 1;

        spell.type = Spell.Type.MODIFIER;

        spell.tooltip = new Spell.Tooltip();
        spell.tooltip.name = new Spell.Tooltip.LineOptions(false, true);
        spell.tooltip.description.color = Formatting.GRAY.asString();
        spell.tooltip.description.show_in_compact = true;
        spell.tooltip.name.show_in_compact = false;
        spell.tooltip.name.show_in_details = false;
        spell.tooltip.show_header = false;

        return spell;
    }
    private static Spell.Impact.TargetModifier createImpactModifier(String entityType) {
        var condition = new Spell.TargetCondition();
        condition.entity_type = entityType;
        var modifier = new Spell.Impact.TargetModifier();
        modifier.conditions = List.of(condition);
        return modifier;
    }
    private static void bleedingDeny(Spell.Impact impact) {
        var modifier = createImpactModifier("#more_rpg_classes:bleeding_immune");
        modifier.execute = TriState.DENY;
        impact.target_modifiers = List.of(modifier);
    }
    private static Spell passiveSpellBase() {
        var spell = new Spell();
        spell.range = 0;
        spell.tier = 8;

        spell.type = Spell.Type.PASSIVE;
        spell.passive = new Spell.Passive();
        return spell;
    }

    ///MODIFIERS
    public static Entry improved_bloody_strike = add(improved_bloody_strike());
    private static Entry improved_bloody_strike() {
        var id = Identifier.of(MOD_ID, "improved_bloody_strike");
        var title = "Improved Bloody Strike";
        var description = "Increases power multiplier of Bloody Strike by {power_multiplier}";
        var spell = modifierSpellBase();
        spell.school = MoreSpellSchools.RAGE_MELEE;

        var modifier = new Spell.Modifier();
        modifier.spell_pattern = "berserker_rpg:bloody_strike";
        modifier.power_modifier = new Spell.Impact.Modifier();
        modifier.power_modifier.power_multiplier = 0.1F;
        spell.modifiers = List.of(modifier);

        return new Entry(id, spell, title, description, null);
    }
    ///PASSIVES
    public static Entry carve_melee = add(carve_melee());
    private static Entry carve_melee() {
        var id = Identifier.of(MOD_ID, "carve");
        var title = "Carve";
        var description = "On melee hit: {trigger_chance} chance to stack armor reduction by {bonus2} and increasing incoming damage by {bonus} for {effect_amplifier_cap} times.";
        var spell = passiveSpellBase();
        spell.school = ExternalSpellSchools.PHYSICAL_MELEE;
        var debuffEffect = BerserkerEffects.CARVE;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = debuffEffect.config().attributes().get(1);
            var modifier2 = debuffEffect.config().attributes().get(0);
            var bonus = SpellTooltip.bonus(modifier.value, modifier.operation);
            var bonus2 = SpellTooltip.bonus(modifier2.value, modifier2.operation);
            return args.description()
                    .replace("{bonus}", bonus)
                    .replace("{bonus2}", bonus2);
        };

        var trigger = new Spell.Trigger();
        trigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        trigger.equipment_condition = EquipmentSlot.MAINHAND;
        trigger.chance = 0.2F;
        spell.passive.triggers = List.of(trigger);

        spell.target.type = Spell.Target.Type.FROM_TRIGGER;

        var debuff = createEffectImpact(debuffEffect.id, 5);
        debuff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        debuff.action.status_effect.show_particles = false;
        debuff.action.status_effect.amplifier = 1;
        debuff.action.status_effect.amplifier_cap = 5;
        debuff.action.status_effect.duration = 8;
        debuff.particles = new ParticleBatch[]{
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SKULL,
                                SpellEngineParticles.MagicParticles.Motion.DECELERATE).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.2F, 0.25F)
                        .color(Color.RAGE.toRGBA())
        };
        debuff.sound = new Sound(BerserkerSounds.CARVE.id().toString());
        spell.impacts = List.of(debuff);

        configureCooldown(spell, 3);
        spell.cost.batching = true;

        return new Entry(id, spell, title, description, mutator);
    }
    ///ACTIVE SPELLS
    public static final Entry wild_rage = add(wild_rage());
    private static Entry wild_rage() {
        var id = Identifier.of(MOD_ID, "wild_rage");
        var title = "Wild Rage";
        var description = "Enter a wild rage, increases rage by {bonus} and attack speed by {bonus2}, stacking up to {effect_amplifier_cap} times.";
        var effect = BerserkerEffects.RAGE;
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0;
        spell.tier = 1;
        spell.school = MoreSpellSchools.RAGE_MELEE;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().attributes().get(1);
            var modifier2 = effect.config().attributes().get(0);
            var bonus = SpellTooltip.bonus(modifier.value, modifier.operation);
            var bonus2 = SpellTooltip.bonus(modifier2.value, modifier2.operation);
            return args.description()
                    .replace("{bonus}", bonus)
                    .replace("{bonus2}", bonus2);
        };

        spell.release.animation = "more_rpg_classes:two_handed_roar";
        spell.release.sound = new Sound(BerserkerSounds.WILD_RAGE.id());
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch("minecraft:angry_villager",
                        ParticleBatch.Shape.PIPE, ParticleBatch.Origin.CENTER,
                        2, 0.01F, 0.1F)
                        .preSpawnTravel(7),
                new ParticleBatch("berserker_rpg:rage_particle",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        4, 0.01F, 0.2F)
                        .preSpawnTravel(7),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.STRIPE,
                                SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                        20, 0.2F, 0.25F)
                        .extent(-0.2F)
                        .color(Color.RAGE.toRGBA()),
        };

        spell.deliver.type = Spell.Delivery.Type.STASH_EFFECT;
        spell.deliver.stash_effect = new Spell.Delivery.StashEffect();
        spell.deliver.stash_effect.id = effect.id.toString();
        spell.deliver.stash_effect.consume = 0;
        var stashMeleeTrigger = new Spell.Trigger();
        stashMeleeTrigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        stashMeleeTrigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        spell.deliver.stash_effect.triggers = List.of(stashMeleeTrigger);

        var buff = createEffectImpact(effect.id, 10);
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        buff.action.status_effect.amplifier = 1;
        buff.action.status_effect.amplifier_cap = 9;
        buff.action.status_effect.refresh_duration = false;
        spell.impacts = List.of(buff);

        configureCooldown(spell, 15);
        spell.cost.exhaust = 0.2F;

        return new Entry(id, spell, title, description, mutator);
    }
    public static final Entry blood_reckoning = add(blood_reckoning());
    private static Entry blood_reckoning() {
        var id = Identifier.of(MOD_ID, "blood_reckoning");
        var title = "";
        var description = "";
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0;
        spell.tier = 2;
        spell.school = MoreSpellSchools.RAGE_MELEE;

        spell.release.animation = "more_rpg_classes:two_handed_roar";
        spell.release.sound = new Sound(BerserkerSounds.BLOOD_RECKONING.id());
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch("berserker_rpg:rage_particle",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        4, 0.01F, 0.2F)
                        .preSpawnTravel(7),
                new ParticleBatch(SpellEngineParticles.smoke_medium.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.2F, 0.4F)
                        .color(Color.RAGE.toRGBA())
                        .extent(3),
                new ParticleBatch(SpellEngineParticles.dripping_blood.id().toString(),
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.FEET,
                        15, 0.1F, 0.5F)
        };

        spell.target.type = Spell.Target.Type.CASTER;

        var custom = new Spell.Impact();
        custom.action = new Spell.Impact.Action();
        custom.action.custom = new Spell.Impact.Action.Custom();
        custom.action.type = Spell.Impact.Action.Type.CUSTOM;
        custom.action.custom.intent = SpellTarget.Intent.HARMFUL;
        custom.action.custom.handler = "berserker_rpg:blood_reckoning";

        spell.impacts = List.of(custom);
        spell.cost.exhaust = 0.3F;

        configureCooldown(spell, 20);
        return new Entry(id, spell, title, description, null);
    }
    public static final Entry bloody_strike = add(bloody_strike());
    private static Entry bloody_strike() {
        var id = Identifier.of(MOD_ID, "bloody_strike");
        var title = "";
        var description = "";
        var effect = BerserkerEffects.BLOOD_SACRIFICE;
        var debuffEffect = MRPGCEffects.BLEEDING;
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0.5F;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 3;
        spell.school = MoreSpellSchools.RAGE_MELEE;

        spell.release.animation = "berserker_rpg:berserker_axe_both";

        var buff = createEffectImpact(effect.id, 15);
        buff.action.apply_to_caster = true;
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        buff.action.status_effect.show_particles = false;
        buff.action.status_effect.amplifier_power_multiplier = 0.5F;
        buff.particles = new ParticleBatch[]{
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.SPARK,
                                SpellEngineParticles.MagicParticles.Motion.DECELERATE).id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        25, 0.1F, 0.1F)
                        .extent(0.2F)
                        .color(Color.RAGE.toRGBA())
        };

        var debuff = createEffectImpact(debuffEffect.id, 5);
        bleedingDeny(debuff);
        debuff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        debuff.action.status_effect.show_particles = false;
        debuff.action.status_effect.amplifier_power_multiplier = 0.2F;
        debuff.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.dripping_blood.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.05F, 0.3F),
                new ParticleBatch("more_rpg_classes:blood_drop",
                        ParticleBatch.Shape.PIPE, ParticleBatch.Origin.FEET,
                        10, 0.2F, 0.4F),
        };

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 0.5F;
        spell.target.area.angle_degrees = 45;

        var damage = damageImpact(0.65F, 1.0F);
        damage.sound = new Sound(BerserkerSounds.BLOODY_STRIKE.id());

        spell.impacts = List.of(debuff, damage, buff);
        
        configureCooldown(spell, 13);
        spell.cost.exhaust = 0.3F;
        return new Entry(id, spell, title, description, null);
    }
    public static final Entry outrage = add(outrage());
    private static Entry outrage() {
        var id = Identifier.of(MOD_ID, "outrage");
        var title = "Outrage";
        var description = "Clears harmful effects, increases attack speed by {bonus}. Deal extra {damage} damage per hit and stack grievous wounds.";
        var spell = SpellBuilder.createSpellActive();
        var effect = BerserkerEffects.OUTRAGE;
        spell.school = MoreSpellSchools.RAGE_MELEE;
        spell.range = 0;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 4;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().firstModifier();
            var bonus = SpellTooltip.bonus(modifier.value, modifier.operation);
            return args.description()
                    .replace("{bonus}", bonus);
        };

        spell.release.animation = "more_rpg_classes:two_handed_roar";
        spell.release.sound = new Sound(BerserkerSounds.OUTRAGE.id());
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch("crimson_spore",
                        ParticleBatch.Shape.PIPE, ParticleBatch.Origin.CENTER,
                        20, 0.01F, 0.1F),
                new ParticleBatch(
                        SpellEngineParticles.MagicParticles.get(
                                SpellEngineParticles.MagicParticles.Shape.STRIPE,
                                SpellEngineParticles.MagicParticles.Motion.FLOAT).id().toString(),
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.FEET,
                        30, 0.1F, 0.4F)
                        .extent(-0.2F)
                        .color(Color.RAGE.toRGBA()),
        };
        spell.release.particles_scaled_with_ranged = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.area_swirl.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1, 0.0F, 0.F)
                        .scale(0.25F)
                        .followEntity(true).color(Color.RAGE.toRGBA())
        };

        spell.deliver.type = Spell.Delivery.Type.STASH_EFFECT;
        var stash = new Spell.Delivery.StashEffect();
        stash.id = effect.id.toString();
        stash.duration = 12;
        stash.amplifier = 0;
        stash.consume = 0;
        var meleeTrigger = new Spell.Trigger();
        meleeTrigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        stash.triggers = List.of(meleeTrigger);
        spell.deliver.stash_effect = stash;

        var effectDebuff = MRPGCEffects.GRIEVOUS_WOUNDS;
        var debuff = createEffectImpact(effectDebuff.id, 10);
        debuff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        debuff.action.status_effect.amplifier = 1;
        debuff.action.status_effect.amplifier_cap = 7;
        debuff.action.status_effect.refresh_duration = true;
        debuff.particles = new ParticleBatch[]{
                new ParticleBatch(SpellEngineParticles.dripping_blood.id().toString(),
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        10, 0.05F, 0.3F)
        };

        var damage = damageImpact(0.2F, 0.1F);
        spell.impacts = List.of(damage, debuff);

        configureCooldown(spell, 35);
        spell.cost.exhaust = 0.3F;
        return new Entry(id, spell, title, description, mutator);
    }
}
