package net.berserker_rpg.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

/**
 * Provider for crafting recipes that require mod load conditions
 */
public class BerserkerConditionalRecipeProvider implements DataProvider {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final FabricDataOutput output;
    private final List<ConditionalRecipe> recipes = new ArrayList<>();

    public BerserkerConditionalRecipeProvider(FabricDataOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        generateRecipes();

        return CompletableFuture.allOf(recipes.stream().map(recipe -> {
            JsonObject json = buildRecipeJson(recipe);
            Path path = output.getResolver(net.minecraft.data.DataOutput.OutputType.DATA_PACK, "recipes")
                    .resolveJson(new Identifier(MOD_ID, recipe.name));

            return DataProvider.writeToPath(writer, json, path);
        }).toArray(CompletableFuture[]::new));
    }

    private void generateRecipes() {
        // ==========================================
        // AETERNIUM BERSERKER AXE (Better End)
        // ==========================================

        Item aeterniumAxe = WeaponsRegister.entries.stream()
                .filter(e -> e.id().getPath().equals("aeternium_berserker_axe"))
                .findFirst()
                .map(e -> e.item())
                .orElse(null);

        if (aeterniumAxe != null) {
            recipes.add(new ConditionalRecipe(
                    "aeternium_berserker_axe",
                    "betterend",
                    createShapedRecipe(
                            "BB ",
                            "BR ",
                            "BR ",
                            "B", "betterend:aeternium_ingot",
                            "R", "minecraft:bone",
                            aeterniumAxe
                    )
            ));
        }
    }

    private JsonObject createShapedRecipe(String pattern1, String pattern2, String pattern3,
                                          String key1, String item1,
                                          String key2, String item2,
                                          Item result) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shaped");

        // Pattern
        JsonArray pattern = new JsonArray();
        pattern.add(pattern1);
        pattern.add(pattern2);
        pattern.add(pattern3);
        recipe.add("pattern", pattern);

        // Key
        JsonObject key = new JsonObject();
        JsonObject key1Obj = new JsonObject();
        key1Obj.addProperty("item", item1);
        key.add(key1, key1Obj);

        JsonObject key2Obj = new JsonObject();
        key2Obj.addProperty("item", item2);
        key.add(key2, key2Obj);
        recipe.add("key", key);

        // Result -- 1.20.1 recipe results are keyed by `item`, not `id`
        JsonObject resultObj = new JsonObject();
        resultObj.addProperty("item", net.minecraft.registry.Registries.ITEM.getId(result).toString());
        recipe.add("result", resultObj);

        return recipe;
    }

    private JsonObject buildRecipeJson(ConditionalRecipe recipe) {
        JsonObject json = recipe.recipeJson;

        // Add Fabric load conditions
        JsonArray fabricLoadConditions = new JsonArray();
        JsonObject fabricCondition = new JsonObject();
        fabricCondition.addProperty("condition", "fabric:all_mods_loaded");
        JsonArray modValues = new JsonArray();
        modValues.add(recipe.requiredMod);
        fabricCondition.add("values", modValues);
        fabricLoadConditions.add(fabricCondition);

        // Forge 47 reads a plain top-level `conditions` array; `forge:conditions` / `neoforge:conditions`
        // are keys it does not know, and the recipe would then parse and fail on the unloaded modded item.
        JsonArray forgeConditions = new JsonArray();
        JsonObject forgeCondition = new JsonObject();
        forgeCondition.addProperty("type", "forge:mod_loaded");
        forgeCondition.addProperty("modid", recipe.requiredMod);
        forgeConditions.add(forgeCondition);

        // Build final JSON with conditions at the top
        JsonObject finalJson = new JsonObject();
        finalJson.add("fabric:load_conditions", fabricLoadConditions);
        finalJson.add("conditions", forgeConditions);

        // Copy all other properties
        json.entrySet().forEach(entry -> finalJson.add(entry.getKey(), entry.getValue()));

        return finalJson;
    }

    @Override
    public String getName() {
        return "Conditional Crafting Recipes (" + MOD_ID + ")";
    }

    private record ConditionalRecipe(String name, String requiredMod, JsonObject recipeJson) {}
}
