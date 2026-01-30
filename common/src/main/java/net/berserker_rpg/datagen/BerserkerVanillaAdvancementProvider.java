package net.berserker_rpg.datagen;

import net.berserker_rpg.item.armor.Armors;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class BerserkerVanillaAdvancementProvider extends FabricAdvancementProvider {

    public record Entry(
            Identifier id,
            String title,
            String description,
            @Nullable Identifier parent,
            Item iconItem,
            AdvancementFrame frame,
            boolean showToast,
            boolean announceToChat,
            boolean hidden,
            @Nullable String background,
            Item[] requiredItems,
            @Nullable Integer experienceReward
    ) {
        public String titleKey() {
            return "advancements." + id.getNamespace() + "." + id.getPath().replace("/", ".") + ".title";
        }

        public String descriptionKey() {
            return "advancements." + id.getNamespace() + "." + id.getPath().replace("/", ".") + ".description";
        }
    }

    public static final List<Entry> entries = new ArrayList<>();

    private static Entry addEntry(Entry entry) {
        entries.add(entry);
        return entry;
    }

    private static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static void init() {
        var phasleb = Armors.northlingArmorSet.armorSet();
        addEntry(new Entry(
                id("equipment/phasleb_armor_set"),
                "For the North!",
                "Obtain the full Northling Armor Set",
                Identifier.of("more_rpg_content", "root"),
                (Item) phasleb.chest,
                AdvancementFrame.GOAL,
                true, true, false, null,
                new Item[]{
                        (Item) phasleb.head,
                        (Item) phasleb.chest,
                        (Item) phasleb.legs,
                        (Item) phasleb.feet
                },
                null
        ));
    }

    public BerserkerVanillaAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup wrapperLookup, Consumer<AdvancementEntry> consumer) {
        for (Entry entry : entries) {
            generateAdvancementEntry(entry, consumer);
        }
    }

    private void generateAdvancementEntry(Entry entry, Consumer<AdvancementEntry> consumer) {
        Item iconItem = entry.iconItem() != null ? entry.iconItem() : Items.BARRIER;

        var builder = Advancement.Builder.create()
                .display(
                        iconItem,
                        Text.translatable(entry.titleKey()),
                        Text.translatable(entry.descriptionKey()),
                        entry.background() != null ? Identifier.tryParse(entry.background()) : null,
                        entry.frame(),
                        entry.showToast(),
                        entry.announceToChat(),
                        entry.hidden()
                );

        builder.criterion("has_all_items", InventoryChangedCriterion.Conditions.items(entry.requiredItems()));

        if (entry.parent() != null) {
            @SuppressWarnings("deprecation")
            var builderWithParent = builder.parent(entry.parent());
            builder = builderWithParent;
        }

        if (entry.experienceReward() != null) {
            builder.rewards(AdvancementRewards.Builder.experience(entry.experienceReward()));
        }

        consumer.accept(builder.build(consumer, entry.id().toString()));
    }

    public static List<Entry> getEntries() {
        return entries;
    }
}
