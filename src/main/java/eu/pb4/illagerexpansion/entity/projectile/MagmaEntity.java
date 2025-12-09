package eu.pb4.illagerexpansion.entity.projectile;


import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import eu.pb4.illagerexpansion.entity.EntityRegistry;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class MagmaEntity extends AbstractHurtingProjectile implements PolymerEntity {

    public MagmaEntity(EntityType<? extends MagmaEntity> entityType, Level world) {
        super((EntityType<? extends AbstractHurtingProjectile>)entityType, world);
    }

    public MagmaEntity(Level world, LivingEntity owner, double directionX, double directionY, double directionZ) {
        super(EntityRegistry.MAGMA, owner, new Vec3(directionX, directionY, directionZ), world);
    }
    @Override
    public void tick() {
        if (level() instanceof ServerLevel) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 3, 0.3D, 0.3D, 0.3D, 0.05D);
        }
        super.tick();
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (level().isClientSide()) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        Entity entity2 = this.getOwner();
        entity.hurt(this.damageSources().indirectMagic(this, entity2), 12.0f);
        if (level() instanceof ServerLevel) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 15, 0.4D, 0.4D, 0.4D, 0.15D);
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!level().isClientSide()) {
            boolean bl = ((ServerLevel) level()).getGameRules().get(GameRules.MOB_GRIEFING);
            level().explode(null, this.getX(), this.getY(), this.getZ(), 1, bl, Level.ExplosionInteraction.MOB);
            this.discard();
        }
        if (level() instanceof ServerLevel) {
            ((ServerLevel) level()).sendParticles(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 15, 0.4D, 0.4D, 0.4D, 0.15D);
        }
        this.discard();

    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity entity) {
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.FIREBALL;
    }
}

