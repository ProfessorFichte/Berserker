package com.berserker.forge;

import net.berserker_rpg.BerserkerClassMod;
import net.berserker_rpg.item.BerserkerGroup;
import net.berserker_rpg.item.armor.Armors;
import net.berserker_rpg.item.weapons.WeaponsRegister;
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
/// Forge locks every vanilla registry outside its own `RegisterEvent` window, so each `registerX()` call
/// sits inside the window of the registry it writes to. `ITEM_GROUP` is *not* a Forge-wrapped registry, so
/// it never gets its own `RegisterEvent` — the group is registered from the `ITEM` window instead (it stays
/// unfrozen for the whole phase), which is what `BerserkerClassMod.registerItems()` does.
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
        event.register(RegistryKeys.SOUND_EVENT, reg -> BerserkerClassMod.registerSounds());
        event.register(RegistryKeys.STATUS_EFFECT, reg -> BerserkerClassMod.registerEffects());
        event.register(RegistryKeys.ITEM, reg -> {
            // Also registers the `berserker_rpg:generic` item group, see the class doc.
            BerserkerClassMod.registerItems();
        });
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
