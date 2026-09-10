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

/// Forge 47 entrypoint (1.20.1 port of the NeoForge entrypoint).
///
/// Registration is duplicated here on purpose. `common/` writes content the Architectury way -
/// `Registry.register(Registries.X, ...)` - which Forge rejects with
/// `IllegalStateException: Can not register to a locked registry` on **47.0-47.3 and NeoForge 1.20.1**, even
/// inside the correct `RegisterEvent` window: only 47.4.0+ clears the vanilla `NamespacedWrapper`'s lock.
/// `mods.toml` declares `loaderVersion = "[47,)"`, so those are supported configurations. The fix is to
/// register through the `RegisterHelper` the event hands out, iterating the same content `common` exposes;
/// `fabric/` is untouched and keeps calling `BerserkerClassMod.registerX()`.
///
/// Each block is declared unconditionally - `event.register` is a no-op unless its key matches the event's
/// registry, and Forge posts one event per registry. Note `creative_mode_tab` is event **65** while `item` is
/// event **7**, so the item group gets its own block; riding along in the ITEM pass would drop it silently.
@Mod(BerserkerClassMod.MOD_ID)
public final class ForgeMod {
    // FMLJavaModLoadingContext.get() is flagged for removal by late 47.x builds, but the
    // constructor-injected replacement doesn't exist on early 47.x; get() works on all of [47,).
    @SuppressWarnings("removal")
    public ForgeMod() {
        BerserkerClassMod.init();

        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Explicit event classes: Forge 47's plain addListener(Consumer) infers the event type from the
        // lambda via TypeTools, which is fragile; the 4-arg overload takes it directly.
        modBus.addListener(EventPriority.NORMAL, false, RegisterEvent.class, ForgeMod::register);
        modBus.addListener(EventPriority.NORMAL, false, BuildCreativeModeTabContentsEvent.class,
                ForgeMod::onBuildCreativeTabContents);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ForgeClient.register(modBus);
        }
    }

    public static void register(RegisterEvent event) {
        // `BerserkerClassMod.registerSounds()`
        event.register(RegistryKeys.SOUND_EVENT, helper -> {
            BerserkerSounds.soundsToRegister().forEach(helper::register);
            // The helper returns void where `Registry.registerReference` returned the entry - read them back.
            BerserkerSounds.linkEntries();
        });

        // `BerserkerClassMod.registerEffects()`
        event.register(RegistryKeys.STATUS_EFFECT, helper -> {
            BerserkerEffects.effectsToRegister(BerserkerClassMod.effectsConfig.value).forEach(helper::register);
            Effects.linkEntries(BerserkerEffects.entries);
            BerserkerClassMod.effectsConfig.save();
        });

        // `BerserkerClassMod.registerItems()`, minus the item group (see below).
        event.register(RegistryKeys.ITEM, helper -> {
            BerserkerItems.registerModItems();
            WeaponsRegister.itemsToRegister(BerserkerClassMod.itemConfig.value.weapons).forEach(helper::register);
            Armors.itemsToRegister(BerserkerClassMod.itemConfig.value.armor_sets).forEach(helper::register);
            // Trailing side effects of registerItems() - dropping these would stop the configs being written out.
            BerserkerClassMod.itemConfig.save();
            BerserkerClassMod.effectsConfig.save();
        });

        // NOT in the ITEM block: `creative_mode_tab` is event 65, `item` is event 7.
        event.register(RegistryKeys.ITEM_GROUP,
                helper -> helper.register(BerserkerGroup.ID, BerserkerGroup.createItemGroup()));
    }

    /// Forge fires this per creative tab, on the logical client only. Unlike NeoForge there is no
    /// `remove(...)` / `getParentEntries()` / `getSearchEntries()`: the tab is one
    /// `MutableHashedLinkedMap<ItemStack, StackVisibility>` covering both the parent and the search tab, so
    /// removing a key removes it from both. `event.accept(Supplier)` / `event.add(ItemStack)` add.
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
