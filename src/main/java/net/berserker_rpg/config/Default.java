package net.berserker_rpg.config;

import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.spell_engine.api.item.ItemConfig;

public class Default {
    public final static ItemConfig itemConfig;


    static {
        itemConfig = new ItemConfig();
        for (var armorSet : Armors.entries) {
            itemConfig.armor_sets.put(armorSet.name(), armorSet.defaults());
        }
        for (var weapon: WeaponsRegister.entries) {
            itemConfig.weapons.put(weapon.name(), weapon.defaults());
        }

    }
}