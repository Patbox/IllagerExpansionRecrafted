package eu.pb4.illagerexpansion.util.spellutil;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SpellParticleUtil <T extends ParticleEffect> {

    public void setSpellParticles(LivingEntity entity, World world, T particleTypes, int count, double speed) {
        float g = entity.bodyYaw * ((float)Math.PI / 180) + MathHelper.cos((float)entity.age * 0.6662f) * 0.25f;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        ((ServerWorld)world).spawnParticles(particleTypes, entity.getX() + (double)h * 0.6, entity.getY() + 1.8, entity.getZ() + (double)i, count, 0.0D, 0.0D, 0.0D, speed);
        ((ServerWorld)world).spawnParticles(particleTypes, entity.getX() - (double)h * 0.6, entity.getY() + 1.8, entity.getZ() - (double)i, count, 0.0D, 0.0D, 0.0D, speed);
    }
}
