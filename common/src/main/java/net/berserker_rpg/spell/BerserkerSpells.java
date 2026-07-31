package net.berserker_rpg.spell;

import net.berserker_rpg.effect.BerserkerEffects;
import net.berserker_rpg.sounds.BerserkerSounds;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.effect.SpellEngineEffects;
import net.spell_power.api.SpellSchools;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.PlayerAnimation;
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
    public enum Book { BERSERKER}
    public record Entry(Identifier id, Spell spell, String title, String description,
                        @Nullable SpellTooltip.DescriptionMutator mutator,
                        @Nullable Book book) {
        public Entry(Identifier id, Spell spell, String title, String description) {
            this(id, spell, title, description, null, null);
        }
        public Entry mutator(SpellTooltip.DescriptionMutator mutator) {
            return new Entry(id, spell, title, description, mutator, book);
        }
        public Entry book(Book book) {
            return new Entry(id, spell, title, description, mutator, book);
        }
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

        return new Entry(id, spell, title, description, null, null);
    }
    ///PASSIVES
    ///ACTIVE SPELLS
    public static final Entry wild_rage = add(wild_rage());
    private static Entry wild_rage() {
        var id = Identifier.of(MOD_ID, "wild_rage");
        var title = "Wild Rage";
        var description = "Enter a wild rage, increases rage by {bonus} and attack speed by {bonus2}, stacking up to {effect_amplifier_cap} times.";
        var effect = BerserkerEffects.RAGE;
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0;
        spell.tier = 2;
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

        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_roar");
        spell.release.sound = new Sound(BerserkerSounds.WILD_RAGE.id());
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch("minecraft:angry_villager",
                        ParticleBatch.Shape.PIPE, ParticleBatch.Origin.CENTER,
                        2, 0.01F, 0.1F)
                        .preSpawnTravel(7),
                new ParticleBatch("more_rpg_classes:rage_particle",
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

        return new Entry(id, spell, title, description, mutator,Book.BERSERKER);
    }
    public static final Entry bloody_strike = add(bloody_strike());
    private static Entry bloody_strike() {
        var id = Identifier.of(MOD_ID, "bloody_strike");
        var title = "Bloody Strike";
        var description = "Deals {damage} physical-damage in trade of self damage, gives absorption for dealt damage. Below half a heart, the trade is skipped entirely.";
        var effect = BerserkerEffects.BLOOD_SACRIFICE;
        var debuffEffect = SpellEngineEffects.BLEED;
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0.5F;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 3;
        spell.school = MoreSpellSchools.RAGE_MELEE;

        spell.release.animation = PlayerAnimation.of("berserker_rpg:berserker_axe_both");

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
        debuff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        debuff.action.status_effect.show_particles = false;
        debuff.action.status_effect.amplifier = 1;
        debuff.action.status_effect.amplifier_cap = 3;
        debuff.action.status_effect.amplifier_cap_power_multiplier = 0.2F;
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
        return new Entry(id, spell, title, description, null,Book.BERSERKER);
    }
    public static final Entry apprehend = add(apprehend());
    private static Entry apprehend() {
        var id = Identifier.of(MOD_ID, "apprehend");
        var title = "Apprehend";
        var description = "Strikes a wide area in front of you, pulling hit enemies towards you and lowering their armor.";
        var effect = BerserkerEffects.APPREHEND;
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0.5F;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 2;
        spell.school = MoreSpellSchools.RAGE_MELEE;

        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_roar");
        spell.release.sound = new Sound(BerserkerSounds.OUTRAGE.id());
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch("crimson_spore",
                        ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.CENTER,
                        20, 0.2F, 0.4F)
        };

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 0.5F;
        spell.target.area.angle_degrees = 120;

        var damage = damageImpact(0.5F, 0F);
        damage.sound = new Sound(BerserkerSounds.BLOODY_STRIKE.id());

        var pull = SpellBuilder.Impacts.pull(0.8F);

        var debuff = createEffectImpact(effect.id, 6);
        debuff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;

        spell.impacts = List.of(damage, pull, debuff);

        configureCooldown(spell, 16);
        spell.cost.exhaust = 0.3F;
        return new Entry(id, spell, title, description, null, Book.BERSERKER);
    }
    public static final Entry blood_reckoning = add(blood_reckoning());
    private static Entry blood_reckoning() {
        var id = Identifier.of(MOD_ID, "blood_reckoning");
        var title = "Blood Reckoning";
        var description = "Grants absorption for missing health. Heals for a portion of the remaining absorption when the effect runs out.";
        var effect = BerserkerEffects.BLOOD_RECKONING;
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0;
        spell.tier = 4;
        spell.school = MoreSpellSchools.RAGE_MELEE;

        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_roar");
        spell.release.sound = new Sound(BerserkerSounds.BLOOD_RECKONING.id());
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch("more_rpg_classes:rage_particle",
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

        var buff = createEffectImpact(effect.id, 20);
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;

        spell.impacts = List.of(buff);
        spell.cost.exhaust = 0.3F;

        configureCooldown(spell, 20);
        return new Entry(id, spell, title, description, null,Book.BERSERKER);
    }
    public static final Entry outrage = add(outrage());
    private static Entry outrage() {
        var id = Identifier.of(MOD_ID, "outrage");
        var title = "Outrage";
        var description = "Clears harmful effects and increases attack damage by {bonus} for {effect_duration}.";
        var spell = SpellBuilder.createSpellActive();
        var effect = BerserkerEffects.OUTRAGE;
        spell.school = MoreSpellSchools.RAGE_MELEE;
        spell.range = 0;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 3;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().firstModifier();
            var bonus = SpellTooltip.bonus(modifier.value, modifier.operation);
            return args.description()
                    .replace("{bonus}", bonus);
        };

        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_roar");
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

        spell.target.type = Spell.Target.Type.CASTER;

        var buff = createEffectImpact(effect.id, 10);
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;
        buff.action.status_effect.amplifier = 0;

        spell.impacts = List.of(buff);

        configureCooldown(spell, 35);
        spell.cost.exhaust = 0.3F;
        return new Entry(id, spell, title, description, mutator,Book.BERSERKER);
    }

    public static final Entry northerners_guillotine = add(northerners_guillotine());
    private static Entry northerners_guillotine() {
        var id = Identifier.of(MOD_ID, "northerners_guillotine");
        var title = "Northerners Guillotine";
        var description = "Leaps to the target, striking a lethal blow. Deals more damage for every negative status effect on the target.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.RAGE_MELEE;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.range = 0.5F;
        spell.tier = 4;

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.sticky = false;
        spell.target.aim.required = true;

        spell.release.animation = PlayerAnimation.of("berserker_rpg:decapitate_charge");
        spell.release.sound = new Sound(BerserkerSounds.DECAPITATE_RELEASE.id());

        var custom = new Spell.Impact();
        custom.action = new Spell.Impact.Action();
        custom.action.custom = new Spell.Impact.Action.Custom();
        custom.action.type = Spell.Impact.Action.Type.CUSTOM;
        custom.action.custom.intent = SpellTarget.Intent.HARMFUL;
        custom.action.custom.handler = "berserker_rpg:northerners_guillotine";
        custom.sound = new Sound(BerserkerSounds.DECAPITATE_IMPACT.id());

        spell.impacts = List.of(custom);

        configureCooldown(spell, 30);
        spell.cost.exhaust = 1.0F;
        spell.cost.durability = 1;
        return new Entry(id, spell, title, description, null, Book.BERSERKER);
    }
    public static final Entry rumbling_swing = add(rumbling_swing());
    private static Entry rumbling_swing() {
        var id = Identifier.of(MOD_ID, "rumbling_swing");
        var title = "Rumbling Swing";
        var description = "Jumps lightning fast, dealing {damage_0} physical- and {damage_1} lightning-damage";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.LIGHTNING;
        spell.range = 10;
        spell.tier = 5;

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.sticky = false;
        spell.target.aim.required = true;

        spell.release.animation = PlayerAnimation.of("berserker_rpg:rumbling_swing");
        spell.release.sound = new Sound("entity.player.attack.sweep");
        spell.release.particles = new ParticleBatch[]{
                new ParticleBatch("electric_spark",
                        ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER,
                        15, 0.5F, 3.0F)
        };

        var teleport = new Spell.Impact();
        teleport.action = new Spell.Impact.Action();
        teleport.action.type = Spell.Impact.Action.Type.TELEPORT;
        teleport.action.teleport = new Spell.Impact.Action.Teleport();
        teleport.action.teleport.mode = Spell.Impact.Action.Teleport.Mode.BEHIND_TARGET;
        teleport.action.teleport.intent = SpellTarget.Intent.HARMFUL;
        teleport.action.teleport.behind_target = new Spell.Impact.Action.Teleport.BehindTarget();
        teleport.action.teleport.behind_target.distance = 1.5F;
        teleport.action.teleport.depart_particles = new ParticleBatch[]{
                new ParticleBatch("cloud",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET,
                        20, 0.05F, 0.1F)
                        .invert()
                        .preSpawnTravel(15)
        };
        teleport.action.teleport.arrive_particles = new ParticleBatch[]{
                new ParticleBatch("poof",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.FEET,
                        10, 0.05F, 0.1F)
                        .preSpawnTravel(2)
        };

        var damage = damageImpact(0.8F, 1.0F);
        damage.attribute = "minecraft:generic.attack_damage";
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("berserker_rpg:small_thunder",
                        ParticleBatch.Shape.PIPE, ParticleBatch.Origin.CENTER,
                        1, 0.1F, 0.15F)
        };
        damage.sound = new Sound("entity.lightning_bolt.impact");

        var lightningDamage = new Spell.Impact();
        lightningDamage.school = SpellSchools.LIGHTNING;
        lightningDamage.action = new Spell.Impact.Action();
        lightningDamage.action.type = Spell.Impact.Action.Type.DAMAGE;
        lightningDamage.action.damage = new Spell.Impact.Action.Damage();
        lightningDamage.action.damage.spell_power_coefficient = 0.5F;

        spell.impacts = List.of(teleport, damage, lightningDamage);

        configureCooldown(spell, 25);
        spell.cost.exhaust = 1.0F;
        spell.cost.durability = 1;
        return new Entry(id, spell, title, description, null,null);
    }
    public static final Entry nordic_storm = add(nordic_storm());
    private static Entry nordic_storm() {
        var id = Identifier.of(MOD_ID, "nordic_storm");
        var title = "Nordic Storm";
        var description = "Spinning attack, that deals {damage_0} physical- and {damage_1} frost-damage on targets per second.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = SpellSchools.FROST;
        spell.range = 0;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 5;

        spell.active.cast = new Spell.Active.Cast();
        spell.active.cast.duration = 0.5F;
        spell.active.cast.movement_speed = 1.2F;
        spell.active.cast.animation = PlayerAnimation.of("berserker_rpg:nordic_storm");
        spell.active.cast.sound = new Sound(Identifier.of("spell_engine:generic_frost_impact"), 0);
        spell.active.cast.start_sound = new Sound("entity.player.breath");
        spell.active.cast.type = Spell.Active.Cast.Type.CHANNEL;
        spell.active.cast.channel = new Spell.Active.Cast.Channel();
        spell.active.cast.channel.ticks = 5;
        spell.active.cast.particles = new ParticleBatch[]{
                new ParticleBatch("sweep_attack",
                        ParticleBatch.Shape.PIPE, ParticleBatch.Origin.CENTER,
                        1, 0.3F, 3.0F),
                new ParticleBatch("spell_engine:frost_shard",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.CENTER,
                        1, 0.3F, 3.0F)
        };

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;
        spell.target.area.angle_degrees = 360;
        spell.target.area.vertical_range_multiplier = 0.5F;

        var damage = damageImpact(0.8F, 0F);
        damage.attribute = "minecraft:generic.attack_damage";
        var freezeHurts = new Spell.Impact.TargetModifier();
        var freezeHurtsCond = new Spell.TargetCondition();
        freezeHurtsCond.entity_type = "#minecraft:freeze_hurts_extra_types";
        freezeHurts.conditions = List.of(freezeHurtsCond);
        freezeHurts.modifier = new Spell.Impact.Modifier();
        freezeHurts.modifier.power_multiplier = 0.3F;
        var freezeImmune = new Spell.Impact.TargetModifier();
        var freezeImmuneCond = new Spell.TargetCondition();
        freezeImmuneCond.entity_type = "#minecraft:freeze_immune_entity_types";
        freezeImmune.conditions = List.of(freezeImmuneCond);
        freezeImmune.modifier = new Spell.Impact.Modifier();
        freezeImmune.modifier.power_multiplier = -0.3F;
        damage.target_modifiers = List.of(freezeHurts, freezeImmune);
        damage.particles = new ParticleBatch[]{
                new ParticleBatch("spell_engine:frost_hit",
                        ParticleBatch.Shape.SPHERE, ParticleBatch.Origin.LAUNCH_POINT,
                        1, 0.5F, 3.0F)
        };
        damage.sound = new Sound(Identifier.of("item.axe.strip"), 2);

        var frosted = createEffectImpact(Identifier.of("more_rpg_classes", "frosted"), 8);
        frosted.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        frosted.action.status_effect.amplifier = 1;
        frosted.action.status_effect.amplifier_cap = 4;
        frosted.action.status_effect.show_particles = false;
        var frostedDeny = new Spell.Impact.TargetModifier();
        var frostedDenyCond = new Spell.TargetCondition();
        frostedDenyCond.entity_type = "#minecraft:freeze_immune_entity_types";
        frostedDeny.conditions = List.of(frostedDenyCond);
        frostedDeny.execute = TriState.DENY;
        frosted.target_modifiers = List.of(frostedDeny);

        spell.impacts = List.of(damage, frosted);

        configureCooldown(spell, 10);
        spell.cost.cooldown.proportional = true;
        spell.cost.exhaust = 1.0F;
        spell.cost.durability = 5;
        return new Entry(id, spell, title, description, null, null);
    }
    /// WEAPON SKILLS
    public static Entry decapitate = add(decapitate());
    private static Entry decapitate() {
        var id = Identifier.of(MOD_ID, "decapitate");
        var title = "Decapitate";
        var description = "Delivers a heavy blow with forward momentum that disables shield and item usage of target.";
        var spell = SpellBuilder.createSpellActive();
        spell.tier = 1;
        spell.school = MoreSpellSchools.RAGE_MELEE;
        spell.range = 0.0F;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;

        SpellBuilder.Casting.cast(spell, 0.75F);
        spell.active.cast.animation = PlayerAnimation.of("berserker_rpg:decapitate_charge");
        spell.active.cast.animation.speed = 1.5F;
        spell.active.cast.animation_pitch = false;
        spell.release.sound = new Sound(BerserkerSounds.DECAPITATE_RELEASE.id());

        SpellBuilder.Target.none(spell);

        var cut_1 = new Spell.Delivery.Melee.Attack();
        cut_1.attack_speed_multiplier = 1.25F;
        cut_1.delay = 0.1F;
        cut_1.hitbox = new Spell.Delivery.Melee.HitBox();
        cut_1.hitbox.arc = 180;
        cut_1.hitbox.height = 0.7F;
        cut_1.hitbox.roll = 15F;
        cut_1.damage_bonus = 0.25F;
        cut_1.forward_momentum = 1.5F;
        cut_1.swing_sound = new Sound(BerserkerSounds.DECAPITATE_SWING.id());
        cut_1.impact_sound = new Sound(BerserkerSounds.DECAPITATE_IMPACT.id());
        cut_1.impact_sound_cap = 1;
        cut_1.animation = PlayerAnimation.of("berserker_rpg:decapitate_release");

        SpellBuilder.Deliver.melee(spell, List.of(cut_1));

        var disrupt = SpellBuilder.Impacts.disrupt(true, 2F);
        spell.impacts = List.of(disrupt);

        SpellBuilder.Cost.cooldownGroupWeapon(spell);
        SpellBuilder.Cost.cooldown(spell, 15);
        spell.cost.cooldown.attempt_duration = 1F;

        return new Entry(id, spell, title, description, null, null);
    }
}
