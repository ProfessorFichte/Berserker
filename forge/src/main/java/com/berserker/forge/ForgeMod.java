package com.berserker.forge;

import net.berserker_rpg.BerserkerClassMod;
import net.berserker_rpg.effect.BerserkerEffects;
import net.berserker_rpg.item.BerserkerGroup;
import net.berserker_rpg.item.BerserkerItems;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.berserker_rpg.sounds.BerserkerSounds;
import net.spell_engine.api.effect.Effects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;

import com.berserker.forge.client.ForgeClient;

@Mod(BerserkerClassMod.MOD_ID)
public final class ForgeMod {
    @SuppressWarnings("removal")
    public ForgeMod() {
        BerserkerClassMod.init();

        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(EventPriority.NORMAL, false, RegisterEvent.class, ForgeMod::register);
        modBus.addListener(EventPriority.NORMAL, false, BuildCreativeModeTabContentsEvent.class,
                ForgeMod::onBuildCreativeTabContents);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ForgeClient.register(modBus);
        }
    }

    // Goes through the helper on purpose, on Forge 47.0-47.3 a plain Registry.register throws "Can not register to a locked registry".
    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, helper -> {
            BerserkerSounds.soundsToRegister().forEach(helper::register);
            BerserkerSounds.linkEntries();
        });

        event.register(RegistryKeys.STATUS_EFFECT, helper -> {
            BerserkerEffects.effectsToRegister(BerserkerClassMod.effectsConfig.value).forEach(helper::register);
            Effects.linkEntries(BerserkerEffects.entries);
            BerserkerClassMod.effectsConfig.save();
        });

        event.register(RegistryKeys.ITEM, helper -> {
            BerserkerItems.registerModItems();
            WeaponsRegister.itemsToRegister(BerserkerClassMod.itemConfig.value.weapons).forEach(helper::register);
            Armors.itemsToRegister(BerserkerClassMod.itemConfig.value.armor_sets).forEach(helper::register);
            BerserkerClassMod.itemConfig.save();
            BerserkerClassMod.effectsConfig.save();
        });

        event.register(RegistryKeys.ITEM_GROUP,
                helper -> helper.register(BerserkerGroup.ID, BerserkerGroup.createItemGroup()));
    }

    public static void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        Armors.forEachGroupOverride((pieces, key) -> {
            if (event.getTabKey().equals(BerserkerGroup.BERSERKER_KEY)) {
                for (var piece : pieces) {
                    removeFromTab(event, (ArmorItem) piece);
                }
            } else if (event.getTabKey().equals(key)) {
                for (var piece : pieces) {
                    event.accept(() -> (ArmorItem) piece);
                }
            }
        });
        WeaponsRegister.forEachGroupOverride((item, key) -> {
            if (event.getTabKey().equals(BerserkerGroup.BERSERKER_KEY)) {
                removeFromTab(event, item);
            } else if (event.getTabKey().equals(key)) {
                event.accept(() -> item);
            }
        });
    }

    private static void removeFromTab(BuildCreativeModeTabContentsEvent event, Item item) {
        var iterator = event.getEntries().iterator();
        while (iterator.hasNext()) {
            ItemStack stack = iterator.next().getKey();
            if (stack.isOf(item)) {
                iterator.remove();
            }
        }
    }
}
