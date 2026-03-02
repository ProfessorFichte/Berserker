package net.berserker_rpg.datagen;

import net.berserker_rpg.effect.BerserkerEffects;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.berserker_rpg.spell.BerserkerSpells;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class LangGenerator extends FabricLanguageProvider {
    public LangGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder builder) {
        // Item Group
        builder.add("itemGroup." + MOD_ID + ".general", "Berserker");

        // Spell Book and Scroll
        builder.add("item." + MOD_ID + ".spell_book/berserker", "Ancient Rune Tome");
        builder.add("item.berserker_rpg.spell_book/berserker.spell_binding.description",
                "Spell Book of Berserkers, using raid axe's and other high damage weapons. The Berserker is all about high physical melee damage.\n- Strengths: High damage output.\n- Weaknesses: Ranged Enemies\n- Equipment: Light Armor");
        builder.add("item." + MOD_ID + ".spell_scroll/berserker", "Berserker Rune Scroll");

        // Weapons
        WeaponsRegister.entries.forEach(entry -> {
            if (entry.item() != null && entry.translatedName() != null && !entry.translatedName().isEmpty()) {
                builder.add(entry.item(), entry.translatedName());
            }
        });

        // Armors
        Armors.entries.forEach(entry -> {
            var set = entry.armorSet();
            if (set.headTranslation != null && !set.headTranslation.isEmpty()) {
                builder.add(((Item) set.head).getTranslationKey(), set.headTranslation);
            }
            if (set.chestTranslation != null && !set.chestTranslation.isEmpty()) {
                builder.add(((Item) set.chest).getTranslationKey(), set.chestTranslation);
            }
            if (set.legsTranslation != null && !set.legsTranslation.isEmpty()) {
                builder.add(((Item) set.legs).getTranslationKey(), set.legsTranslation);
            }
            if (set.feetTranslation != null && !set.feetTranslation.isEmpty()) {
                builder.add(((Item) set.feet).getTranslationKey(), set.feetTranslation);
            }
        });

        // Effects
        BerserkerEffects.entries.forEach(entry -> {
            if (entry.title != null && !entry.title.isEmpty()) {
                builder.add(entry.effect.getTranslationKey(), entry.title);
            }
            if (entry.description != null && !entry.description.isEmpty()) {
                builder.add(entry.effect.getTranslationKey() + ".description", entry.description);
            }
        });

        // Spells
        BerserkerSpells.entries.forEach(entry -> {
            var id = entry.id();
            if (entry.title() != null && !entry.title().isEmpty()) {
                builder.add("spell." + id.getNamespace() + "." + id.getPath() + ".name", entry.title());
            }
            if (entry.description() != null && !entry.description().isEmpty()) {
                builder.add("spell." + id.getNamespace() + "." + id.getPath() + ".description", entry.description());
            }
        });

        // Equipment Set
        builder.add("equipment_set." + MOD_ID + ".warlord", "Raging Warlord");

        // Death message
        builder.add("death.attack.berserker_cost", "%1$s collapsed from exhaustion.");
        // Advancements
        for (var entry : BerserkerAdvancementDataGen.getEntries()) {
            builder.add(entry.titleKey(), entry.title());
            builder.add(entry.descriptionKey(), entry.description());
        }
        for (var entry : BerserkerVanillaAdvancementProvider.getEntries()) {
            builder.add(entry.titleKey(), entry.title());
            builder.add(entry.descriptionKey(), entry.description());
        }

    }
}
