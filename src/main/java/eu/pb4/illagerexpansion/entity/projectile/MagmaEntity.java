package eu.pb4.illagerexpansion.entity.projectile;


import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.illagerexpansion.entity.EntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import xyz.nucleoid.packettweaker.PacketContext;

public class MagmaEntity extends ExplosiveProjectileEntity implements PolymerEntity {

    public MagmaEntity(EntityType<? extends MagmaEntity> entityType, World world) {
        super((EntityType<? extends ExplosiveProjectileEntity>)entityType, world);
    }

    public MagmaEntity(World world, LivingEntity owner, double directionX, double directionY, double directionZ) {
        super(EntityRegistry.MAGMA, owner, new Vec3d(directionX, directionY, directionZ), world);
    }
    @Override
    public void tick() {
        if (getWorld() instanceof ServerWorld) {
            ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 3, 0.3D, 0.3D, 0.3D, 0.05D);
        }
        super.tick();
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (getWorld().isClient) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        Entity entity2 = this.getOwner();
        entity.serverDamage(this.getDamageSources().indirectMagic(this, entity2), 12.0f);
        if (getWorld() instanceof ServerWorld) {
            ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 15, 0.4D, 0.4D, 0.4D, 0.15D);
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!getWorld().isClient) {
            boolean bl = ((ServerWorld) getWorld()).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
            getWorld().createExplosion(null, this.getX(), this.getY(), this.getZ(), 1, bl, World.ExplosionSourceType.MOB);
            this.discard();
        }
        if (getWorld() instanceof ServerWorld) {
            ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 15, 0.4D, 0.4D, 0.4D, 0.15D);
        }
        this.discard();

    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.FIREBALL;
    }
}

