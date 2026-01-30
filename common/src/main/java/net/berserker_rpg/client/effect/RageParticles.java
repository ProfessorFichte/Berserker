package net.berserker_rpg.client.effect;

import net.minecraft.entity.LivingEntity;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleBatch;
import net.spell_engine.fx.ParticleHelper;

public class RageParticles implements CustomParticleStatusEffect.Spawner{
    private final ParticleBatch particles;

    public RageParticles(int particleCount) {
        this.particles = new ParticleBatch(
                "more_rpg_classes:rage_particle",
                ParticleBatch.Shape.WIDE_PIPE, ParticleBatch.Origin.CENTER,
                null, particleCount, 0.1F, 0.3F, 0);
    }

    @Override
    public void spawnParticles(LivingEntity livingEntity, int amplifier) {
        var world = livingEntity.getWorld();
        if (world.isClient) {
            if (world.random.nextFloat() < 0.15F) {
                ParticleHelper.play(livingEntity.getWorld(), livingEntity, particles);
            }
        }
    }
}

