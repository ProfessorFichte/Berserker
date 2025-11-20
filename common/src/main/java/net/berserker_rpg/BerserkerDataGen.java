package net.berserker_rpg;

import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.berserker_rpg.sounds.BerserkerSounds;
import net.berserker_rpg.spell.BerserkerSpells;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.spell_engine.api.datagen.SimpleSoundGeneratorV2;
import net.spell_engine.api.datagen.SpellGenerator;
import net.spell_engine.api.item.armor.Armor;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;
import net.spell_engine.rpg_series.tags.RPGSeriesItemTags;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class BerserkerDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ItemTagGenerator::new);
        pack.addProvider(UnsmeltGenerator::new);
        pack.addProvider(SpellGen::new);
        pack.addProvider(SpellTagGenerator::new);
        pack.addProvider(SoundGen::new);
        pack.addProvider(ModelProvider::new);
    }

    public static class SpellGen extends SpellGenerator {
        public SpellGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateSpells(Builder builder) {
            for (var entry: BerserkerSpells.entries) {
                builder.add(entry.id(), entry.spell());
            }
        }
    }

    public static class SpellTagGenerator extends FabricTagProvider<Spell> {
        public SpellTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, SpellRegistry.KEY, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getOrCreateTagBuilder(TagKey.of(SpellRegistry.KEY, Identifier.of("arsenal", "melee")))
                    .addOptional(BerserkerSpells.carve_melee.id())
            ;
        }
    }

    public static class ItemTagGenerator extends RPGSeriesDataGen.ItemTagGenerator {
        public ItemTagGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        public void armoryTags(List<Armor.Entry> armors) {
            this.armoryTags(armors, EnumSet.noneOf(RPGSeriesItemTags.ArmorMetaType.class));
        }

        public void armoryTags(List<Armor.Entry> armors, RPGSeriesItemTags.ArmorMetaType metaType) {
            this.armoryTags(armors, EnumSet.of(metaType));
        }

        public void armoryTags(List<Armor.Entry> armors, EnumSet<RPGSeriesItemTags.ArmorMetaType> metaTypes) {
            Iterator var3 = armors.iterator();

            while(var3.hasNext()) {
                Armor.Entry armor = (Armor.Entry)var3.next();
                Armor.Set set = armor.armorSet();
                FabricTagProvider<Item>.FabricTagBuilder headTag = this.getOrCreateTagBuilder(ItemTags.HEAD_ARMOR);
                headTag.addOptional(set.idOf(set.head));
                FabricTagProvider<Item>.FabricTagBuilder chestTag = this.getOrCreateTagBuilder(ItemTags.CHEST_ARMOR);
                chestTag.addOptional(set.idOf(set.chest));
                FabricTagProvider<Item>.FabricTagBuilder legsTag = this.getOrCreateTagBuilder(ItemTags.LEG_ARMOR);
                legsTag.addOptional(set.idOf(set.legs));
                FabricTagProvider<Item>.FabricTagBuilder feetTag = this.getOrCreateTagBuilder(ItemTags.FOOT_ARMOR);
                feetTag.addOptional(set.idOf(set.feet));
                Iterator var12;

                String lootTheme = armor.lootProperties().theme();
                if (lootTheme != null && !lootTheme.isEmpty()) {
                    FabricTagProvider<Item>.FabricTagBuilder themeTag = this.getOrCreateTagBuilder(RPGSeriesItemTags.LootThemes.get(lootTheme));
                    Iterator var19 = armor.armorSet().pieceIds().iterator();

                    while(var19.hasNext()) {
                        Object id = var19.next();
                        themeTag.addOptional((Identifier)id);
                    }
                }

                var12 = metaTypes.iterator();

                while(var12.hasNext()) {
                    RPGSeriesItemTags.ArmorMetaType metaType = (RPGSeriesItemTags.ArmorMetaType)var12.next();
                    FabricTagProvider<Item>.FabricTagBuilder metaTag = this.getOrCreateTagBuilder(RPGSeriesItemTags.ArmorType.get(metaType));
                    Iterator var15 = armor.armorSet().pieceIds().iterator();

                    while(var15.hasNext()) {
                        Object id = var15.next();
                        metaTag.addOptional((Identifier)id);
                    }
                }
            }

        }
        List<String> armoryKeywords = List.of("warlord");
        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            generateWeaponTags(WeaponsRegister.entries);
            var armorTagOptions1 = new ArmorOptions(false, true);
            var armorTagOptions2 = new ArmorOptions(true, true);
            armoryTags(
                    Armors.entries.stream().filter(entry -> armoryKeywords.stream().anyMatch(entry.name()::contains)).toList(),
                    RPGSeriesItemTags.ArmorMetaType.MELEE
            );
            generateArmorTags(
                    Armors.entries.stream().filter(entry -> armoryKeywords.stream().noneMatch(entry.name()::contains)).toList(),
                    RPGSeriesItemTags.ArmorMetaType.MELEE,
                    armorTagOptions2
            );
        }
    }

    public static class UnsmeltGenerator extends FabricRecipeProvider {
        public UnsmeltGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        public static int UNSMELT_TIME = 300;

        @Override
        public void generate(RecipeExporter exporter) {
            disassembleArmor(exporter, Armors.wildlingArmorSet, Items.LEATHER);
            disassembleArmor(exporter, Armors.northlingArmorSet, Items.IRON_NUGGET);
            disassembleArmor(exporter, Armors.netheriteNorthlingArmorSet, Items.NETHERITE_SCRAP);

            disassemble(exporter,
                    WeaponsRegister.entries.stream()
                            .filter(entry -> entry.id().getPath().contains("flint"))
                            .map(entry -> (ItemConvertible) entry.item()).toList(),
                    Items.FLINT);
            disassemble(exporter,
                    WeaponsRegister.entries.stream()
                            .filter(entry -> entry.id().getPath().contains("gold"))
                            .map(entry -> (ItemConvertible) entry.item()).toList(),
                    Items.GOLD_NUGGET);
            disassemble(exporter,
                    WeaponsRegister.entries.stream()
                            .filter(entry -> entry.id().getPath().contains("iron"))
                            .map(entry -> (ItemConvertible) entry.item()).toList(),
                    Items.IRON_NUGGET);
            disassemble(exporter,
                    WeaponsRegister.entries.stream()
                            .filter(entry -> entry.id().getPath().contains("netherite"))
                            .map(entry -> (ItemConvertible) entry.item()).toList(),
                    Items.NETHERITE_SCRAP);
        }

        private static void disassembleArmor(RecipeExporter exporter, Armor.Set armorSet, Item output) {
            FabricRecipeProvider.offerSmelting(exporter,
                    armorSet.pieces(),
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME,
                    "disassemble"
            );
            FabricRecipeProvider.offerBlasting(exporter,
                    armorSet.pieces(),
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME / 2,
                    "disassemble"
            );
        }

        private static void disassemble(RecipeExporter exporter, List<ItemConvertible> items, Item output) {
            FabricRecipeProvider.offerSmelting(exporter,
                    items,
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME,
                    "disassemble"
            );
            FabricRecipeProvider.offerBlasting(exporter,
                    items,
                    RecipeCategory.MISC,
                    output,
                    0.1f,
                    UNSMELT_TIME / 2,
                    "disassemble"
            );
        }
    }
    public static class SoundGen extends SimpleSoundGeneratorV2 {
        public SoundGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateSounds(Builder builder) {
            builder.entries.add(new Entry(MOD_ID,
                            BerserkerSounds.entries.stream()
                                    .map(entry -> SoundEntry.withVariants(entry.id().getPath(), entry.variants()))
                                    .toList()
                    )
            );
        }
    }

    public static class ModelProvider extends FabricModelProvider {
        public ModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            Armors.entries.forEach(entry -> {
                for (var piece: entry.armorSet().pieces()) {
                    itemModelGenerator.register((Item) piece, Models.GENERATED);
                }
            });
        }
    }
}
