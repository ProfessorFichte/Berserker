package net.berserker_rpg.datagen;

import com.google.gson.JsonObject;
import net.berserker_rpg.item.weapons.WeaponsRegister;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;
import net.more_rpg_classes.datagen.BetterCombatWeaponAttributeGenerator;
import net.spell_engine.api.item.weapon.Weapon;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;

public class WeaponAttributesGenerator implements DataProvider {
        private final DataOutput.PathResolver pathResolver;

    public WeaponAttributesGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "weapon_attributes");
        }

        @Override
        public CompletableFuture<?> run(DataWriter writer) {
            List<CompletableFuture<?>> futures = new ArrayList<>();

            for (Weapon.Entry entry : WeaponsRegister.entries) {
                if (!entry.id().getPath().contains("berserker_axe")) {
                    continue;
                }

                JsonObject json = new JsonObject();
                json.addProperty("parent", MOD_ID + ":berserker_axe_attack");

                Path path = pathResolver.resolveJson(entry.id());
                futures.add(DataProvider.writeToPath(writer, json, path));
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        }

        @Override
        public String getName() {
            return "Berserker Weapon Attributes";
        }
}
