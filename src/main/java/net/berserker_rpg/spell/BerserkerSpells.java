package net.berserker_rpg.spell;

import net.berserker_rpg.effect.BerserkerEffects;
import net.berserker_rpg.sounds.BerserkerSounds;
import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SpellBuilder;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.api.spell.fx.Sound;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.client.util.Color;
import net.spell_engine.fx.SpellEngineParticles;
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

    ///ACTIVE SPELLS
    public static final Entry wild_rage = add(wild_rage());
    private static Entry wild_rage() {
        var id = Identifier.of(MOD_ID, "wild_rage");
        var title = "Wild Rage";
        var description = "";
        var effect = BerserkerEffects.RAGE;
        var spell = SpellBuilder.createSpellActive();
        spell.range = 0;
        spell.tier = 1;
        spell.school = BerserkerSpellSchool.BERSERKER_MELEE;

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

        return new Entry(id, spell, title, description, null);
    }

    public static final Entry outrage = add(outrage());
    private static Entry outrage() {
        var id = Identifier.of(MOD_ID, "outrage");
        var title = "Outrage";
        var description = "";
        var spell = SpellBuilder.createSpellActive();
        spell.school = BerserkerSpellSchool.BERSERKER_MELEE;
        spell.range = 0;
        spell.range_mechanic = Spell.RangeMechanic.MELEE;
        spell.tier = 4;

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
        stash.id = BerserkerEffects.OUTRAGE.id.toString();
        stash.duration = 12;
        stash.amplifier = 0;
        stash.consume = 0;
        var meleeTrigger = new Spell.Trigger();
        meleeTrigger.type = Spell.Trigger.Type.MELEE_IMPACT;
        meleeTrigger.target_override = Spell.Trigger.TargetSelector.CASTER;
        stash.triggers = List.of(meleeTrigger);
        spell.deliver.stash_effect = stash;

        var effect = BerserkerEffects.RAGE;
        var buff = createEffectImpact(effect.id, 10);
        buff.action.status_effect.apply_mode = Spell.Impact.Action.StatusEffect.ApplyMode.ADD;
        buff.action.status_effect.amplifier = 1;
        buff.action.status_effect.amplifier_cap = 9;
        buff.action.status_effect.refresh_duration = true;

        var damage = damageImpact(0.75F, 0.1F);
        spell.impacts = List.of(damage, buff);

        spell.area_impact = new Spell.AreaImpact();
        spell.area_impact.radius = 2.5F;
        spell.area_impact.area.distance_dropoff = Spell.Target.Area.DropoffCurve.SQUARED;

        configureCooldown(spell, 35);
        return new Entry(id, spell, title, description, null);
    }
}
