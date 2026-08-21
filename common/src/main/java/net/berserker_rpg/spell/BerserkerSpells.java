package net.berserker_rpg.spell;

import net.berserker_rpg.effect.BerserkerEffects;
import net.berserker_rpg.sounds.BerserkerSounds;
import net.berserker_rpg.spell.custom_spell_impacts.NortherhersGuillotineImpact;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.more_rpg_classes.entity.attribute.MRPGCEntityAttributes;
import net.spell_engine.api.effect.SpellEngineEffects;
import net.spell_power.api.SpellSchools;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.spell.Spell;
import net.more_rpg_classes.client.particle.MoreParticles;
import net.spell_engine.api.spell.fx.Fx;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.api.spell.fx.PlayerAnimation;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.api.spell.tooltip.TooltipTokens;
import net.spell_engine.api.util.TriState;
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
                        @Nullable Book book) {
        public Entry(Identifier id, Spell spell, String title, String description) {
            this(id, spell, title, description, null);
        }
        public Entry book(Book book) {
            return new Entry(id, spell, title, description, book);
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

        return new Entry(id, spell, title, description, null);
    }

    public static final Entry wild_rage = add(wild_rage());
    private static Entry wild_rage() {
        var id = Identifier.of(MOD_ID, "wild_rage");
        var title = "Wild Rage";
        var effect = BerserkerEffects.RAGE;
        // `Rage` carries two modifiers with *different* values, so each token names its attribute
        // explicitly — the effect's modifier map is unordered, the implicit "first modifier"
        // fallback would be unreliable here. Amplifier 0 = the per-stack value.
        var description = "Enter a wild rage, increases rage by "
                + TooltipTokens.effect(effect.id, 0, Identifier.of(MRPGCEntityAttributes.RAGE_MODIFIER.getIdAsString()))
                + " and attack speed by "
                + TooltipTokens.effect(effect.id, 0, Identifier.of(EntityAttributes.GENERIC_ATTACK_SPEED.getIdAsString()))
                + ", stacking up to {effect_amplifier_cap} times.";
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0;
        spell.tier = 2;
        spell.school = MoreSpellSchools.RAGE_MELEE;
        spell.group = FANATIC;

        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_roar");
        spell.release.sound = new Sound(BerserkerSounds.WILD_RAGE.id());
        spell.release.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.of("minecraft:angry_villager")
                        .batch(b -> b.shape(ParticleGroup.Shape.PIPE)
                                .count(2).speed(0.01F, 0.1F)
                                .preTravel(7)),
                ParticleGroupBuilder.of(MoreParticles.RAGE_PAR)
                        .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                                .count(4).speed(0.01F, 0.2F)
                                .preTravel(7)),
                ParticleGroupBuilder.magic(SpellEngineParticles.magic_stripe, ParticleGroup.Motion.FLOAT, Color.RAGE)
                        .batch(b -> b.shape(ParticleGroup.Shape.PIPE).widthFactor(2F)
                                .verticalOrigin(0.1F)
                                .count(20).speed(0.2F, 0.25F)
                                .extent(-0.2F)));

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

        return new Entry(id, spell, title, description, Book.BERSERKER);
    }
    public static final Entry bloody_strike = add(bloody_strike());
    private static Entry bloody_strike() {
        var id = Identifier.of(MOD_ID, "bloody_strike");
        var title = "Bloody Strike";
        var effect = BerserkerEffects.BLOOD_SACRIFICE;
        // `Blood Sacrifice` has a single modifier (max absorption, flat), so the sole-modifier
        // fallback is unambiguous here.
        var description = "Deals {damage} physical damage, costing {self_damage} of it back as self-damage in exchange for "
                + TooltipTokens.effect(effect.id)
                + " absorption. Skipped entirely below half a heart.";
        var debuffEffect = SpellEngineEffects.BLEED;
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0.5F;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 3;
        spell.school = MoreSpellSchools.RAGE_MELEE;
        spell.group = FANATIC;
        // `{self_damage}` is config-derived — see `registerTooltipTokens()`.

        spell.release.animation = PlayerAnimation.of("berserker_rpg:berserker_axe_both");

        var buff = SpellBuilder.Impacts.effectSet_ScaledAmplifier(
                effect.id.toString(), 15,1,0.5F);
        buff.action.apply_to_caster = true;
        buff.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.magic(SpellEngineParticles.magic_spark, ParticleGroup.Motion.DECELERATE, Color.RAGE)
                        .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                                .count(25).speed(0.1F, 0.1F)
                                .extent(0.2F)));

        var debuff = SpellBuilder.Impacts.effectAdd_ScaledAmplifier(
                debuffEffect.id.toString(), 5,1,0.2F);
        debuff.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.of(SpellEngineParticles.dripping_blood)
                        .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                                .count(10).speed(0.05F, 0.3F)),
                ParticleGroupBuilder.of(MoreParticles.BLOOD_DROP)
                        .batch(b -> b.shape(ParticleGroup.Shape.PIPE)
                                .verticalOrigin(0.1F)
                                .count(10).speed(0.2F, 0.4F)));

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 0.5F;
        spell.target.area.angle_degrees = 45;

        var damage = SpellBuilder.Impacts.damage(0.65F, 1.0F);
        damage.sound = new Sound(BerserkerSounds.BLOODY_STRIKE.id());

        spell.impacts = List.of(debuff, damage, buff);
        
        SpellBuilder.Cost.cooldown(spell, 13);
        spell.cost.exhaust = 0.3F;
        return new Entry(id, spell, title, description, Book.BERSERKER);
    }
    public static final Entry apprehend = add(apprehend());
    private static Entry apprehend() {
        var id = Identifier.of(MOD_ID, "apprehend");
        var title = "Apprehend";
        var effect = BerserkerEffects.APPREHEND;
        // Single modifier (armor, flat, stored negative) — `ABS` matches the "lowering ... by" phrasing.
        var description = "Strikes a wide area in front of you, pulling hit enemies towards you and lowering their armor by "
                + TooltipTokens.effect(effect.id, 0, null, TooltipTokens.Format.ABS)
                + ".";
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0.5F;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 2;
        spell.school = MoreSpellSchools.RAGE_MELEE;
        spell.group = BRUTE;

        spell.release.animation = PlayerAnimation.of("berserker_rpg:apprehend_cast");
        spell.release.sound = new Sound(BerserkerSounds.APPREHEND_CAST.id());

        spell.target.type = Spell.Target.Type.AREA;
        spell.target.area = new Spell.Target.Area();
        spell.target.area.vertical_range_multiplier = 0.5F;
        spell.target.area.angle_degrees = 120;

        var damage = SpellBuilder.Impacts.damage(0.5F, 0F);
        damage.sound = new Sound(BerserkerSounds.APPREHEND_IMPACT.id());

        var pull = SpellBuilder.Impacts.pull(1.0F);
        pull.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.of(SpellEngineParticles.smoke_medium)
                        .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                                .verticalOrigin(0.1F)
                                .count(25).speed(0.3F, 0.3F)
                                .preTravel(1)));

        var debuff = SpellBuilder.Impacts.effectAdd_ScaledAmplifier(effect.id.toString(), 6,1,0.1F);
        debuff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;

        spell.impacts = List.of(damage, pull, debuff);

        SpellBuilder.Cost.cooldown(spell, 16);
        spell.cost.exhaust = 0.3F;
        return new Entry(id, spell, title, description, Book.BERSERKER);
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
        // `{absorption_ratio}` / `{heal_ratio}` are config-derived — see `registerTooltipTokens()`.

        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_roar");
        spell.release.sound = new Sound(BerserkerSounds.BLOOD_RECKONING.id());
        spell.release.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.of(MoreParticles.RAGE_PAR)
                        .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                                .count(4).speed(0.01F, 0.2F)
                                .preTravel(7F)),
                ParticleGroupBuilder.of(SpellEngineParticles.smoke_medium)
                        .color(Color.RAGE)
                        .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                                .count(25).speed(0.2F, 0.4F)
                                .extent(3F)),
                ParticleGroupBuilder.of(SpellEngineParticles.dripping_blood)
                        .batch(b -> b.shape(ParticleGroup.Shape.CIRCLE)
                                .count(15).speed(0.1F, 0.5F)
                                .verticalOrigin(ParticleGroupBuilder.Batches.FEET)));

        spell.target.type = Spell.Target.Type.CASTER;

        var buff = SpellBuilder.Impacts.effectSet(effect.id.toString(), 8,0);
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.SET;

        spell.impacts = List.of(buff);
        spell.cost.exhaust = 0.3F;

        SpellBuilder.Cost.cooldown(spell, 24);
        return new Entry(id, spell, title, description, Book.BERSERKER);
    }
    public static final Entry outrage = add(outrage());
    private static Entry outrage() {
        var id = Identifier.of(MOD_ID, "outrage");
        var title = "Outrage";
        var effect = BerserkerEffects.OUTRAGE;
        // `Outrage` carries two modifiers (attack damage + attack speed), so the attribute is named
        // explicitly rather than relying on the unordered "first modifier" fallback.
        var description = "Clears harmful effects and increases attack damage by "
                + TooltipTokens.effect(effect.id, 0, Identifier.of(EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString()))
                + " for {effect_duration}. If Rage is active, extends the duration by an additional {rage_bonus_duration}.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.RAGE_MELEE;
        spell.range = 0;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 3;
        spell.group = BRUTE;
        // `{rage_bonus_duration}` is config-derived — see `registerTooltipTokens()`.

        spell.release.animation = PlayerAnimation.of("more_rpg_classes:two_handed_roar");
        spell.release.sound = new Sound(BerserkerSounds.OUTRAGE.id());
        spell.release.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.of("crimson_spore")
                        .batch(b -> b.shape(ParticleGroup.Shape.PIPE)
                                .count(20).speed(0.01F, 0.1F)),
                ParticleGroupBuilder.magic(SpellEngineParticles.magic_stripe, ParticleGroup.Motion.FLOAT, Color.RAGE)
                        .batch(b -> b.shape(ParticleGroup.Shape.PIPE).widthFactor(2F)
                                .count(30).speed(0.1F, 0.4F)
                                .verticalOrigin(ParticleGroupBuilder.Batches.FEET)
                                .extent(-0.2F)),
                ParticleGroupBuilder.of(SpellEngineParticles.area_swirl)
                        .color(Color.RAGE)
                        .attached()
                        .scaleWith(Fx.ScaleWith.RANGE)
                        .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                                .count(1).speed(0F, 0F)));

        spell.target.type = Spell.Target.Type.CASTER;

        var buff = SpellBuilder.Impacts.effectSet(effect.id.toString(), 10,0);

        var cleanse = SpellBuilder.Impacts.effectCleanse();

        spell.impacts = List.of(buff, cleanse);

        SpellBuilder.Cost.cooldown(spell, 30);
        spell.cost.exhaust = 0.3F;
        return new Entry(id, spell, title, description, Book.BERSERKER);
    }

    public static final Entry northerners_guillotine = add(northerners_guillotine());
    private static Entry northerners_guillotine() {
        var id = Identifier.of(MOD_ID, "northerners_guillotine");
        var title = "Northerners Guillotine";
        // `BASE_DAMAGE_MULTIPLIER` is a compile-time constant, so its percentage is baked straight into
        // the description. Baked literals must escape `%` as `%%` (the lang value goes through
        // `String.format`) — hence `bakedPercent`, not `percent`.
        var description = "Leaps to the target, striking a lethal blow. Deals "
                + TooltipTokens.bakedPercent(NortherhersGuillotineImpact.BASE_DAMAGE_MULTIPLIER - 1F)
                + " bonus damage, increasing by {damage_per_amplifier} for every level of a harmful effect on the target. " +
                "Always critically strikes targets at or below {execute_threshold} of their max health.";
        var spell = SpellBuilder.createSpellActive();
        spell.school = MoreSpellSchools.RAGE_MELEE;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.range = 0.5F;
        spell.tier = 4;
        spell.group = BRUTE;
        // `{damage_per_amplifier}` / `{execute_threshold}` are config-derived — see `registerTooltipTokens()`.

        spell.target.type = Spell.Target.Type.AIM;
        spell.target.aim = new Spell.Target.Aim();
        spell.target.aim.sticky = false;
        spell.target.aim.required = true;

        spell.release.animation = PlayerAnimation.of("berserker_rpg:northerners_guillotine");
        spell.release.sound = new Sound(BerserkerSounds.NORTHERNERS_GUILLOTINE_CAST.id());

        var custom = new Spell.Impact();
        custom.action = new Spell.Impact.Action();
        custom.action.custom = new Spell.Impact.Action.Custom();
        custom.action.type = Spell.Impact.Action.Type.CUSTOM;
        custom.action.custom.intent = SpellTarget.Intent.HARMFUL;
        custom.action.custom.handler = "berserker_rpg:northerners_guillotine";
        custom.sound = new Sound(BerserkerSounds.NORTHERNERS_GUILLOTINE_IMPACT.id());
        custom.visuals = Fx.Visuals.of(
                ParticleGroupBuilder.of(SpellEngineParticles.smoke_medium)
                        .color(Color.RAGE)
                        .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                                .count(20).speed(0.3F, 0.3F)
                                .verticalOrigin(ParticleGroupBuilder.Batches.FEET)
                                .preTravel(1F)),
                ParticleGroupBuilder.of(SpellEngineParticles.smoke_large)
                        .color(Color.RAGE)
                        .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                                .count(20).speed(0.45F, 0.45F)
                                .verticalOrigin(ParticleGroupBuilder.Batches.FEET)
                                .preTravel(3F)),
                ParticleGroupBuilder.of(SpellEngineParticles.smoke_medium)
                        .color(Color.RAGE)
                        .batch(b -> b.shape(ParticleGroup.Shape.SPHERE)
                                .count(20).speed(0.6F, 0.6F)
                                .verticalOrigin(ParticleGroupBuilder.Batches.FEET)
                                .preTravel(5F)));



        spell.impacts = List.of(custom);

        SpellBuilder.Cost.cooldown(spell, 35);
        spell.cost.exhaust = 1.0F;
        spell.cost.durability = 1;
        return new Entry(id, spell, title, description, Book.BERSERKER);
    }

    /// Registers the description values that no declarative `{token}` can express: numbers read from
    /// the user-editable tweaks config. They can't be baked into the lang value (that would freeze the
    /// datagen-time default) and they aren't status-effect modifiers, so they resolve at render time.
    ///
    /// `TooltipTokens.Custom` references only shared types, so this is safe to call from either side.
    /// The lambdas read `tweaksConfig` lazily, so registration may run before the config is loaded.
    ///
    /// Percentages injected here land *after* translation, so their `%` must NOT be doubled — unlike a
    /// percentage baked into a description literal, which uses `TooltipTokens.bakedPercent`.
    public static void registerTooltipTokens() {
        TooltipTokens.registerCustom(bloody_strike.id(), args -> args.description()
                .replace("{self_damage}", TooltipTokens.percent(tweaksConfig.value.bloody_strike_self_damage)));

        TooltipTokens.registerCustom(blood_reckoning.id(), args -> args.description()
                .replace("{absorption_ratio}", TooltipTokens.percent(tweaksConfig.value.blood_reckoning_missing_health_to_absorption))
                .replace("{heal_ratio}", TooltipTokens.percent(tweaksConfig.value.blood_reckoning_absorption_to_heal_on_expire)));

        TooltipTokens.registerCustom(outrage.id(), args -> args.description()
                .replace("{rage_bonus_duration}", String.format("%.1fs", tweaksConfig.value.outrage_rage_bonus_duration)));

        TooltipTokens.registerCustom(northerners_guillotine.id(), args -> args.description()
                .replace("{damage_per_amplifier}", String.format("%.1f%%", tweaksConfig.value.northerners_guillotine_damage_per_amplifier * 100))
                .replace("{execute_threshold}", TooltipTokens.percent(tweaksConfig.value.northerners_guillotine_execute_health_threshold)));
    }
}
