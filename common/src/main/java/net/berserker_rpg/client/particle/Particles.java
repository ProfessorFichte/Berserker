package net.berserker_rpg.client.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.berserker_rpg.BerserkerClassMod.MOD_ID;
public class Particles {
    public static final SimpleParticleType RAGE_PAR = FabricParticleTypes.simple();
    public static final SimpleParticleType SMALL_THUNDER = FabricParticleTypes.simple();


    public static void register(){
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "rage_particle"), RAGE_PAR);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "small_thunder"), SMALL_THUNDER);
    }
}
