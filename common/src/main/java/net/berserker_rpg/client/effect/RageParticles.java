package net.berserker_rpg.client.effect;

import net.minecraft.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.fx.ParticleHelper;

public class RageParticles implements CustomParticleStatusEffect.Spawner{
    private final ParticleBatch particles;

    public RageParticles(int particleCount) {
        this.particles = new ParticleBatch(
                "berserker_rpg:rage_particle",
                ParticleBatch.Shape.PIPE, ParticleBatch.Origin.LAUNCH_POINT,
                null, particleCount, 0.1F, 0.3F, 0);
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var world = livingEntity.getWorld();
        if (world.isClient) {
            var scaledParticles = new ParticleBatch(particles);
            scaledParticles.count *= (1);
            ParticleHelper.play(livingEntity.getWorld(), livingEntity, scaledParticles);
        }
    }
}

