package net.berserker_rpg.item;

import net.berserker_rpg.BerserkerClassMod;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.spell_engine.api.item.SpellBooks;

import java.util.HashMap;

public class BerserkerItems {
    public static final HashMap<String, Item> entries;
    static {
        entries = new HashMap<>();
        for(var weaponEntry: WeaponsRegister.entries) {
            entries.put(weaponEntry.id().toString(), weaponEntry.item());
        }
        for(var entry: Armors.entries) {
            var set = entry.armorSet();
            for (var piece: set.pieces()) {
                var armorItem = (ArmorItem) piece;
                entries.put(set.idOf(armorItem).toString(), armorItem);
            }
        }
    }


    public static void registerModItems(){
        SpellBooks.createAndRegister(Identifier.of(BerserkerClassMod.MOD_ID,"berserker"), BerserkerGroup.BERSERKER_KEY);

        ItemGroupEvents.modifyEntriesEvent(BerserkerGroup.BERSERKER_KEY).register((content) -> {
        });

        BerserkerClassMod.LOGGER.info("Registering Mod Items for " + BerserkerClassMod.MOD_ID);
    }
}

