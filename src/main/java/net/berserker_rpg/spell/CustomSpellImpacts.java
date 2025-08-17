package net.berserker_rpg.spell;

import net.berserker_rpg.spell.custom_spell_impacts.BloodReckoningImpact;
import net.berserker_rpg.spell.custom_spell_impacts.LightningStrikeImpact;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.event.SpellHandlers;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class CustomSpellImpacts {

    public static void registerCustomImpacts(){
        SpellHandlers.registerCustomImpact(
                Identifier.of(MOD_ID, "blood_reckoning"),
                new BloodReckoningImpact()
        );
        SpellHandlers.registerCustomImpact(
                Identifier.of(MOD_ID, "lightning"),
                new LightningStrikeImpact()
        );
    }
}
