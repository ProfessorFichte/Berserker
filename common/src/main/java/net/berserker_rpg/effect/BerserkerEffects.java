package net.berserker_rpg.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectCategory;
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
    public static final List<Effects.Entry> entries = new ArrayList<>();
    private static Effects.Entry add(Effects.Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static final Effects.Entry RAGE = add(new Effects.Entry(
            Identifier.of(MOD_ID, "rage"),
            "Rage",
            "Increases Rage & Attack Speed. You deal more damage, the less health you have.",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xf70000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            MRPGCEntityAttributes.RAGE_MODIFIER.getIdAsString(),
                            0.1F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ATTACK_SPEED.getIdAsString(),
                            0.02F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));
    public static final Effects.Entry BLOOD_SACRIFICE = add(new Effects.Entry(
            Identifier.of(MOD_ID, "blood_sacrifice"),
            "Blood Sacrifice",
            "Converts your health to absorption hearts.",
            new BloodSacrificeEffect(StatusEffectCategory.BENEFICIAL, 0xf70000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_MAX_ABSORPTION.getIdAsString(),
                            4.0F,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    )
            ))
    ));
    public static final Effects.Entry BLOOD_RECKONING = add(new Effects.Entry(
            Identifier.of(MOD_ID, "blood_reckoning"),
            "Blood Reckoning",
            "Grants absorption for missing health. Heals for a portion of your remaining absorption when the effect runs out.",
            new BloodReckoningEffect(StatusEffectCategory.BENEFICIAL, 0xf70000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_MAX_ABSORPTION.getIdAsString(),
                            20.0F,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    )
            ))
    ));
    public static final Effects.Entry OUTRAGE = add(new Effects.Entry(
            Identifier.of(MOD_ID, "outrage"),
            "Outrage",
            "Increases attack damage and attack speed.",
            new OutrageEffect(StatusEffectCategory.BENEFICIAL, 0xf70000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString(),
                            0.2F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ATTACK_SPEED.getIdAsString(),
                            0.2F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));
    public static final Effects.Entry APPREHEND = add(new Effects.Entry(
            Identifier.of(MOD_ID, "apprehend"),
            "Apprehend",
            "Lowers armor.",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0x8a1f1f),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ARMOR.getIdAsString(),
                            -4.0F,
                            EntityAttributeModifier.Operation.ADD_VALUE
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
