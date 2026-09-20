package net.berserker_rpg.item.armor;

import net.berserker_rpg.item.BerserkerGroup;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.more_rpg_classes.item.MRPGCItemGroups;
import net.more_rpg_classes.item.MRPGCItems;
import net.spell_engine.rpg_series.config.ArmorSetConfig;
import net.spell_engine.rpg_series.config.AttributeModifier;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.rpg_series.item.Armor;
import net.spell_engine.api.item.SpellItemData;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class Armors {
    private static final Supplier<Ingredient> WILDLING_INGREDIENTS = () -> Ingredient.ofItems(
            Items.LEATHER, Items.CHAIN, MRPGCItems.WOLF_FUR
    );
    private static final Supplier<Ingredient> NORTHLING_INGREDIENTS = () -> Ingredient.ofItems(
            Items.IRON_INGOT, Items.CHAIN, MRPGCItems.POLAR_BEAR_FUR
    );

    public static Identifier warlord_passive = new Identifier(MOD_ID, "warlord");

    private static Armor.ItemSettingsTweaker commonSettings(Identifier equipmentSetId) {
        return Armor.ItemSettingsTweaker.standard(itemSettings -> {
            itemSettings.rarity(Rarity.RARE);
            SpellItemData.defaults(itemSettings).equipmentSet(equipmentSetId);
        });
    }

    private static final String CRIT_MOD_ID = "critical_strike";
    private static final Identifier CRIT_CHANCE_ID = new Identifier(CRIT_MOD_ID, "chance");
    private static final Identifier CRIT_DAMAGE_ID = new Identifier(CRIT_MOD_ID, "damage");

    public static final float berserker_atkspeed_T1 = 0.02F;
    public static final float berserker_rage_T1 = 0.025F;
    public static final float berserker_atkspeed_T2 = 0.02F;
    public static final float berserker_rage_T2 = 0.05F;
    public static final float berserker_atkdamage_T2 = 0.04F;
    public static final float berserker_t2_crit_damage = 0.04F;
    public static final float berserker_atkspeed_T3 = 0.02F;
    public static final float berserker_rage_T3 = 0.075F;
    public static final float berserker_atkdamage_T3 = 0.05F;
    public static final float berserker_t3_crit_damage = 0.05F;
    public static final float warlord_atkspeed = 0.025F;
    public static final float warlord_rage = 0.075F;
    public static final float warlord_atkdamage = 0.06F;
    private static final float warlord_crit_damage = 0.08F;


    public static ArmorMaterial material(String name,
                                         int protectionHead, int protectionChest, int protectionLegs, int protectionFeet,
                                         int enchantability, SoundEvent equipSound, Supplier<Ingredient> repairIngredient) {
        return Armor.material(
                new Identifier(MOD_ID, name),
                Map.of(
                        ArmorItem.Type.HELMET, protectionHead,
                        ArmorItem.Type.CHESTPLATE, protectionChest,
                        ArmorItem.Type.LEGGINGS, protectionLegs,
                        ArmorItem.Type.BOOTS, protectionFeet),
                enchantability, equipSound, repairIngredient,
                0, 0
        );
    }

    public static ArmorMaterial material_wildling = material(
            "wildling",
            1, 3, 3, 1,
            9,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, WILDLING_INGREDIENTS);

    public static ArmorMaterial material_northling = material(
            "northling",
            2, 4, 4, 2,
            11,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, NORTHLING_INGREDIENTS);
    public static ArmorMaterial material_netherite_northling = material(
            "netherite_northling",
            2, 4, 4, 2,
            20,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });
    public static ArmorMaterial material_warlord = material(
            "warlord",
            2, 4, 4, 2,
            20,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    private static Armor.Entry create(ArmorMaterial material, Identifier id, int durability,
                                      Armor.Set.ItemFactory factory, ArmorSetConfig defaults, int tier, Armor.ItemSettingsTweaker settings) {
        var entry = Armor.Entry.create(
                material,
                id,
                durability,
                factory,
                defaults,
                Equipment.LootProperties.of(tier),
                settings
        );
        entries.add(entry);
        return entry;
    }

    private static final Map<Armor.Entry, RegistryKey<ItemGroup>> groupOverrides = new IdentityHashMap<>();

    private static Armor.Entry groupKey(Armor.Entry entry, RegistryKey<ItemGroup> key) {
        groupOverrides.put(entry, key);
        return entry;
    }

    public static final Armor.Entry wildlingArmorSet =
            create(
                    material_wildling,
                    new Identifier(MOD_ID, "wildling"),
                    15,
                    WildlingArmor::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),berserker_atkspeed_T1),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T1 )
                                    )),
                            new ArmorSetConfig.Piece(3)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),berserker_atkspeed_T1),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T1 )
                                    )),
                            new ArmorSetConfig.Piece(3)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),berserker_atkspeed_T1),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T1 )
                                    )),
                            new ArmorSetConfig.Piece(1)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),berserker_atkspeed_T1),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T1 )
                                    ))
                    ),1,null)
                    .translatedName("Wildling Head", "Wildling Suit", "Wildling Legs", "Wildling Boots");

    public static final Armor.Entry northlingArmorSet =
            create(
                    material_northling,
                    new Identifier(MOD_ID, "northling"),
                    25,
                    NorthlingArmor::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(2)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),berserker_atkspeed_T2),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T2 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T2)
                                    )).addConditional(CRIT_MOD_ID, List.of(
                                            AttributeModifier.multiply(CRIT_DAMAGE_ID,berserker_t2_crit_damage),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T2 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T2)
                                    )),
                            new ArmorSetConfig.Piece(4)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),berserker_atkspeed_T2),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T2 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T2)
                                    )).addConditional(CRIT_MOD_ID, List.of(
                                            AttributeModifier.multiply(CRIT_DAMAGE_ID,berserker_t2_crit_damage),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T2 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T2)
                                    )),
                            new ArmorSetConfig.Piece(4)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),berserker_atkspeed_T2),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T2 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T2)
                                    )).addConditional(CRIT_MOD_ID, List.of(
                                            AttributeModifier.multiply(CRIT_DAMAGE_ID,berserker_t2_crit_damage),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T2 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T2)
                                    )),
                            new ArmorSetConfig.Piece(2)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),berserker_atkspeed_T2),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T2 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T2)
                                    )).addConditional(CRIT_MOD_ID, List.of(
                                            AttributeModifier.multiply(CRIT_DAMAGE_ID,berserker_t2_crit_damage),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T2 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T2)
                                    ))
                    ),2,null)
                    .translatedName("Northling Head", "Northling Suit", "Northling Pants", "Northling Boots");

    public static final Armor.Entry netheriteNorthlingArmorSet =
            create(
                    material_netherite_northling,
                    new Identifier(MOD_ID, "netherite_northling"),
                    30,
                    NorthlingArmor::new,
                    ArmorSetConfig.with(
                            new ArmorSetConfig.Piece(2)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),berserker_atkspeed_T3),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T3 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T3)
                                    )).addConditional(CRIT_MOD_ID, List.of(
                                            AttributeModifier.multiply(CRIT_DAMAGE_ID,berserker_t3_crit_damage),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T3 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T3)
                                    )),
                            new ArmorSetConfig.Piece(4)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),berserker_atkspeed_T3),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T3 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T3)
                                    )).addConditional(CRIT_MOD_ID, List.of(
                                            AttributeModifier.multiply(CRIT_DAMAGE_ID,berserker_t3_crit_damage),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T3 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T3)
                                    )),
                            new ArmorSetConfig.Piece(4)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),berserker_atkspeed_T3),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T3 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T3)
                                    )).addConditional(CRIT_MOD_ID, List.of(
                                            AttributeModifier.multiply(CRIT_DAMAGE_ID,berserker_t3_crit_damage),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T3 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T3)
                                    )),
                            new ArmorSetConfig.Piece(2)
                                    .addAll(List.of(
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),berserker_atkspeed_T3),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T3 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T3)
                                    )).addConditional(CRIT_MOD_ID, List.of(
                                            AttributeModifier.multiply(CRIT_DAMAGE_ID,berserker_t3_crit_damage),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),berserker_rage_T3 ),
                                            AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),berserker_atkdamage_T3)
                                    ))
                    ),3,null)
                    .translatedName("Netherite Northling Head", "Netherite Northling Suit", "Netherite Northling Pants", "Netherite Northling Boots");

    public static Armor.Entry warlordArmorSet;

    private static boolean conditionalEntriesCreated = false;

    private static void createConditionalEntries() {
        if (conditionalEntriesCreated) { return; }
        conditionalEntriesCreated = true;
        warlordArmorSet = groupKey(create(
                material_warlord,
                new Identifier(MOD_ID, "warlord"),
                40,
                Armor.CustomItem::new,
                ArmorSetConfig.with(
                        new ArmorSetConfig.Piece(2)
                                .addAll(List.of(
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),warlord_atkspeed),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),warlord_rage ),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),warlord_atkdamage)
                                )).addConditional(CRIT_MOD_ID, List.of(
                                        AttributeModifier.multiply(CRIT_DAMAGE_ID,warlord_crit_damage),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),warlord_rage ),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),warlord_atkdamage)
                                )),
                        new ArmorSetConfig.Piece(4)
                                .addAll(List.of(
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),warlord_atkspeed),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),warlord_rage ),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),warlord_atkdamage)
                                )).addConditional(CRIT_MOD_ID, List.of(
                                        AttributeModifier.multiply(CRIT_DAMAGE_ID,warlord_crit_damage),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),warlord_rage ),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),warlord_atkdamage)
                                )),
                        new ArmorSetConfig.Piece(4)
                                .addAll(List.of(
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),warlord_atkspeed),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),warlord_rage ),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),warlord_atkdamage)
                                )).addConditional(CRIT_MOD_ID, List.of(
                                        AttributeModifier.multiply(CRIT_DAMAGE_ID,warlord_crit_damage),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),warlord_rage ),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),warlord_atkdamage)
                                )),
                        new ArmorSetConfig.Piece(2)
                                .addAll(List.of(
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_speed")),warlord_atkspeed),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),warlord_rage ),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),warlord_atkdamage)
                                )).addConditional(CRIT_MOD_ID, List.of(
                                        AttributeModifier.multiply(CRIT_DAMAGE_ID,warlord_crit_damage),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("more_rpg_classes:rage_modifier")),warlord_rage ),
                                        AttributeModifier.multiply(Objects.requireNonNull(Identifier.tryParse("minecraft:generic.attack_damage")),warlord_atkdamage)
                                ))
                ),5,
                commonSettings(warlord_passive)
        ).translatedName("Norse Warlord Head", "Norse Warlord Suit", "Norse Warlord Pants", "Norse Warlord Boots"), MRPGCItemGroups.ARMORY_KEY);
    }

    public static void register(Map<String, ArmorSetConfig> configs) {
        createConditionalEntries();
        Armor.register(configs, entries, BerserkerGroup.BERSERKER_KEY);
    }

    public static Map<Identifier, Item> itemsToRegister(Map<String, ArmorSetConfig> configs) {
        createConditionalEntries();
        return Armor.itemsToRegister(configs, entries, BerserkerGroup.BERSERKER_KEY);
    }

    /// Sets pushed into a different creative tab (e.g. Armory RPGs compat) still land in the
    /// default {@link BerserkerGroup#BERSERKER_KEY} tab first, since that's the tab passed to
    /// {@link Armor#register}. Each platform has to pull them back out and add them to the real
    /// target tab itself, since Fabric's item group content API and NeoForge's aren't portable
    /// through a shared interface.
    public static void forEachGroupOverride(GroupOverrideConsumer consumer) {
        for (var override : groupOverrides.entrySet()) {
            consumer.accept(override.getKey().armorSet().pieces(), override.getValue());
        }
    }

    @FunctionalInterface
    public interface GroupOverrideConsumer {
        void accept(List<?> pieces, RegistryKey<ItemGroup> targetGroup);
    }
}