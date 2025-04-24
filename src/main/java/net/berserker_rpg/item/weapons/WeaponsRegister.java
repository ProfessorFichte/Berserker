package net.berserker_rpg.item.weapons;

import more_rpg_loot.item.Group;
import net.berserker_rpg.BerserkerClassMod;
import net.berserker_rpg.item.BerserkerGroup;
import net.berserker_rpg.item.BerserkerItems;
import net.berserker_rpg.item.armor.Armors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.weapon.Weapon;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class WeaponsRegister {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();
    public static final ArrayList<Weapon.Entry> entriesLNE = new ArrayList<>();

    private static Weapon.Entry entry(String name, Weapon.CustomMaterial material, Item item, ItemConfig.Weapon defaults) {
        return entry(null, name, material, item, defaults);
    }

    private static Weapon.Entry entry(String requiredMod, String name, Weapon.CustomMaterial material, Item item, ItemConfig.Weapon defaults) {
        var entry = new Weapon.Entry(BerserkerClassMod.MOD_ID, name, material, item, defaults, null);
        if (entry.isRequiredModInstalled()) {
            entries.add(entry);
        }
        return entry;
    }

    private static Supplier<Ingredient> ingredient(String idString) {
        return ingredient(idString, Items.DIAMOND);
    }

    private static Supplier<Ingredient> ingredient(String idString, Item fallback) {
        var id = new Identifier(idString);
        return () -> {
            var item = Registries.ITEM.get(id);
            var ingredient = item != null ? item : fallback;
            return Ingredient.ofItems(ingredient);
        };
    }

    public static float berserker_axe_attackSpeed = -3.1f;
    private static final String LNE = "loot_n_explore";
    private static final float lneWeaponSpellPower = 3.0F;
    private static final float lneAxeAttackDamage = 13.0F;

    //BERSERKER-AXE
    private static Weapon.Entry berserker_axes(String name, Weapon.CustomMaterial material, float damage,boolean fireproof) {
        return berserker_axes(null, name, material, damage,fireproof);}
    private static Weapon.Entry berserker_axes(String requiredMod, String name, Weapon.CustomMaterial material, float damage,boolean fireproof) {
        var settings = new Item.Settings();
        if (fireproof) {
            settings = settings.fireproof();
        }
        var item = new BerserkerAxeItem(material,settings);
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(damage, berserker_axe_attackSpeed));}

    private static Weapon.Entry elder_guardian_berserker_axe(String name, Weapon.CustomMaterial material, float damage,boolean fireproof) {
        return elder_guardian_berserker_axe(null, name, material, damage, fireproof);}
    private static Weapon.Entry elder_guardian_berserker_axe(String requiredMod, String name, Weapon.CustomMaterial material, float damage, boolean fireproof ) {
        var settings = new Item.Settings().fireproof().rarity(Rarity.EPIC);
        Item item = new ElderGuardianRaidAxe(material,settings);
        ItemGroupEvents.modifyEntriesEvent(Group.RPG_LOOT_KEY).register((content) -> {
            content.add(item);
        });
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(damage, berserker_axe_attackSpeed));
    }

    private static Weapon.Entry ender_dragon_berserker_axe(String name, Weapon.CustomMaterial material, float damage,boolean fireproof) {
        return ender_dragon_berserker_axe(null, name, material, damage, fireproof);}
    private static Weapon.Entry ender_dragon_berserker_axe(String requiredMod, String name, Weapon.CustomMaterial material, float damage, boolean fireproof ) {
        var settings = new Item.Settings().fireproof().rarity(Rarity.EPIC);
        Item item = new DragonRaidAxe(material,settings);
        ItemGroupEvents.modifyEntriesEvent(Group.RPG_LOOT_KEY).register((content) -> {
            content.add(item);
        });
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(damage, berserker_axe_attackSpeed));
    }

    private static Weapon.Entry glacial_berserker_axe(String name, Weapon.CustomMaterial material, float damage,boolean fireproof) {
        return glacial_berserker_axe(null, name, material, damage, fireproof);}
    private static Weapon.Entry glacial_berserker_axe(String requiredMod, String name, Weapon.CustomMaterial material, float damage, boolean fireproof ) {
        var settings = new Item.Settings().fireproof().rarity(Rarity.EPIC);
        Item item = new GlacialRaidAxe(material,settings);
        ItemGroupEvents.modifyEntriesEvent(Group.RPG_LOOT_KEY).register((content) -> {
            content.add(item);
        });
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(damage, berserker_axe_attackSpeed));
    }

    private static Weapon.Entry wither_berserker_axe(String name, Weapon.CustomMaterial material, float damage,boolean fireproof) {
        return wither_berserker_axe(null, name, material, damage, fireproof);}
    private static Weapon.Entry wither_berserker_axe(String requiredMod, String name, Weapon.CustomMaterial material, float damage, boolean fireproof ) {
        var settings = new Item.Settings().fireproof().rarity(Rarity.EPIC);
        Item item = new WitherRaidAxe(material,settings);
        ItemGroupEvents.modifyEntriesEvent(Group.RPG_LOOT_KEY).register((content) -> {
            content.add(item);
        });
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(damage, berserker_axe_attackSpeed));
    }

    public static final Weapon.Entry flint_berserker_axe = berserker_axes("flint_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.WOOD, () -> Ingredient.ofItems(Items.FLINT)), 7.0F, false)
            ;
    public static final Weapon.Entry stone_berserker_axe = berserker_axes("stone_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.STONE, () -> Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS)), 9.0F, false)
            .attribute(ItemConfig.Attribute.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.01F))
            ;
    public static final Weapon.Entry iron_berserker_axe = berserker_axes("iron_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)), 9.0F, false)
            .attribute(ItemConfig.Attribute.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.02F))
            ;
    public static final Weapon.Entry golden_berserker_axe = berserker_axes("golden_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.GOLD, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 7.0F, false)
            .attribute(ItemConfig.Attribute.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.02F))
            ;
    public static final Weapon.Entry diamond_berserker_axe = berserker_axes("diamond_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)), 10.5F, false)
            .attribute(ItemConfig.Attribute.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.035F))
            ;
    public static final Weapon.Entry netherite_berserker_axe = berserker_axes("netherite_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)), 12.0F, true)
            .attribute(ItemConfig.Attribute.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.05F))
            ;
    public static final Weapon.Entry thunder_berserker_axe = berserker_axes("thunder_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.DIAMOND)),11.5F, true)
            .attribute(ItemConfig.Attribute.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.075F))
            .attribute(ItemConfig.Attribute.bonus(SpellSchools.LIGHTNING.id, 3))
            ;

    private static final String BETTER_END = "betterend";
    private static final String BETTER_NETHER = "betternether";

    //Registration
    public static void register(Map<String, ItemConfig.Weapon> configs) {

        if(FabricLoader.getInstance().isModLoaded(BETTER_NETHER)){
            var repair = ingredient("betternether:nether_ruby");
            berserker_axes("betternether", "ruby_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair),15.0F, true)
                    .attribute(ItemConfig.Attribute.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F));
        }
        if(FabricLoader.getInstance().isModLoaded(BETTER_END)){
            var repair = ingredient("betterend:aeternium_ingot");
            berserker_axes("betterend", "aeternium_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair),15.0F, true)
                    .attribute(ItemConfig.Attribute.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F));
        }
        if(FabricLoader.getInstance().isModLoaded(LNE)){
            var dragonRepair = ingredient("loot_n_explore:ender_dragon_scales");
            var elderGuardianRepair = ingredient("loot_n_explore:elder_guardian_eye");
            var frostMonarchRepair = ingredient("loot_n_explore:frozen_soul");
            var witherRepair = ingredient("minecraft:nether_star");
           ender_dragon_berserker_axe(LNE, "ender_dragon_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, dragonRepair),lneAxeAttackDamage, true)
                    .attribute(ItemConfig.Attribute.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.08F))
                    .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, lneWeaponSpellPower));
           elder_guardian_berserker_axe(LNE, "elder_guardian_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, elderGuardianRepair),lneAxeAttackDamage, true)
                    .attribute(ItemConfig.Attribute.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.08F))
                    .attribute(ItemConfig.Attribute.bonus(MoreSpellSchools.WATER.id, lneWeaponSpellPower));
           wither_berserker_axe(LNE, "soul_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, witherRepair),lneAxeAttackDamage, true)
                    .attribute(ItemConfig.Attribute.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.08F))
                    .attribute(ItemConfig.Attribute.bonus(SpellSchools.ARCANE.id, lneWeaponSpellPower));
           glacial_berserker_axe(LNE, "frozen_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, frostMonarchRepair),lneAxeAttackDamage, true)
                    .attribute(ItemConfig.Attribute.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.08F))
                    .attribute(ItemConfig.Attribute.bonus(SpellSchools.FROST.id, lneWeaponSpellPower));



        }
        Weapon.register(configs, entries, BerserkerGroup.BERSERKER_KEY);
    }
}
