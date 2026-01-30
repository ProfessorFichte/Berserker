package net.berserker_rpg.datagen;

import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.datagen.SmithingRecipeGenerator;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class BerserkerSmithingRecipeProvider extends SmithingRecipeGenerator {

    public BerserkerSmithingRecipeProvider(FabricDataOutput output) {
        super(output, MOD_ID);
    }

    @Override
    public String getName() {
        return "Smithing Recipes (" + MOD_ID + ")";
    }

    @Override
    public void generate() {
        // ==========================================
        // VANILLA NETHERITE UPGRADES (No conditions)
        // ==========================================

        // Netherite Berserker Axe
        createSimpleSmithingRecipe(
                "netherite_berserker_axe",
                WeaponsRegister.diamond_berserker_axe.item(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                WeaponsRegister.netherite_berserker_axe.item()
        );

        // Netherite Northling Armor Set (4 pieces)
        createSimpleArmorSetUpgrade(
                "netherite_northling",
                Armors.northlingArmorSet.armorSet(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                Armors.netheriteNorthlingArmorSet.armorSet()
        );

        // ==========================================
        // BETTER NETHER MOD - Ruby Berserker Axe
        // ==========================================

        Item rubyAxe = WeaponsRegister.entries.stream()
                .filter(e -> e.id().getPath().equals("ruby_berserker_axe"))
                .findFirst()
                .map(e -> e.item())
                .orElse(null);

        if (rubyAxe != null) {
            createSmithingTransformRecipe(
                    "ruby_berserker_axe",
                    WeaponsRegister.netherite_berserker_axe.item(),
                    Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                    Identifier.of("betternether", "nether_ruby"),
                    rubyAxe,
                    "betternether"
            );
        }

        // ==========================================
        // BETTER END MOD - Aeternium Berserker Axe (CRAFTING ONLY - No smithing recipe needed)
        // ==========================================

        // ==========================================
        // LOOT N EXPLORE MOD - Boss Berserker Axes
        // ==========================================

        // Glacial Berserker Axe (Frost Monarch)
        Item glacialAxe = WeaponsRegister.entries.stream()
                .filter(e -> e.id().getPath().equals("glacial_berserker_axe"))
                .findFirst()
                .map(e -> e.item())
                .orElse(null);

        if (glacialAxe != null) {
            createSmithingTransformRecipe(
                    "glacial_berserker_axe",
                    WeaponsRegister.netherite_berserker_axe.item(),
                    Identifier.of("loot_n_explore", "frostmonarch_upgrade_smithing_template"),
                    Identifier.of("loot_n_explore", "frozen_soul"),
                    glacialAxe,
                    "loot_n_explore"
            );
        }

        // Ender Dragon Berserker Axe
        Item enderDragonAxe = WeaponsRegister.entries.stream()
                .filter(e -> e.id().getPath().equals("ender_dragon_berserker_axe"))
                .findFirst()
                .map(e -> e.item())
                .orElse(null);

        if (enderDragonAxe != null) {
            createSmithingTransformRecipe(
                    "ender_dragon_berserker_axe",
                    WeaponsRegister.netherite_berserker_axe.item(),
                    Identifier.of("loot_n_explore", "dragon_upgrade_smithing_template"),
                    Identifier.of("loot_n_explore", "ender_dragon_scales"),
                    enderDragonAxe,
                    "loot_n_explore"
            );
        }

        // Elder Guardian Berserker Axe
        Item elderGuardianAxe = WeaponsRegister.entries.stream()
                .filter(e -> e.id().getPath().equals("elder_guardian_berserker_axe"))
                .findFirst()
                .map(e -> e.item())
                .orElse(null);

        if (elderGuardianAxe != null) {
            createSmithingTransformRecipe(
                    "elder_guardian_berserker_axe",
                    WeaponsRegister.netherite_berserker_axe.item(),
                    Identifier.of("loot_n_explore", "guardian_upgrade_smithing_template"),
                    Identifier.of("loot_n_explore", "elder_guardian_eye"),
                    elderGuardianAxe,
                    "loot_n_explore"
            );
        }

        // Wither Berserker Axe
        Item witherAxe = WeaponsRegister.entries.stream()
                .filter(e -> e.id().getPath().equals("wither_berserker_axe"))
                .findFirst()
                .map(e -> e.item())
                .orElse(null);

        if (witherAxe != null) {
            createSmithingTransformRecipe(
                    "wither_berserker_axe",
                    WeaponsRegister.netherite_berserker_axe.item(),
                    Identifier.of("loot_n_explore", "wither_upgrade_smithing_template"),
                    Identifier.of("loot_n_explore", "wither_spine"),
                    witherAxe,
                    "loot_n_explore"
            );
        }

        // ==========================================
        // ARMORY RPGS MOD - Warlord Armor Set
        // ==========================================

        if (Armors.warlordArmorSet != null) {
            // From Northling to Warlord
            createArmorSetUpgrade(
                    "smithing_warlord",
                    Armors.northlingArmorSet.armorSet(),
                    Identifier.of("armory_rpgs", "epic_armor_upgrade"),
                    Identifier.of("more_rpg_classes", "ravager_upgrade_crystal"),
                    Armors.warlordArmorSet.armorSet(),
                    "armory_rpgs"
            );

            // From Netherite Northling to Warlord
            createArmorSetUpgrade(
                    "smithing_warlord",
                    Armors.netheriteNorthlingArmorSet.armorSet(),
                    Identifier.of("armory_rpgs", "epic_armor_upgrade"),
                    Identifier.of("more_rpg_classes", "ravager_upgrade_crystal"),
                    Armors.warlordArmorSet.armorSet(),
                    "armory_rpgs"
            );
        }
    }
}
