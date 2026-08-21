package net.berserker_rpg;

import net.berserker_rpg.config.Default;
import net.berserker_rpg.spell.CustomSpellImpacts;
import net.berserker_rpg.effect.BerserkerEffects;
import net.berserker_rpg.item.BerserkerGroup;
import net.berserker_rpg.item.BerserkerItems;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.berserker_rpg.sounds.BerserkerSounds;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.berserker_rpg.config.TweaksConfig;
import net.minecraft.util.Identifier;
import net.spell_engine.rpg_series.config.ConfigFile;
import net.tiny_config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.berserker_rpg.compat.CompatLoadingCheck.armoryLoadCheck;


public class BerserkerClassMod {
	public static final String MOD_ID = "berserker_rpg";
	public static final Logger LOGGER = LoggerFactory.getLogger("berserker_rpg");

	public static ConfigManager<ConfigFile.Equipment> itemConfig = new ConfigManager<>
			("equipment_v1", Default.itemConfig)
			.builder()
			.setDirectory(MOD_ID)
			.sanitize(true)
			.build();

	public static ConfigManager<ConfigFile.Effects> effectsConfig = new ConfigManager<>
			("effects_v5", new ConfigFile.Effects())
			.builder()
			.setDirectory(MOD_ID)
			.sanitize(true)
			.build();
	public static ConfigManager<TweaksConfig> tweaksConfig = new ConfigManager<>
			("tweaks_v2", new TweaksConfig())
			.builder()
			.setDirectory(MOD_ID)
			.sanitize(true)
			.build();

	public static void init() {
		itemConfig.refresh();
		effectsConfig.refresh();
		tweaksConfig.refresh();
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			tweaksConfig.value.ignore_items_required_mods = true;
		}
		CustomSpellImpacts.registerCustomImpacts();
	}
	public static void registerItems() {
		BerserkerItems.registerModItems();
		BerserkerGroup.registerItemGroups();
		BerserkerGroup.BERSERKER = FabricItemGroup.builder()
				.icon(() -> new ItemStack(Armors.wildlingArmorSet.armorSet().head.asItem()))
				.displayName(Text.translatable("itemGroup." + MOD_ID + ".general"))
				.build();
		Registry.register(Registries.ITEM_GROUP, BerserkerGroup.BERSERKER_KEY, BerserkerGroup.BERSERKER);
		WeaponsRegister.register(itemConfig.value.weapons);
		Armors.register(itemConfig.value.armor_sets);
		if (armoryLoadCheck()) {
			FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
				ResourceManagerHelper.registerBuiltinResourcePack(
						Identifier.of(MOD_ID, "berserker_armory_compat"),
						modContainer,
						ResourcePackActivationType.ALWAYS_ENABLED
				);
			});
		}
		itemConfig.save();
		effectsConfig.save();
	}
	public static void registerSounds() {
		BerserkerSounds.register();
	}
	public static void registerEffects() {
		BerserkerEffects.register(effectsConfig.value);
		effectsConfig.save();
	}
	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}