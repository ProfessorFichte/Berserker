package net.berserker_rpg.datagen;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.more_rpg_classes.datagen.SpellEngineAdvancementHelper;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class BerserkerAdvancementDataGen implements DataProvider {
    private final DataOutput.PathResolver pathResolver;

    public record Entry(
            Identifier id,
            String title,
            String description,
            @Nullable Identifier parent,
            String iconItemName,
            AdvancementFrame frame,
            boolean showToast,
            boolean announceToChat,
            boolean hidden,
            @Nullable String background,
            SpellEngineCriteriaType criteriaType,
            String criteriaValue
    ) {
        public String titleKey() {
            return "advancements." + id.getNamespace() + "." + id.getPath().replace("/", ".") + ".title";
        }

        public String descriptionKey() {
            return "advancements." + id.getNamespace() + "." + id.getPath().replace("/", ".") + ".description";
        }
    }

    public enum SpellEngineCriteriaType {
        SPELL_BOOK_CREATION,
        ONE_SPELL_BOUND,
        ALL_SPELLS_BOUND,
        SPELL_CAST
    }

    public static final List<Entry> entries = new ArrayList<>();

    private static Entry addEntry(Entry entry) {
        entries.add(entry);
        return entry;
    }

    private static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    static {
        addEntry(new Entry(
                id("path_choose_berserker"),
                "Path of the Berserker",
                "Create the Ancient Rune Tome",
                Identifier.of("more_rpg_content", "root"),
                MOD_ID + ":item/spell_scroll/berserker",
                AdvancementFrame.TASK,
                true, true, false, null,
                SpellEngineCriteriaType.SPELL_BOOK_CREATION,
                MOD_ID + ":spell_book/berserker"
        ));
        addEntry(new Entry(
                id("spell_cast_berserker_book"),
                "To the Ships!",
                "Use a skill from the Ancient Rune Tome",
                id("spell_novice_berserker"),
                MOD_ID + ":item/spell_book/berserker",
                AdvancementFrame.TASK,
                true, true, false, null,
                SpellEngineCriteriaType.SPELL_CAST,
                "#" + MOD_ID + ":spell_book/berserker"
        ));
        addEntry(new Entry(
                id("spell_novice_berserker"),
                "Berserking",
                "Obtain your first berserker skill",
                id("path_choose_berserker"),
                MOD_ID + ":iron_berserker_axe",
                AdvancementFrame.TASK,
                true, true, false, null,
                SpellEngineCriteriaType.ONE_SPELL_BOUND,
                MOD_ID + ":spell_book/berserker"
        ));
        addEntry(new Entry(
                id("spell_master_berserker"),
                "Relentless Raider",
                "Complete the Ancient Rune Tome",
                id("spell_novice_berserker"),
                MOD_ID + ":netherite_northling_head",
                AdvancementFrame.GOAL,
                true, true, false, null,
                SpellEngineCriteriaType.ALL_SPELLS_BOUND,
                MOD_ID + ":spell_book/berserker"
        ));
    }

    public BerserkerAdvancementDataGen(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "advancement");
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (Entry entry : entries) {
            JsonObject advancement = createAdvancementJson(entry);
            Path path = pathResolver.resolveJson(entry.id());
            futures.add(DataProvider.writeToPath(writer, advancement, path));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private JsonObject createAdvancementJson(Entry entry) {
        JsonObject advancement = new JsonObject();

        // Display
        JsonObject display = new JsonObject();
        JsonObject icon = new JsonObject();
        String iconName = entry.iconItemName().contains(":") ? entry.iconItemName() : MOD_ID + ":" + entry.iconItemName();
        if (iconName.contains("item/spell_book/")) {
            icon.addProperty("id", "spell_engine:spell_book");
            JsonObject components = new JsonObject();
            components.addProperty("spell_engine:item_model", iconName);
            icon.add("components", components);
        }
        else if (iconName.contains("item/spell_scroll/")) {
            icon.addProperty("id", "spell_engine:spell_scroll");
            JsonObject components = new JsonObject();
            components.addProperty("spell_engine:item_model", iconName);
            icon.add("components", components);
        }
        else {
            icon.addProperty("id", iconName);
        }
        display.add("icon", icon);
        display.add("title", createTranslatable(entry.titleKey()));
        display.add("description", createTranslatable(entry.descriptionKey()));
        display.addProperty("frame", entry.frame().asString());
        display.addProperty("show_toast", entry.showToast());
        display.addProperty("announce_to_chat", entry.announceToChat());
        display.addProperty("hidden", entry.hidden());
        if (entry.background() != null) {
            display.addProperty("background", entry.background());
        }
        advancement.add("display", display);

        // Parent
        if (entry.parent() != null) {
            advancement.addProperty("parent", entry.parent().toString());
        }

        // Criteria
        JsonObject criteria = getCriteriaForType(entry.criteriaType(), entry.criteriaValue());
        advancement.add("criteria", criteria);

        return advancement;
    }

    private JsonObject createTranslatable(String key) {
        JsonObject translatable = new JsonObject();
        translatable.addProperty("translate", key);
        return translatable;
    }

    private JsonObject getCriteriaForType(SpellEngineCriteriaType type, String value) {
        return switch (type) {
            case SPELL_BOOK_CREATION -> SpellEngineAdvancementHelper.criteriaSpellBookCreation(value);
            case ONE_SPELL_BOUND -> SpellEngineAdvancementHelper.criteriaOneSpellBound(value);
            case ALL_SPELLS_BOUND -> SpellEngineAdvancementHelper.criteriaAllSpellsBound(value);
            case SPELL_CAST -> SpellEngineAdvancementHelper.criteriaSpellCast(value);
        };
    }

    public static List<Entry> getEntries() {
        return entries;
    }

    @Override
    public String getName() {
        return "berserker Advancements";
    }
}

