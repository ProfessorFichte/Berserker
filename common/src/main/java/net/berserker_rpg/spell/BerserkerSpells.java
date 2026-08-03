package net.berserker_rpg.spell;

import net.berserker_rpg.effect.BerserkerEffects;
import net.berserker_rpg.sounds.BerserkerSounds;
import net.berserker_rpg.spell.custom_spell_impacts.NortherhersGuillotineImpact;
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
import static net.berserker_rpg.BerserkerClassMod.tweaksConfig;

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

    public static final String BRUTE =  "brute";
    public static final String FANATIC = "fanatic";
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
        spell.group = FANATIC;
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

        var buff = SpellBuilder.Impacts.effectAdd(effect.id.toString(),10,1,9);
        buff.action.status_effect.refresh_duration = false;
        spell.impacts = List.of(buff);

        SpellBuilder.Cost.cooldown(spell, 15);
        spell.cost.exhaust = 0.2F;

        return new Entry(id, spell, title, description, mutator,Book.BERSERKER);
    }
    public static final Entry bloody_strike = add(bloody_strike());
    private static Entry bloody_strike() {
        var id = Identifier.of(MOD_ID, "bloody_strike");
        var title = "Bloody Strike";
        var description = "Deals {damage} physical damage, costing {self_damage} of it back as self-damage in exchange for {absorption} absorption. Skipped entirely below half a heart.";
        var effect = BerserkerEffects.BLOOD_SACRIFICE;
        var debuffEffect = SpellEngineEffects.BLEED;
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0.5F;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 3;
        spell.school = MoreSpellSchools.RAGE_MELEE;
        spell.group = FANATIC;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().attributes().get(0);
            var absorption = SpellTooltip.bonus(modifier.value, modifier.operation);
            var self_damage = SpellTooltip.percent(tweaksConfig.value.bloody_strike_self_damage);
            return args.description()
                    .replace("{self_damage}", self_damage)
                    .replace("{absorption}", absorption);
        };

        spell.release.animation = PlayerAnimation.of("berserker_rpg:berserker_axe_both");

        var buff = SpellBuilder.Impacts.effectSet_ScaledAmplifier(
                effect.id.toString(), 15,1,0.5F);
        buff.action.apply_to_caster = true;
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

        var debuff = SpellBuilder.Impacts.effectAdd_ScaledAmplifier(
                debuffEffect.id.toString(), 5,1,0.2F);
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

        var damage = SpellBuilder.Impacts.damage(0.65F, 1.0F);
        damage.sound = new Sound(BerserkerSounds.BLOODY_STRIKE.id());

        spell.impacts = List.of(debuff, damage, buff);
        
        SpellBuilder.Cost.cooldown(spell, 13);
        spell.cost.exhaust = 0.3F;
        return new Entry(id, spell, title, description, mutator,Book.BERSERKER);
    }
    public static final Entry apprehend = add(apprehend());
    private static Entry apprehend() {
        var id = Identifier.of(MOD_ID, "apprehend");
        var title = "Apprehend";
        var description = "Strikes a wide area in front of you, pulling hit enemies towards you and lowering their armor by {armor}.";
        var effect = BerserkerEffects.APPREHEND;
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0.5F;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 2;
        spell.school = MoreSpellSchools.RAGE_MELEE;
        spell.group = BRUTE;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var modifier = effect.config().attributes().get(0);
            var armor = SpellTooltip.bonus(Math.abs(modifier.value), modifier.operation);
            return args.description()
                    .replace("{armor}", armor);
        };

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

        var damage = SpellBuilder.Impacts.damage(0.5F, 0F);
        damage.sound = new Sound(BerserkerSounds.BLOODY_STRIKE.id());

        var pull = SpellBuilder.Impacts.pull(0.8F);

        var debuff = SpellBuilder.Impacts.effectAdd_ScaledAmplifier(effect.id.toString(), 6,0,0.15F);
        debuff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;

        spell.impacts = List.of(damage, pull, debuff);

        SpellBuilder.Cost.cooldown(spell, 16);
        spell.cost.exhaust = 0.3F;
        return new Entry(id, spell, title, description, mutator, Book.BERSERKER);
    }
    public static final Entry blood_reckoning = add(blood_reckoning());
    private static Entry blood_reckoning() {
        var id = Identifier.of(MOD_ID, "blood_reckoning");
        var title = "Blood Reckoning";
        var description = "Grants absorption equal to {absorption_ratio} of your missing health. When the effect ends, converts {heal_ratio} of your remaining absorption into a heal.";
        var effect = BerserkerEffects.BLOOD_RECKONING;
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0;
        spell.tier = 4;
        spell.group = FANATIC;
        spell.school = MoreSpellSchools.RAGE_MELEE;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var absorptionRatio = SpellTooltip.percent(tweaksConfig.value.blood_reckoning_missing_health_to_absorption);
            var healRatio = SpellTooltip.percent(tweaksConfig.value.blood_reckoning_absorption_to_heal_on_expire);
            return args.description()
                    .replace("{absorption_ratio}", absorptionRatio)
                    .replace("{heal_ratio}", healRatio);
        };

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

        var buff = SpellBuilder.Impacts.effectSet(effect.id.toString(), 12,0);
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;

        spell.impacts = List.of(buff);
        spell.cost.exhaust = 0.3F;

        SpellBuilder.Cost.cooldown(spell, 20);
        return new Entry(id, spell, title, description, mutator,Book.BERSERKER);
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
        spell.group = BRUTE;
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

        var buff = SpellBuilder.Impacts.effectSet(effect.id.toString(), 10,0);

        var cleanse = SpellBuilder.Impacts.effectCleanse();

        spell.impacts = List.of(buff, cleanse);

        SpellBuilder.Cost.cooldown(spell, 35);
        spell.cost.exhaust = 0.3F;
        return new Entry(id, spell, title, description, mutator,Book.BERSERKER);
    }

    public static final Entry northerners_guillotine = add(northerners_guillotine());
    private static Entry northerners_guillotine() {
        var id = Identifier.of(MOD_ID, "northerners_guillotine");
        var title = "Northerners Guillotine";
        var description = "Leaps to the target, striking a lethal blow. Deals {base_damage} bonus damage, increasing by {damage_per_amplifier} for every level of a harmful effect on the target.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.RAGE_MELEE;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.range = 0.5F;
        spell.tier = 4;
        spell.group = BRUTE;
        SpellTooltip.DescriptionMutator mutator = (args) -> {
            var baseDamage = SpellTooltip.percent(NortherhersGuillotineImpact.BASE_DAMAGE_MULTIPLIER - 1F);
            var perAmplifier = String.format("%.1f%%", tweaksConfig.value.northerners_guillotine_damage_per_amplifier * 100);
            return args.description()
                    .replace("{base_damage}", baseDamage)
                    .replace("{damage_per_amplifier}", perAmplifier);
        };

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.sticky = false;
        spell.target.aim.required = true;

        spell.release.animation = PlayerAnimation.of("berserker_rpg:decapitate_release");
        spell.release.sound = new Sound(BerserkerSounds.DECAPITATE_RELEASE.id());

        var custom = new Spell.Impact();
        custom.action = new Spell.Impact.Action();
        custom.action.custom = new Spell.Impact.Action.Custom();
        custom.action.type = Spell.Impact.Action.Type.CUSTOM;
        custom.action.custom.intent = SpellTarget.Intent.HARMFUL;
        custom.action.custom.handler = "berserker_rpg:northerners_guillotine";
        custom.sound = new Sound(BerserkerSounds.DECAPITATE_IMPACT.id());

        spell.impacts = List.of(custom);

        SpellBuilder.Cost.cooldown(spell, 30);
        spell.cost.exhaust = 1.0F;
        spell.cost.durability = 1;
        return new Entry(id, spell, title, description, mutator, Book.BERSERKER);
    }
}
