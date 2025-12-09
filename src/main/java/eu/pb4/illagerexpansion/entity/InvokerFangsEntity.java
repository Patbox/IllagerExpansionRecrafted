package eu.pb4.illagerexpansion.entity;

import java.util.List;
import java.util.UUID;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class InvokerFangsEntity
        extends Entity implements PolymerEntity {
    private int warmup;
    private boolean startedAttack;
    private int ticksLeft = 22;
    private boolean playingAnimation;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUuid;

    public InvokerFangsEntity(EntityType<? extends InvokerFangsEntity> entityType, Level world) {
        super(entityType, world);
    }

    public InvokerFangsEntity(Level world, double x, double y, double z, float yaw, int warmup, LivingEntity owner) {
        this(EntityRegistry.INVOKER_FANGS, world);
        this.warmup = warmup;
        this.setOwner(owner);
        this.setYRot(yaw * 57.295776f);
        this.setPos(x, y, z);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUuid = owner == null ? null : owner.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        Entity entity;
        if (this.owner == null && this.ownerUuid != null && this.level() instanceof ServerLevel serverWorld && (entity = serverWorld.getEntity(this.ownerUuid)) instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
        }
        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput nbt) {
        this.warmup = nbt.getIntOr("Warmup", 0);
        this.ownerUuid = nbt.read("Owner", UUIDUtil.LENIENT_CODEC).orElse(null);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput nbt) {
        nbt.putInt("Warmup", this.warmup);
        if (this.ownerUuid != null) {
            nbt.store("Owner", UUIDUtil.LENIENT_CODEC, this.ownerUuid);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();
        if (this.playingAnimation) {
            --this.ticksLeft;
            if (this.ticksLeft == 14) {
                for (int i = 0; i < 12; ++i) {
                    double d = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getBbWidth() * 0.5;
                    double e = this.getY() + 0.05 + this.random.nextDouble();
                    double f = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getBbWidth() * 0.5;
                    double g = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                    double h = 0.3 + this.random.nextDouble() * 0.3;
                    double j = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.CRIT, d, e + 1.0, f, 0, g, h, j, 1);
                }
            }
        }
        if (--this.warmup < 0) {
            if (this.warmup == -8) {
                List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2));
                for (LivingEntity livingEntity : list) {
                    this.damage(livingEntity);
                }
            }
            if (!this.startedAttack) {
                this.level().broadcastEntityEvent(this, (byte)4);
                this.startedAttack = true;
            }
            if (--this.ticksLeft < 0) {
                this.discard();
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel world, DamageSource source, float amount) {
        return false;
    }

    private void damage(LivingEntity target) {
        LivingEntity livingEntity = this.getOwner();
        if (!target.isAlive() || target.isInvulnerable() || target == livingEntity) {
            return;
        }
        if (livingEntity == null) {
            target.hurt(this.damageSources().magic(), 10.0f);
            target.push(0.0f, 1.7f, 0.0f);
        } else {
            if (livingEntity.isAlliedTo(target)) {
                return;
            }
            target.hurt(this.damageSources().indirectMagic(this, livingEntity), 10.0f);
            this.knockBack(target);

        }
    }

    @Override
    public void handleEntityEvent(byte status) {
        super.handleEntityEvent(status);
        if (status == 4) {
            this.playingAnimation = true;
            if (!this.isSilent()) {
                this.level().playSound(this, this.getX(), this.getY(), this.getZ(), SoundRegistry.INVOKER_FANGS, this.getSoundSource(), 1.0f, this.random.nextFloat() * 0.2f + 0.85f);
            }
        }
    }
    private void knockBack(LivingEntity entity) {
        entity.push(0.0, 0.6, 0.0);
    }
    protected void knockback(LivingEntity target) {
        this.knockBack(target);
        target.hurtMarked = true;
    }

    public float getAnimationProgress(float tickDelta) {
        if (!this.playingAnimation) {
            return 0.0f;
        }
        int i = this.ticksLeft - 2;
        if (i <= 0) {
            return 1.0f;
        }
        return 1.0f - ((float)i - tickDelta) / 20.0f;
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.EVOKER_FANGS;
    }

}


