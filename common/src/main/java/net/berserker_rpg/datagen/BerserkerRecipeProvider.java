package net.berserker_rpg.datagen;

import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.item.MRPGCItems;

import java.util.concurrent.CompletableFuture;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class BerserkerRecipeProvider extends FabricRecipeProvider {

    private static Item getOrFallback(Identifier id, Item fallback) {
        var item = Registries.ITEM.get(id);
        return item != null && item != Items.AIR ? item : fallback;
    }

    public BerserkerRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "Crafting Recipes (" + MOD_ID + ")";
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // ==========================================
        // BERSERKER AXES - VANILLA MATERIALS
        // ==========================================

        // Flint Berserker Axe
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WeaponsRegister.flint_berserker_axe.item())
                .pattern("WW ")
                .pattern("WR ")
                .pattern("WR ")
                .input('W', Items.FLINT)
                .input('R', Items.STICK)
                .criterion(hasItem(Items.FLINT), conditionsFromItem(Items.FLINT))
                .offerTo(exporter, Identifier.of(MOD_ID, "flint_berserker_axe"));

        // Stone Berserker Axe
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WeaponsRegister.stone_berserker_axe.item())
                .pattern("WW ")
                .pattern("WR ")
                .pattern("WR ")
                .input('W', Items.COBBLESTONE)
                .input('R', Items.STICK)
                .criterion(hasItem(Items.COBBLESTONE), conditionsFromItem(Items.COBBLESTONE))
                .offerTo(exporter, Identifier.of(MOD_ID, "stone_berserker_axe"));

        // Iron Berserker Axe
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WeaponsRegister.iron_berserker_axe.item())
                .pattern("WW ")
                .pattern("WR ")
                .pattern("WR ")
                .input('W', Items.IRON_INGOT)
                .input('R', Items.BONE)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter, Identifier.of(MOD_ID, "iron_berserker_axe"));

        // Golden Berserker Axe
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WeaponsRegister.golden_berserker_axe.item())
                .pattern("WW ")
                .pattern("WR ")
                .pattern("WR ")
                .input('W', Items.GOLD_INGOT)
                .input('R', Items.BONE)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter, Identifier.of(MOD_ID, "golden_berserker_axe"));

        // Diamond Berserker Axe
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, WeaponsRegister.diamond_berserker_axe.item())
                .pattern("WW ")
                .pattern("WR ")
                .pattern("WR ")
                .input('W', Items.DIAMOND)
                .input('R', Items.BONE)
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .offerTo(exporter, Identifier.of(MOD_ID, "diamond_berserker_axe"));

        // ==========================================
        // BERSERKER AXES - MODDED MATERIALS
        // ==========================================
        // Note: Modded material axes (aeternium, etc.) are created via the shaped recipe JSON directly
        // because we need to handle load conditions properly for cross-mod items.
        // These will be generated separately via the smithing recipe provider if they use smithing.

        // ==========================================
        // WILDLING ARMOR SET
        // ==========================================

        // Wildling Head
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Armors.wildlingArmorSet.armorSet().head)
                .pattern("R R")
                .pattern("WRW")
                .pattern("   ")
                .input('W', Items.LEATHER)
                .input('R', MRPGCItems.WOLF_FUR)
                .criterion(hasItem(MRPGCItems.WOLF_FUR), conditionsFromItem(MRPGCItems.WOLF_FUR))
                .offerTo(exporter, Identifier.of(MOD_ID, "wildling_head"));

        // Wildling Chest
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Armors.wildlingArmorSet.armorSet().chest)
                .pattern("R R")
                .pattern("WRW")
                .pattern("WZW")
                .input('W', Items.LEATHER)
                .input('R', MRPGCItems.WOLF_FUR)
                .input('Z', Items.CHAIN)
                .criterion(hasItem(MRPGCItems.WOLF_FUR), conditionsFromItem(MRPGCItems.WOLF_FUR))
                .offerTo(exporter, Identifier.of(MOD_ID, "wildling_chest"));

        // Wildling Legs
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Armors.wildlingArmorSet.armorSet().legs)
                .pattern("WRW")
                .pattern("R R")
                .pattern("Z Z")
                .input('W', Items.LEATHER)
                .input('R', MRPGCItems.WOLF_FUR)
                .input('Z', Items.CHAIN)
                .criterion(hasItem(MRPGCItems.WOLF_FUR), conditionsFromItem(MRPGCItems.WOLF_FUR))
                .offerTo(exporter, Identifier.of(MOD_ID, "wildling_legs"));

        // Wildling Feet
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Armors.wildlingArmorSet.armorSet().feet)
                .pattern("R R")
                .pattern("Z Z")
                .input('R', MRPGCItems.WOLF_FUR)
                .input('Z', Items.CHAIN)
                .criterion(hasItem(MRPGCItems.WOLF_FUR), conditionsFromItem(MRPGCItems.WOLF_FUR))
                .offerTo(exporter, Identifier.of(MOD_ID, "wildling_feet"));

        // ==========================================
        // NORTHLING ARMOR SET
        // ==========================================

        // Northling Head
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Armors.northlingArmorSet.armorSet().head)
                .pattern("R R")
                .pattern("WRW")
                .pattern("   ")
                .input('W', Items.LEATHER)
                .input('R', MRPGCItems.POLAR_BEAR_FUR)
                .criterion(hasItem(MRPGCItems.POLAR_BEAR_FUR), conditionsFromItem(MRPGCItems.POLAR_BEAR_FUR))
                .offerTo(exporter, Identifier.of(MOD_ID, "northling_head"));

        // Northling Chest
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Armors.northlingArmorSet.armorSet().chest)
                .pattern("R R")
                .pattern("WRW")
                .pattern("ZZZ")
                .input('W', Items.LEATHER)
                .input('R', MRPGCItems.POLAR_BEAR_FUR)
                .input('Z', Items.CHAIN)
                .criterion(hasItem(MRPGCItems.POLAR_BEAR_FUR), conditionsFromItem(MRPGCItems.POLAR_BEAR_FUR))
                .offerTo(exporter, Identifier.of(MOD_ID, "northling_chest"));

        // Northling Legs
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Armors.northlingArmorSet.armorSet().legs)
                .pattern("WRW")
                .pattern("R R")
                .pattern("Z Z")
                .input('W', Items.LEATHER)
                .input('R', MRPGCItems.POLAR_BEAR_FUR)
                .input('Z', Items.CHAIN)
                .criterion(hasItem(MRPGCItems.POLAR_BEAR_FUR), conditionsFromItem(MRPGCItems.POLAR_BEAR_FUR))
                .offerTo(exporter, Identifier.of(MOD_ID, "northling_legs"));

        // Northling Feet
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, Armors.northlingArmorSet.armorSet().feet)
                .pattern("R R")
                .pattern("Z Z")
                .input('R', MRPGCItems.POLAR_BEAR_FUR)
                .input('Z', Items.CHAIN)
                .criterion(hasItem(MRPGCItems.POLAR_BEAR_FUR), conditionsFromItem(MRPGCItems.POLAR_BEAR_FUR))
                .offerTo(exporter, Identifier.of(MOD_ID, "northling_feet"));

        // ==========================================
        // BERSERKER SPELL BOOK
        // ==========================================

        var berserkerBook = getOrFallback(Identifier.of(MOD_ID, "berserker_spell_book"), Items.WRITTEN_BOOK);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, berserkerBook)
                .input(MRPGCItems.HARDENED_LEATHER)
                .input(MRPGCItems.WOLF_FUR)
                .input(Items.BOOK)
                .input(Items.LAPIS_LAZULI)
                .criterion(hasItem(MRPGCItems.HARDENED_LEATHER), conditionsFromItem(MRPGCItems.HARDENED_LEATHER))
                .offerTo(exporter);
    }
}
