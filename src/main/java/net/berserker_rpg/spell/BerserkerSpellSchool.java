package net.berserker_rpg.spell;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.entity.attribute.MRPGCEntityAttributes;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;
import static net.spell_power.api.SpellPowerMechanics.PERCENT_ATTRIBUTE_BASELINE;

public class BerserkerSpellSchool {
    public static final SpellSchool BERSERKER_MELEE = new SpellSchool(SpellSchool.Archetype.MELEE,
            Identifier.of(MOD_ID, "berserker_melee"),
            0xb3b3b3,
            DamageTypes.PLAYER_ATTACK,
            EntityAttributes.GENERIC_ATTACK_DAMAGE);

    public static void initialize() {
        BERSERKER_MELEE.addSource(SpellSchool.Trait.POWER, SpellSchool.Apply.ADD, query -> {
            return query.entity().getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) +
                    ((query.entity().getAttributeValue(MRPGCEntityAttributes.RAGE_MODIFIER)-100) / 50);
        });
        BERSERKER_MELEE.addSource(SpellSchool.Trait.CRIT_CHANCE, new SpellSchool.Source(SpellSchool.Apply.ADD, query ->  {
            var value = SpellPowerMod.attributesConfig.value.base_spell_critical_chance_percentage
                    + query.entity().getAttributeValue(MRPGCEntityAttributes.RAGE_MODIFIER )- 100/ 10;
            return (value/ PERCENT_ATTRIBUTE_BASELINE)-1;
        }));
        BERSERKER_MELEE.addSource(SpellSchool.Trait.CRIT_DAMAGE, new SpellSchool.Source(SpellSchool.Apply.ADD, query -> {
            var value = SpellPowerMod.attributesConfig.value.base_spell_critical_damage_percentage
                    + query.entity().getAttributeValue(MRPGCEntityAttributes.RAGE_MODIFIER )- 100/ 4;
            return (value/ PERCENT_ATTRIBUTE_BASELINE)-1;
        }));
        SpellSchools.configureSpellCritDamage(BERSERKER_MELEE);
        SpellSchools.configureSpellCritChance(BERSERKER_MELEE);
        SpellSchools.configureSpellHaste(BERSERKER_MELEE);
        SpellSchools.register(BERSERKER_MELEE);
    }
}
