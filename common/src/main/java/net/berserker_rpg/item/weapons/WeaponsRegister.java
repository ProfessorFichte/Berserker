package net.berserker_rpg.item.weapons;

import net.berserker_rpg.BerserkerClassMod;
import net.berserker_rpg.item.BerserkerGroup;
import net.berserker_rpg.spell.BerserkerSpells;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.more_rpg_classes.custom.MrpgLibSpells;
import net.more_rpg_classes.item.MRPGCItemGroups;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.WeaponConfig;
import net.spell_engine.api.item.weapon.SpellSwordItem;
import net.spell_engine.api.spell.container.SpellContainers;
import net.spell_engine.rpg_series.datagen.WeaponSkills;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.rpg_series.item.Weapon;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class WeaponsRegister {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    private static final Map<Weapon.Entry, RegistryKey<ItemGroup>> groupOverrides = new IdentityHashMap<>();

    private static Weapon.Entry groupKey(Weapon.Entry entry, RegistryKey<ItemGroup> key) {
        groupOverrides.put(entry, key);
        return entry;
    }

    private static Weapon.Entry entry(String name, Weapon.CustomMaterial material, Weapon.Factory factory, WeaponConfig defaults, Equipment.WeaponType weaponType) {
        var entry = new Weapon.Entry(MOD_ID, name, material, factory, defaults, weaponType);
        entry.spellContainer(SpellContainers.forMagicWeapon());
        entries.add(entry);
        return entry;
    }

    private static Supplier<Ingredient> ingredient(String idString, boolean requirement, Item fallback) {
        var id = Identifier.of(idString);
        if (requirement) {
            return () -> {
                return Ingredient.ofItems(fallback);
            };
        } else {
            return () -> {
                var item = Registries.ITEM.get(id);
                var ingredient = item != null ? item : fallback;
                return Ingredient.ofItems(ingredient);
            };
        }
    }

    public static float berserker_axe_attackSpeed = -3.1f;

    //BERSERKER-AXE
    private static Weapon.Entry berserker_axes(String name, Weapon.CustomMaterial material, float damage) {
        return entry(name, material, BerserkerAxeItem::new, new WeaponConfig(damage, berserker_axe_attackSpeed), Equipment.WeaponType.DOUBLE_AXE)
                .spellContainer(SpellContainers.forMeleeWeapon().withSpellId(MrpgLibSpells.decapitate.id()));
    }
    private static Weapon.Entry sword(String name, Weapon.CustomMaterial material, float damage) {
        return entry(name, material, SpellSwordItem::new, new WeaponConfig(damage, -2.4f), Equipment.WeaponType.SWORD)
                .spellContainer(SpellContainers.forMeleeWeapon().withSpellId(WeaponSkills.SWIFT_STRIKES.id()));
    }

    public static final Weapon.Entry flint_berserker_axe = berserker_axes("flint_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.WOOD, () -> Ingredient.ofItems(Items.FLINT)), 7.0F)
            .translatedName("Flint Raid Axe")
            .loot(Equipment.LootProperties.of(0));
    public static final Weapon.Entry stone_berserker_axe = berserker_axes("stone_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.STONE, () -> Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS)), 9.0F)
            .translatedName("Stone Raid Axe")
            .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.01F))
            .loot(Equipment.LootProperties.of(0));
    public static final Weapon.Entry iron_berserker_axe = berserker_axes("iron_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)), 9.0F)
            .translatedName("Iron Raid Axe")
            .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.02F))
            .loot(Equipment.LootProperties.of(1));
    public static final Weapon.Entry golden_berserker_axe = berserker_axes("golden_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.GOLD, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 7.0F)
            .translatedName("Gold Raid Axe")
            .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.02F))
            .loot(Equipment.LootProperties.of("golden_weapon"));
    public static final Weapon.Entry diamond_berserker_axe = berserker_axes("diamond_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)), 10.5F)
            .translatedName("Diamond Raid Axe")
            .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.035F))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netherite_berserker_axe = berserker_axes("netherite_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)), 12.0F)
            .translatedName("Netherite Raid Axe")
            .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.05F))
            .loot(Equipment.LootProperties.of(3));

    private static final String BETTER_END = "betterend";
    private static final String BETTER_NETHER = "betternether";
    private static final String AETHER = "aether";
    private static final String ARSENAL = "arsenal";
    private static final String LNE = "loot_n_explore";
    private static final float lneWeaponSpellPower = 2.0F;
    private static final float lneAxeAttackDamage = 15.0F;
    //Registration
    public static void register(Map<String, WeaponConfig> configs) {
        if(FabricLoader.getInstance().isModLoaded(BETTER_NETHER) || BerserkerClassMod.tweaksConfig.value.ignore_items_required_mods){
            var repair = ingredient("betternether:nether_ruby", FabricLoader.getInstance().isModLoaded(BETTER_NETHER), Items.NETHERITE_INGOT);
            berserker_axes("ruby_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair),15.0F)
                    .translatedName("Ruby Raid Axe")
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .loot(Equipment.LootProperties.of(4));
        }
        if(FabricLoader.getInstance().isModLoaded(BETTER_END) || BerserkerClassMod.tweaksConfig.value.ignore_items_required_mods){
            var repair = ingredient("betterend:aeternium_ingot", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            berserker_axes("aeternium_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair),15.0F)
                    .translatedName("Aeternium Raid Axe")
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .loot(Equipment.LootProperties.of(4));
        }
        if (FabricLoader.getInstance().isModLoaded(AETHER) || BerserkerClassMod.tweaksConfig.value.ignore_items_required_mods) {
            var repair = ingredient("aether:ambrosium_shard", FabricLoader.getInstance().isModLoaded(AETHER), Items.NETHERITE_INGOT);
            berserker_axes("aether_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair),15.0F)
                    .translatedName("Holy Raid Axe")
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .loot(Equipment.LootProperties.of("aether"));
        }
        if(FabricLoader.getInstance().isModLoaded(LNE)|| BerserkerClassMod.tweaksConfig.value.ignore_items_required_mods){
            berserker_axes( "ender_dragon_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.AMETHYST_SHARD)),lneAxeAttackDamage)
                    .translatedName("Dragons Conquest")
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, lneWeaponSpellPower))
                    .withAdditionalSpell("loot_n_explore:dragonclaw")
                    .rarity = Rarity.RARE;
            berserker_axes( "elder_guardian_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.PRISMARINE_SHARD)),lneAxeAttackDamage)
                    .translatedName("Sunken Captain")
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, lneWeaponSpellPower))
                    .withAdditionalSpell("loot_n_explore:waterbomb")
                    .rarity = Rarity.RARE;
            berserker_axes( "wither_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.BONE)),lneAxeAttackDamage)
                    .translatedName("Soul Ripper")
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .attribute(AttributeModifier.bonus(SpellSchools.SOUL.id, lneWeaponSpellPower))
                    .withAdditionalSpell("loot_n_explore:wither_pulse")
                    .rarity = Rarity.RARE;
            berserker_axes( "glacial_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.ICE)),lneAxeAttackDamage)
                    .translatedName("Norse Raid Axe")
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, lneWeaponSpellPower))
                    .withAdditionalSpell("loot_n_explore:avalanche")
                    .rarity = Rarity.RARE;
// ADD THIS IN LNE OR BERSERKER LNE MOD
            /*
            Identifier itemIdG = Identifier.of("loot_n_explore", "elder_guardian_axe");
            Identifier itemId0 = Identifier.of("berserker_rpg", "ender_dragon_berserker_axe");
            Identifier itemId1 = Identifier.of("berserker_rpg", "glacial_berserker_axe");
            Identifier itemId2 = Identifier.of("berserker_rpg", "wither_berserker_axe");
            Identifier itemId3 = Identifier.of("berserker_rpg", "elder_guardian_berserker_axe");


            ItemGroupEvents.modifyEntriesEvent(Group.RPG_LOOT_KEY).register((content) -> {
                content.addAfter(Registries.ITEM.get(itemIdG),Registries.ITEM.get(itemId0));
                content.addAfter(Registries.ITEM.get(itemIdG),Registries.ITEM.get(itemId1));
                content.addAfter(Registries.ITEM.get(itemIdG),Registries.ITEM.get(itemId2));
                content.addAfter(Registries.ITEM.get(itemIdG),Registries.ITEM.get(itemId3));
            });
            */
        }
        if (FabricLoader.getInstance().isModLoaded(ARSENAL) || BerserkerClassMod.tweaksConfig.value.ignore_items_required_mods) {
            var uniqueBerserkerAxe1 = groupKey(berserker_axes( "unique_berserker_axe_1",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.IRON_BLOCK)),lneAxeAttackDamage)
                    .translatedName("Black Cleaver")
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .withAdditionalSpell(MrpgLibSpells.carve_melee.id().toString())
                    .loot(Equipment.LootProperties.of(5)), MRPGCItemGroups.ARSENAL_KEY);
            uniqueBerserkerAxe1.rarity = Rarity.RARE;
            var uniqueBerserkerAxe2 = groupKey(berserker_axes( "unique_berserker_axe_2",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.IRON_BLOCK)),lneAxeAttackDamage)
                    .translatedName("Torans's Axe")
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .withAdditionalSpell(MrpgLibSpells.lightning_strike_melee.id().toString())
                    .loot(Equipment.LootProperties.of(5)), MRPGCItemGroups.ARSENAL_KEY);
            uniqueBerserkerAxe2.rarity = Rarity.RARE;
            var uniqueSword1 = groupKey(sword( "unique_sword_1",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.IRON_BLOCK)),8.0F)
                    .translatedName("Skofnung")
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.05F))
                    .withAdditionalSpell(MrpgLibSpells.carve_melee.id().toString())
                    .loot(Equipment.LootProperties.of(5)), MRPGCItemGroups.ARSENAL_KEY);
            uniqueSword1.rarity = Rarity.RARE;
        }
        Weapon.register(configs, entries, BerserkerGroup.BERSERKER_KEY);
        for (var override : groupOverrides.entrySet()) {
            var entry = override.getKey();
            var key = override.getValue();
            ItemGroupEvents.modifyEntriesEvent(BerserkerGroup.BERSERKER_KEY).register(content -> {
                content.getDisplayStacks().removeIf(stack -> stack.isOf(entry.item()));
                content.getSearchTabStacks().removeIf(stack -> stack.isOf(entry.item()));
            });
            ItemGroupEvents.modifyEntriesEvent(key).register(content -> content.add(entry.item()));
        }
    }
}
