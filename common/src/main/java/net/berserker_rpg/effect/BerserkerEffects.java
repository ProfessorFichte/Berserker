package net.berserker_rpg.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.entity.attribute.MRPGCEntityAttributes;
import net.spell_engine.rpg_series.config.AttributeModifier;
import net.spell_engine.rpg_series.config.ConfigFile;
import net.spell_engine.rpg_series.config.EffectConfig;
import net.spell_engine.api.effect.*;

import java.util.ArrayList;
import java.util.List;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;
import static net.berserker_rpg.BerserkerClassMod.tweaksConfig;

public class BerserkerEffects {
    /// 1.20.1 `EntityAttribute` carries no id accessor (`getIdAsString()` is 1.21) - look it up instead.
    public static String attributeId(EntityAttribute attribute) {
        return Registries.ATTRIBUTE.getId(attribute).toString();
    }

    /// The absorption granted by `Blood Sacrifice`, as the tooltip states it.
    ///
    /// On 1.21.1 this number came from the effect's `minecraft:generic.max_absorption` modifier, which the
    /// spell tooltip's `{effect|...}` token read back. 1.20.1 has no max-absorption attribute at all (it is a
    /// 1.20.5 addition) and absorption is uncapped, so the modifier is gone and the displayed value is this
    /// constant instead of a config-driven one.
    public static final float BLOOD_SACRIFICE_ABSORPTION = 4F;

    public static final List<Effects.Entry> entries = new ArrayList<>();
    private static Effects.Entry add(Effects.Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static final Effects.Entry RAGE = add(new Effects.Entry(
            new Identifier(MOD_ID, "rage"),
            "Rage",
            "Increases Rage & Attack Speed. You deal more damage, the less health you have.",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xf70000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(MRPGCEntityAttributes.RAGE_MODIFIER),
                            0.1F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    ),
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_ATTACK_SPEED),
                            0.02F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    )
            ))
    ));
    public static final Effects.Entry BLOOD_SACRIFICE = add(new Effects.Entry(
            new Identifier(MOD_ID, "blood_sacrifice"),
            "Blood Sacrifice",
            "Converts your health to absorption hearts.",
            new BloodSacrificeEffect(StatusEffectCategory.BENEFICIAL, 0xf70000),
            // No `minecraft:generic.max_absorption` attribute before 1.20.5 - absorption is uncapped here,
            // so the 1.21.1 `+4` cap modifier has no 1.20.1 counterpart and is dropped. The effect itself
            // sets the absorption amount directly (see BloodSacrificeEffect#onApplied).
            new EffectConfig(List.of())
    ));
    public static final Effects.Entry BLOOD_RECKONING = add(new Effects.Entry(
            new Identifier(MOD_ID, "blood_reckoning"),
            "Blood Reckoning",
            "Grants absorption for missing health. Heals for a portion of your remaining absorption when the effect runs out.",
            new BloodReckoningEffect(StatusEffectCategory.BENEFICIAL, 0xf70000),
            // Same as Blood Sacrifice: the 1.21.1 `+20` max-absorption cap has no 1.20.1 attribute to ride on.
            new EffectConfig(List.of())
    ));
    public static final Effects.Entry OUTRAGE = add(new Effects.Entry(
            new Identifier(MOD_ID, "outrage"),
            "Outrage",
            "Increases attack damage and attack speed.",
            new OutrageEffect(StatusEffectCategory.BENEFICIAL, 0xf70000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_ATTACK_DAMAGE),
                            0.2F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    ),
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_ATTACK_SPEED),
                            0.2F,
                            EntityAttributeModifier.Operation.MULTIPLY_BASE
                    )
            ))
    ));
    public static final Effects.Entry APPREHEND = add(new Effects.Entry(
            new Identifier(MOD_ID, "apprehend"),
            "Apprehend",
            "Lowers armor.",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x8a1f1f),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            attributeId(EntityAttributes.GENERIC_ARMOR),
                            -4.0F,
                            EntityAttributeModifier.Operation.ADDITION
                    )
            ))
    ));

    public static void register(ConfigFile.Effects config) {
        OnRemoval.configure(BLOOD_RECKONING.effect, (context) -> {
            var entity = context.entity();
            float heal = entity.getAbsorptionAmount() * tweaksConfig.value.blood_reckoning_absorption_to_heal_on_expire;
            entity.setAbsorptionAmount(0F);
            entity.heal(heal);
        });

        for (var entry: entries) {
            Synchronized.configure(entry.effect, true);
        }

        Effects.register(entries, config.effects);
    }
}
