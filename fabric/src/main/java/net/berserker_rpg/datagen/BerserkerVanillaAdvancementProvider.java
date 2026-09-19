package net.berserker_rpg.datagen;

import net.berserker_rpg.item.armor.Armors;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        return new Identifier(MOD_ID, path);
    }

    public static void init() {
        var northling = Armors.northlingArmorSet.armorSet();
        addEntry(new Entry(
                id("equipment/northling_armor_set"),
                "For the North!",
                "Obtain the full Northling Armor Set",
                new Identifier("more_rpg_content", "root"),
                (Item) northling.chest,
                AdvancementFrame.GOAL,
                true, true, false, null,
                new Item[]{
                        (Item) northling.head,
                        (Item) northling.chest,
                        (Item) northling.legs,
                        (Item) northling.feet
                },
                null
        ));
    }

    public BerserkerVanillaAdvancementProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        for (Entry entry : entries) {
            generateAdvancementEntry(entry, consumer);
        }
    }

    /// `Advancement.Builder#build` refuses to build unless the parent *object* resolved
    /// (`findParent(id -> null)`), and a cross-mod parent (`more_rpg_content:root`) never resolves during
    /// datagen. A stub carrying only the id satisfies it, and `toJson` serialises `parent` from that id -
    /// so the emitted JSON is identical to a hand-written `"parent": "..."`.
    private static Advancement parentStub(Identifier parentId) {
        return new Advancement(parentId, null, null, AdvancementRewards.NONE, Map.of(), new String[0][], false);
    }

    private void generateAdvancementEntry(Entry entry, Consumer<Advancement> consumer) {
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
            builder = builder.parent(parentStub(entry.parent()));
        }

        if (entry.experienceReward() != null) {
            builder.rewards(AdvancementRewards.Builder.experience(entry.experienceReward()));
        }

        // `build(Consumer, String)` already hands the advancement to the consumer; accepting it a second
        // time trips Fabric API 0.92's duplicate-id guard in FabricAdvancementProvider#run.
        builder.build(consumer, entry.id().toString());
    }

    public static List<Entry> getEntries() {
        return entries;
    }
}
