package net.berserker_rpg;

import net.berserker_rpg.datagen.*;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.tag.ModItemTags;
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
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
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
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.registry.SpellRegistry;
import net.spell_engine.api.tags.SpellTags;
import net.spell_engine.rpg_series.item.Armor;
import net.spell_engine.rpg_series.item.Weapon;
import net.spell_engine.api.tags.SpellEngineItemTags;
import net.spell_engine.rpg_series.datagen.RPGSeriesDataGen;
import net.spell_engine.rpg_series.tags.RPGSeriesItemTags;
import net.spell_power.api.SpellPowerTags;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class BerserkerDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        BerserkerVanillaAdvancementProvider.init();
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ItemTagGenerator::new);
        pack.addProvider(UnsmeltGenerator::new);
        pack.addProvider(SpellGen::new);
        pack.addProvider(SpellTagGenerator::new);
        pack.addProvider(SoundGen::new);
        pack.addProvider(ModelProvider::new);
        pack.addProvider(LangGenerator::new);
        pack.addProvider(WeaponAttributesGenerator::new);
        pack.addProvider(BerserkerRecipeProvider::new);
        pack.addProvider(BerserkerSmithingRecipeProvider::new);
        pack.addProvider(BerserkerConditionalRecipeProvider::new);
        pack.addProvider(BerserkerVanillaAdvancementProvider::new);
        pack.addProvider(BerserkerAdvancementDataGen::new);
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
        public void generateBerserkerAxeTags(List<Weapon.Entry> weapons) {
            Iterator var2 = weapons.iterator();

            while(var2.hasNext()) {
                Weapon.Entry weapon = (Weapon.Entry)var2.next();
                FabricTagProvider<Item>.FabricTagBuilder tag = this.getOrCreateTagBuilder(ModItemTags.BERSERKER_AXES);
                tag.addOptional(weapon.id());
                int tier = weapon.lootProperties().tier();
                if (tier >= 0) {
                    FabricTagProvider<Item>.FabricTagBuilder tierTag = this.getOrCreateTagBuilder(RPGSeriesItemTags.LootTiers.get(tier, RPGSeriesItemTags.LootCategory.WEAPONS));
                    tierTag.addOptional(weapon.id());
                }
                String lootTheme = weapon.lootProperties().theme();
                if (lootTheme != null && !lootTheme.isEmpty()) {
                    FabricTagProvider<Item>.FabricTagBuilder themeTag = this.getOrCreateTagBuilder(RPGSeriesItemTags.LootThemes.get(lootTheme));
                    themeTag.addOptional(weapon.id());
                }
            }
        }
        public void generateGeneralWeaponTags(List<Weapon.Entry> weapons) {
            Iterator var2 = weapons.iterator();

            while(var2.hasNext()) {
                Weapon.Entry weapon = (Weapon.Entry)var2.next();
                TagKey<Item> weaponType = RPGSeriesItemTags.WeaponType.get(weapon.category());
                FabricTagProvider<Item>.FabricTagBuilder weaponTag = this.getOrCreateTagBuilder(weaponType);
                weaponTag.addOptional(weapon.id());
                int tier = weapon.lootProperties().tier();
                if (tier >= 0) {
                    FabricTagProvider<Item>.FabricTagBuilder tierTag = this.getOrCreateTagBuilder(RPGSeriesItemTags.LootTiers.get(tier, RPGSeriesItemTags.LootCategory.WEAPONS));
                    tierTag.addOptional(weapon.id());
                }

                String lootTheme = weapon.lootProperties().theme();
                if (lootTheme != null && !lootTheme.isEmpty()) {
                    FabricTagProvider<Item>.FabricTagBuilder themeTag = this.getOrCreateTagBuilder(RPGSeriesItemTags.LootThemes.get(lootTheme));
                    themeTag.addOptional(weapon.id());
                }
            }

        }
        List<String> armoryKeywords = List.of("warlord");
        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            generateBerserkerAxeTags(WeaponsRegister.entries.stream().filter(entry -> entry.name().contains("berserker_axe")).toList());
            generateGeneralWeaponTags(WeaponsRegister.entries.stream().filter(entry -> !entry.name().contains("berserker_axe")).toList());
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

            var spellInfinityTag = getOrCreateTagBuilder(SpellEngineItemTags.ENCHANTABLE_SPELL_INFINITY);
            spellInfinityTag.addTag(ModItemTags.BERSERKER_AXES);
            var spellPowerTag  = getOrCreateTagBuilder(SpellPowerTags.Items.Enchantable.SPELL_POWER_GENERIC);
            spellPowerTag .addTag(ModItemTags.BERSERKER_AXES);
            var unbreakingTag = getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE);
            unbreakingTag.addTag(ModItemTags.BERSERKER_AXES);
            var sharpnessTag = getOrCreateTagBuilder(ItemTags.SHARP_WEAPON_ENCHANTABLE);
            sharpnessTag.addTag(ModItemTags.BERSERKER_AXES);
            var meleeTag = getOrCreateTagBuilder(ItemTags.SWORDS);
            meleeTag.addTag(ModItemTags.BERSERKER_AXES);
            var rpgSeriesMeleeWeaponTag = getOrCreateTagBuilder(RPGSeriesItemTags.Archetype.tag(RPGSeriesItemTags.RoleArchetype.MELEE_DAMAGE));
            rpgSeriesMeleeWeaponTag.addTag(ModItemTags.BERSERKER_AXES);
        }
    }

    public static class UnsmeltGenerator extends FabricRecipeProvider {
        public UnsmeltGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        public static int UNSMELT_TIME = 300;

        @Override
        public void generate(RecipeExporter exporter) {
            disassembleArmor(exporter, Armors.wildlingArmorSet.armorSet(), Items.LEATHER);
            disassembleArmor(exporter, Armors.northlingArmorSet.armorSet(), Items.IRON_NUGGET);
            disassembleArmor(exporter, Armors.netheriteNorthlingArmorSet.armorSet(), Items.NETHERITE_SCRAP);

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

        // Custom model with parent for raid axes
        private static final Model RAID_AXE_MODEL = new Model(
                Optional.of(Identifier.of(MOD_ID, "item/raid_axe_model")),
                Optional.empty(),
                TextureKey.LAYER0
        );

        // Handheld model for swords
        private static final Model HANDHELD_MODEL = new Model(
                Optional.of(Identifier.ofVanilla("item/handheld")),
                Optional.empty(),
                TextureKey.LAYER0
        );

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            // Armors
            Armors.entries.forEach(entry -> {
                for (var piece: entry.armorSet().pieces()) {
                    itemModelGenerator.register((Item) piece, Models.GENERATED);
                }
            });

            // Weapons with custom parent
            WeaponsRegister.entries.forEach(entry -> {
                if (entry.item() != null) {
                    if (entry.name().contains("berserker_axe")) {
                        itemModelGenerator.register(entry.item(), RAID_AXE_MODEL);
                    } else if (entry.name().contains("sword")) {
                        itemModelGenerator.register(entry.item(), HANDHELD_MODEL);
                    }
                }
            });
        }
    }
    public static class SpellTagGenerator extends FabricTagProvider<Spell> {
        public SpellTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, SpellRegistry.KEY, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            var namespace = MOD_ID;
            var treasureTagBuilder = getOrCreateTagBuilder(SpellTags.TREASURE);
            var processedBooks = new HashSet<BerserkerSpells.Book>();
            BerserkerSpells.entries.forEach(entry -> {
                if (entry.book() != null) {
                    var bookTagKey = SpellTags.spellBook(namespace, entry.book().toString().toLowerCase());
                    var bookTag = getOrCreateTagBuilder(bookTagKey);
                    bookTag.addOptional(entry.id());
                    var scrollTagKey = SpellTags.spellScroll(namespace, entry.book().toString().toLowerCase());
                    var scrollTag = getOrCreateTagBuilder(scrollTagKey);
                    scrollTag.addOptional(entry.id());
                    if (processedBooks.add(entry.book())) {
                        treasureTagBuilder.addOptionalTag(scrollTagKey);
                    }
                }
            });
        }
    }
}
