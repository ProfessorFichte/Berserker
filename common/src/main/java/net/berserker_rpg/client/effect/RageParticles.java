package net.berserker_rpg.client.effect;

import net.minecraft.entity.LivingEntity;
import net.more_rpg_classes.client.particle.MoreParticles;
import net.spell_engine.api.effect.CustomParticleStatusEffect;
import net.spell_engine.api.spell.fx.ParticleGroup;
import net.spell_engine.api.spell.fx.ParticleGroupBuilder;
import net.spell_engine.fx.ParticleHelper;

public class RageParticles implements CustomParticleStatusEffect.Spawner {
    private final ParticleGroup particles;

    public RageParticles(int particleCount) {
        this.particles = ParticleGroupBuilder.of(MoreParticles.RAGE_PAR)
                .batch(b -> b.shape(ParticleGroup.Shape.PIPE).widthFactor(2F)
                        .count(particleCount).speed(0.1F, 0.3F));
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
