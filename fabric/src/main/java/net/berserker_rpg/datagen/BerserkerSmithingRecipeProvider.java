package net.berserker_rpg.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.spell_engine.rpg_series.item.Armor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class BerserkerSmithingRecipeProvider implements DataProvider {

    private final FabricDataOutput output;
    private final List<RecipeData> recipes = new ArrayList<>();

    public BerserkerSmithingRecipeProvider(FabricDataOutput output) {
        this.output = output;
    }

    @Override
    public String getName() {
        return "Smithing Recipes (" + MOD_ID + ")";
    }

    public void generate() {
        createSimpleSmithingRecipe(
                "netherite_berserker_axe",
                WeaponsRegister.diamond_berserker_axe.item(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                WeaponsRegister.netherite_berserker_axe.item()
        );

        createSimpleArmorSetUpgrade(
                "netherite_northling",
                Armors.northlingArmorSet.armorSet(),
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                Items.NETHERITE_INGOT,
                Armors.netheriteNorthlingArmorSet.armorSet()
        );

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
                    new Identifier("betternether", "nether_ruby"),
                    rubyAxe,
                    "betternether"
            );
        }

        Item glacialAxe = WeaponsRegister.entries.stream()
                .filter(e -> e.id().getPath().equals("glacial_berserker_axe"))
                .findFirst()
                .map(e -> e.item())
                .orElse(null);

        if (glacialAxe != null) {
            createSmithingTransformRecipe(
                    "glacial_berserker_axe",
                    WeaponsRegister.netherite_berserker_axe.item(),
                    new Identifier("loot_n_explore", "frostmonarch_upgrade_smithing_template"),
                    new Identifier("loot_n_explore", "frozen_soul"),
                    glacialAxe,
                    "loot_n_explore"
            );
        }

        Item enderDragonAxe = WeaponsRegister.entries.stream()
                .filter(e -> e.id().getPath().equals("ender_dragon_berserker_axe"))
                .findFirst()
                .map(e -> e.item())
                .orElse(null);

        if (enderDragonAxe != null) {
            createSmithingTransformRecipe(
                    "ender_dragon_berserker_axe",
                    WeaponsRegister.netherite_berserker_axe.item(),
                    new Identifier("loot_n_explore", "dragon_upgrade_smithing_template"),
                    new Identifier("loot_n_explore", "ender_dragon_scales"),
                    enderDragonAxe,
                    "loot_n_explore"
            );
        }

        Item elderGuardianAxe = WeaponsRegister.entries.stream()
                .filter(e -> e.id().getPath().equals("elder_guardian_berserker_axe"))
                .findFirst()
                .map(e -> e.item())
                .orElse(null);

        if (elderGuardianAxe != null) {
            createSmithingTransformRecipe(
                    "elder_guardian_berserker_axe",
                    WeaponsRegister.netherite_berserker_axe.item(),
                    new Identifier("loot_n_explore", "guardian_upgrade_smithing_template"),
                    new Identifier("loot_n_explore", "elder_guardian_eye"),
                    elderGuardianAxe,
                    "loot_n_explore"
            );
        }

        Item witherAxe = WeaponsRegister.entries.stream()
                .filter(e -> e.id().getPath().equals("wither_berserker_axe"))
                .findFirst()
                .map(e -> e.item())
                .orElse(null);

        if (witherAxe != null) {
            createSmithingTransformRecipe(
                    "wither_berserker_axe",
                    WeaponsRegister.netherite_berserker_axe.item(),
                    new Identifier("loot_n_explore", "wither_upgrade_smithing_template"),
                    new Identifier("loot_n_explore", "wither_spine"),
                    witherAxe,
                    "loot_n_explore"
            );
        }

        if (Armors.warlordArmorSet != null) {
            createArmorSetUpgrade(
                    "smithing_northling",
                    Armors.northlingArmorSet.armorSet(),
                    new Identifier("armory_rpgs", "epic_armor_upgrade"),
                    new Identifier("more_rpg_classes", "ravager_upgrade_crystal"),
                    Armors.warlordArmorSet.armorSet(),
                    "armory_rpgs"
            );

            createArmorSetUpgrade(
                    "smithing_netherite_northling",
                    Armors.netheriteNorthlingArmorSet.armorSet(),
                    new Identifier("armory_rpgs", "epic_armor_upgrade"),
                    new Identifier("more_rpg_classes", "ravager_upgrade_crystal"),
                    Armors.warlordArmorSet.armorSet(),
                    "armory_rpgs"
            );
        }
    }

    private void createSimpleSmithingRecipe(String name, Item base, Object template, Object addition, Item result) {
        recipes.add(new RecipeData(name, base, template, addition, result, null));
    }

    private void createSmithingTransformRecipe(String name, Item base, Object template, Object addition,
                                               Item result, String requiredMod) {
        recipes.add(new RecipeData(name, base, template, addition, result, requiredMod));
    }

    private void createSimpleArmorSetUpgrade(String recipeBaseName, Armor.Set baseSet, Object template,
                                             Object addition, Armor.Set resultSet) {
        armorSetUpgrade(recipeBaseName, baseSet, template, addition, resultSet, null);
    }

    private void createArmorSetUpgrade(String recipeBaseName, Armor.Set baseSet, Object template,
                                       Object addition, Armor.Set resultSet, String requiredMod) {
        armorSetUpgrade(recipeBaseName, baseSet, template, addition, resultSet, requiredMod);
    }

    private void armorSetUpgrade(String recipeBaseName, Armor.Set baseSet, Object template, Object addition,
                                 Armor.Set resultSet, String requiredMod) {
        String resultSetName = extractArmorSetName(resultSet);
        recipes.add(new RecipeData(recipeBaseName + "_" + resultSetName + "_head",
                (Item) baseSet.head, template, addition, (Item) resultSet.head, requiredMod));
        recipes.add(new RecipeData(recipeBaseName + "_" + resultSetName + "_chest",
                (Item) baseSet.chest, template, addition, (Item) resultSet.chest, requiredMod));
        recipes.add(new RecipeData(recipeBaseName + "_" + resultSetName + "_legs",
                (Item) baseSet.legs, template, addition, (Item) resultSet.legs, requiredMod));
        recipes.add(new RecipeData(recipeBaseName + "_" + resultSetName + "_feet",
                (Item) baseSet.feet, template, addition, (Item) resultSet.feet, requiredMod));
    }

    private static String extractArmorSetName(Armor.Set armorSet) {
        Identifier id = Registries.ITEM.getId((Item) armorSet.head);
        String path = id.getPath();
        if (path.endsWith("_head")) {
            return path.substring(0, path.length() - 5);
        }
        return path;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        generate();

        return CompletableFuture.allOf(recipes.stream().map(recipeData -> {
            JsonObject recipe = buildRecipeJson(recipeData);
            Path path = output.getResolver(DataOutput.OutputType.DATA_PACK, "recipes")
                    .resolveJson(new Identifier(MOD_ID, recipeData.name));

            return DataProvider.writeToPath(writer, recipe, path);
        }).toArray(CompletableFuture[]::new));
    }

    private JsonObject buildRecipeJson(RecipeData data) {
        JsonObject recipe = new JsonObject();

        if (data.requiredMod != null) {
            JsonArray fabricLoadConditions = new JsonArray();
            JsonObject fabricCondition = new JsonObject();
            fabricCondition.addProperty("condition", "fabric:all_mods_loaded");
            JsonArray modValues = new JsonArray();
            modValues.add(data.requiredMod);
            fabricCondition.add("values", modValues);
            fabricLoadConditions.add(fabricCondition);
            recipe.add("fabric:load_conditions", fabricLoadConditions);

            JsonArray forgeConditions = new JsonArray();
            JsonObject forgeCondition = new JsonObject();
            forgeCondition.addProperty("type", "forge:mod_loaded");
            forgeCondition.addProperty("modid", data.requiredMod);
            forgeConditions.add(forgeCondition);
            recipe.add("conditions", forgeConditions);
        }

        recipe.addProperty("type", "minecraft:smithing_transform");

        JsonObject templateObj = new JsonObject();
        templateObj.addProperty("item", getItemId(data.template));
        recipe.add("template", templateObj);

        JsonObject baseObj = new JsonObject();
        baseObj.addProperty("item", Registries.ITEM.getId(data.base).toString());
        recipe.add("base", baseObj);

        JsonObject additionObj = new JsonObject();
        additionObj.addProperty("item", getItemId(data.addition));
        recipe.add("addition", additionObj);

        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("item", Registries.ITEM.getId(data.result).toString());
        resultObj.addProperty("count", 1);
        recipe.add("result", resultObj);

        return recipe;
    }

    private static String getItemId(Object itemOrId) {
        if (itemOrId instanceof Identifier id) {
            return id.toString();
        } else if (itemOrId instanceof String str) {
            return str;
        } else if (itemOrId instanceof Item item) {
            Identifier id = Registries.ITEM.getId(item);
            if (id.equals(Registries.ITEM.getId(Items.AIR))) {
                throw new IllegalStateException("Item resolved to minecraft:air - use Identifier instead of Item for cross-mod items!");
            }
            return id.toString();
        }
        throw new IllegalArgumentException("Template/Addition must be Item, Identifier, or String, got: " + itemOrId.getClass());
    }

    private record RecipeData(
            String name,
            Item base,
            Object template,
            Object addition,
            Item result,
            String requiredMod
    ) {}
}
