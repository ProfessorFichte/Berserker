package net.berserker_rpg.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.entity.attribute.MRPGCEntityAttributes;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.ConfigFile;
import net.spell_engine.api.config.EffectConfig;
import net.spell_engine.api.effect.*;
import net.spell_engine.api.entity.SpellEngineAttributes;

import java.util.ArrayList;
import java.util.List;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class BerserkerEffects {
    public static final List<Effects.Entry> entries = new ArrayList<>();
    private static Effects.Entry add(Effects.Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static final Effects.Entry RAGE = add(new Effects.Entry(
            Identifier.of(MOD_ID, "rage"),
            "",
            "",
            new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xf70000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            MRPGCEntityAttributes.RAGE_MODIFIER.getIdAsString(),
                            0.1F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ATTACK_SPEED.getIdAsString(),
                            0.025F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));
    public static final Effects.Entry BLOOD_SACRIFICE = add(new Effects.Entry(
            Identifier.of(MOD_ID, "blood_sacrifice"),
            "",
            "",
            new BloodSacrificeEffect(StatusEffectCategory.BENEFICIAL, 0xf70000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_MAX_ABSORPTION.getIdAsString(),
                            4.0F,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    )
            ))
    ));
    public static final Effects.Entry OUTRAGE = add(new Effects.Entry(
            Identifier.of(MOD_ID, "outrage"),
            "",
            "",
            new OutrageEffect(StatusEffectCategory.BENEFICIAL, 0xf70000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ATTACK_SPEED.getIdAsString(),
                            0.1F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ATTACK_DAMAGE.getIdAsString(),
                            0.1F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));
    public static final Effects.Entry CARVE = add(new Effects.Entry(
            Identifier.of(MOD_ID, "carve"),
            "",
            "",
            new CustomStatusEffect(StatusEffectCategory.HARMFUL, 0xf70000),
            new EffectConfig(List.of(
                    new AttributeModifier(
                            EntityAttributes.GENERIC_ARMOR.getIdAsString(),
                            -0.1F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    ),
                    new AttributeModifier(
                            SpellEngineAttributes.DAMAGE_TAKEN.id.toString(),
                            -0.05F,
                            EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
                    )
            ))
    ));

    public static void register(ConfigFile.Effects config) {
        Synchronized.configure(RAGE.effect,true);
        Synchronized.configure(BLOOD_SACRIFICE.effect,true);
        Synchronized.configure(OUTRAGE.effect,true);

        Effects.register(entries, config.effects);
    }
}
