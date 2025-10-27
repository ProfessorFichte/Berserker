package net.berserker_rpg.spell;

public class OldCustomSpells {
    public static void register() {
        /*
        /// OUTRAGE
        CustomSpellHandler.register(Identifier.of(MOD_ID, "outrage"), (data) -> {
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            var spellEntry = SpellRegistry.from(data1.caster().getWorld()).getEntry(Identifier.of(MOD_ID, "outrage")).orElse(null);
            var spell = spellEntry.value();
            Spell.Impact[] impacts = spell.impact;

            if (!data1.caster().getWorld().isClient) {
                for (Entity entity : data1.targets()) {
                    SpellHelper.performImpacts(entity.getWorld(), data1.caster(), entity, entity, spellEntry,impacts ,data1.impactContext());
                    if (data1.caster().hasStatusEffect(Effects.RAGE.registryEntry)) {
                        final int amp_rage = data1.caster().getStatusEffect(Effects.RAGE.registryEntry).getAmplifier();
                        final int dura_rage = data1.caster().getStatusEffect(Effects.RAGE.registryEntry).getDuration();
                        int rage_amplifier_max = effectsConfig.value.rage_max_amplifier_stack - 1;
                        if(amp_rage == rage_amplifier_max){
                            clearNegativeEffects(data1.caster(),true);
                            data1.caster().addStatusEffect(new StatusEffectInstance(Effects.RAGE.registryEntry, dura_rage + 10,rage_amplifier_max,false,false,true));
                        }
                    }
                }
            }
            return false;
        });

        /// RUMBLING SWING
        CustomSpellHandler.register(Identifier.of(MOD_ID,"rumbling_swing"),(data) -> {
            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            var spellEntry = SpellRegistry.from(data1.caster().getWorld()).getEntry(Identifier.of(MOD_ID, "rumbling_swing")).orElse(null);
            var spell = spellEntry.value();
            Spell.Impact[] impacts = spell.impact;
            float range = spell.range;
            BlockHitResult result = data1.caster().getWorld().raycast(new RaycastContext(data1.caster().getEyePos(), data1.caster().getEyePos()
                    .add(data1.caster().getRotationVector().multiply(spell.range)),
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,data1.caster()));
            if(result.getPos() != null) {
                data1.caster().requestTeleport(result.getPos().getX(),result.getPos().getY(),result.getPos().getZ());
            }
            List<Entity> list = TargetHelper.targetsFromArea(data1.caster(),data1.caster().getEyePos(),range,new Spell.Release.Target.Area(), target -> TargetHelper.allowedToHurt(data1.caster(),target) );
            for(Entity entity : list){
                SpellHelper.performImpacts(data1.caster().getWorld(),data1.caster(),entity,data1.caster(), spellEntry,impacts,data1.impactContext());
            }
            return true;
        });
*/
    }
}
