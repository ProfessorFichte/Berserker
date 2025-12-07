package net.berserker_rpg.item.weapons;

import net.berserker_rpg.BerserkerClassMod;
import net.berserker_rpg.item.BerserkerGroup;
import net.berserker_rpg.spell.BerserkerSpells;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.more_rpg_classes.custom.MoreSpellSchools;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.config.WeaponConfig;
import net.spell_engine.api.item.Equipment;
import net.spell_engine.api.item.weapon.SpellSwordItem;
import net.spell_engine.api.item.weapon.Weapon;
import net.spell_power.api.SpellSchools;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class WeaponsRegister {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();
    private static Weapon.Entry entry(String name, Weapon.CustomMaterial material, Weapon.Factory factory, WeaponConfig defaults, Equipment.WeaponType weaponType) {
        var entry = new Weapon.Entry(MOD_ID, name, material, factory, defaults, weaponType);
        entry.castSpell();
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
        return entry(name, material, BerserkerAxeItem::new, new WeaponConfig(damage, berserker_axe_attackSpeed), Equipment.WeaponType.DOUBLE_AXE);
    }
    private static Weapon.Entry sword(String name, Weapon.CustomMaterial material, float damage) {
        return entry(name, material, SpellSwordItem::new, new WeaponConfig(damage, -2.4f), Equipment.WeaponType.SWORD);
    }

    public static final Weapon.Entry flint_berserker_axe = berserker_axes("flint_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.WOOD, () -> Ingredient.ofItems(Items.FLINT)), 7.0F)
            .loot(Equipment.LootProperties.of(0));
    public static final Weapon.Entry stone_berserker_axe = berserker_axes("stone_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.STONE, () -> Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS)), 9.0F)
            .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.01F))
            .loot(Equipment.LootProperties.of(0));
    public static final Weapon.Entry iron_berserker_axe = berserker_axes("iron_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)), 9.0F)
            .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.02F))
            .loot(Equipment.LootProperties.of(1));
    public static final Weapon.Entry golden_berserker_axe = berserker_axes("golden_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.GOLD, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 7.0F)
            .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.02F))
            .loot(Equipment.LootProperties.of("golden_weapon"));
    public static final Weapon.Entry diamond_berserker_axe = berserker_axes("diamond_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)), 10.5F)
            .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.035F))
            .loot(Equipment.LootProperties.of(2));
    public static final Weapon.Entry netherite_berserker_axe = berserker_axes("netherite_berserker_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)), 12.0F)
            .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.05F))
            .loot(Equipment.LootProperties.of(3));

    private static final String BETTER_END = "betterend";
    private static final String BETTER_NETHER = "betternether";
    private static final String AETHER = "aether";
    private static final String ARSENAL = "arsenal";
    private static final String LNE = "loot_n_explore";
    private static final float lneWeaponSpellPower = 2.0F;
    private static final float lneAxeAttackDamage = 15.0F;
    ///PASSIVE SPELLS
    public static Identifier dragonclaw = Identifier.of("loot_n_explore", "dragonclaw");
    public static Identifier avalanche = Identifier.of("loot_n_explore", "avalanche");
    public static Identifier waterbomb = Identifier.of("loot_n_explore", "waterbomb");
    public static Identifier wither_pulse = Identifier.of("loot_n_explore", "wither_pulse");
    //Registration
    public static void register(Map<String, WeaponConfig> configs) {
        if(FabricLoader.getInstance().isModLoaded(BETTER_NETHER) || BerserkerClassMod.tweaksConfig.value.ignore_items_required_mods){
            var repair = ingredient("betternether:nether_ruby", FabricLoader.getInstance().isModLoaded(BETTER_NETHER), Items.NETHERITE_INGOT);
            berserker_axes("ruby_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair),15.0F)
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .loot(Equipment.LootProperties.of(4));
        }
        if(FabricLoader.getInstance().isModLoaded(BETTER_END) || BerserkerClassMod.tweaksConfig.value.ignore_items_required_mods){
            var repair = ingredient("betterend:aeternium_ingot", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            berserker_axes("aeternium_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair),15.0F)
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .loot(Equipment.LootProperties.of(4));
        }
        if (FabricLoader.getInstance().isModLoaded(AETHER) || BerserkerClassMod.tweaksConfig.value.ignore_items_required_mods) {
            var repair = ingredient("aether:ambrosium_shard", FabricLoader.getInstance().isModLoaded(AETHER), Items.NETHERITE_INGOT);
            berserker_axes("aether_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair),15.0F)
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .loot(Equipment.LootProperties.of("aether"));
        }
        if(FabricLoader.getInstance().isModLoaded(LNE)|| BerserkerClassMod.tweaksConfig.value.ignore_items_required_mods){
            berserker_axes( "ender_dragon_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.AMETHYST_SHARD)),lneAxeAttackDamage)
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .attribute(AttributeModifier.bonus(SpellSchools.ARCANE.id, lneWeaponSpellPower))
                    .spell(dragonclaw)
                    .rarity = Rarity.RARE;
            berserker_axes( "elder_guardian_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.PRISMARINE_SHARD)),lneAxeAttackDamage)
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .attribute(AttributeModifier.bonus(MoreSpellSchools.WATER.id, lneWeaponSpellPower))
                    .spell(waterbomb)
                    .rarity = Rarity.RARE;
            berserker_axes( "wither_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.BONE)),lneAxeAttackDamage)
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .attribute(AttributeModifier.bonus(SpellSchools.SOUL.id, lneWeaponSpellPower))
                    .spell(wither_pulse)
                    .rarity = Rarity.RARE;
            berserker_axes( "glacial_berserker_axe",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.ICE)),lneAxeAttackDamage)
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .attribute(AttributeModifier.bonus(SpellSchools.FROST.id, lneWeaponSpellPower))
                    .spell(avalanche)
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
            berserker_axes( "unique_berserker_axe_1",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.IRON_BLOCK)),lneAxeAttackDamage)
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .spell(BerserkerSpells.carve_melee.id())
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            berserker_axes( "unique_berserker_axe_2",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.IRON_BLOCK)),lneAxeAttackDamage)
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.10F))
                    .spell(BerserkerSpells.lightning_strike.id())
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
            sword( "unique_sword_1",
                    Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.IRON_BLOCK)),8.0F)
                    .attribute(AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),0.05F))
                    .spell(BerserkerSpells.carve_melee.id())
                    .loot(Equipment.LootProperties.of(5))
                    .rarity = Rarity.RARE;
        }
        Weapon.register(configs, entries, BerserkerGroup.BERSERKER_KEY);
    }
}
