package eu.pb4.illagerexpansion.entity;

import java.util.List;
import java.util.UUID;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.illagerexpansion.sounds.SoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;
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

    public InvokerFangsEntity(EntityType<? extends InvokerFangsEntity> entityType, World world) {
        super(entityType, world);
    }

    public InvokerFangsEntity(World world, double x, double y, double z, float yaw, int warmup, LivingEntity owner) {
        this(EntityRegistry.INVOKER_FANGS, world);
        this.warmup = warmup;
        this.setOwner(owner);
        this.setYaw(yaw * 57.295776f);
        this.setPosition(x, y, z);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUuid = owner == null ? null : owner.getUuid();
    }

    @Nullable
    public LivingEntity getOwner() {
        Entity entity;
        if (this.owner == null && this.ownerUuid != null && this.getWorld() instanceof ServerWorld serverWorld && (entity = serverWorld.getEntity(this.ownerUuid)) instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
        }
        return this.owner;
    }

    @Override
    protected void readCustomData(ReadView nbt) {
        this.warmup = nbt.getInt("Warmup", 0);
        this.ownerUuid = nbt.read("Owner", Uuids.STRICT_CODEC).orElse(null);
    }

    @Override
    protected void writeCustomData(WriteView nbt) {
        nbt.putInt("Warmup", this.warmup);
        if (this.ownerUuid != null) {
            nbt.put("Owner", Uuids.STRICT_CODEC, this.ownerUuid);
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            if (this.playingAnimation) {
                --this.ticksLeft;
                if (this.ticksLeft == 14) {
                    for (int i = 0; i < 12; ++i) {
                        double d = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
                        double e = this.getY() + 0.05 + this.random.nextDouble();
                        double f = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * (double)this.getWidth() * 0.5;
                        double g = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        double h = 0.3 + this.random.nextDouble() * 0.3;
                        double j = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                        //this.getWorld().addParticle(ParticleTypes.CRIT, d, e + 1.0, f, g, h, j);
                    }
                }
            }
        } else if (--this.warmup < 0) {
            if (this.warmup == -8) {
                List<LivingEntity> list = this.getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(0.2, 0.0, 0.2));
                for (LivingEntity livingEntity : list) {
                    this.damage(livingEntity);
                }
            }
            if (!this.startedAttack) {
                this.getWorld().sendEntityStatus(this, (byte)4);
                this.startedAttack = true;
            }
            if (--this.ticksLeft < 0) {
                this.discard();
            }
        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    private void damage(LivingEntity target) {
        LivingEntity livingEntity = this.getOwner();
        if (!target.isAlive() || target.isInvulnerable() || target == livingEntity) {
            return;
        }
        if (livingEntity == null) {
            target.serverDamage(this.getDamageSources().magic(), 10.0f);
            target.addVelocity(0.0f, 1.7f, 0.0f);
        } else {
            if (livingEntity.isTeammate(target)) {
                return;
            }
            target.serverDamage(this.getDamageSources().indirectMagic(this, livingEntity), 10.0f);
            this.knockBack(target);

        }
    }

    @Override
    public void handleStatus(byte status) {
        super.handleStatus(status);
        if (status == 4) {
            this.playingAnimation = true;
            if (!this.isSilent()) {
                this.getWorld().playSound(this, this.getX(), this.getY(), this.getZ(), SoundRegistry.INVOKER_FANGS, this.getSoundCategory(), 1.0f, this.random.nextFloat() * 0.2f + 0.85f);
            }
        }
    }
    private void knockBack(LivingEntity entity) {
        entity.addVelocity(0.0, 0.6, 0.0);
    }
    protected void knockback(LivingEntity target) {
        this.knockBack(target);
        target.velocityModified = true;
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


