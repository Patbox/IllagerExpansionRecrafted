package eu.pb4.illagerexpansion.entity.projectile;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import eu.pb4.illagerexpansion.entity.EntityRegistry;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class HatchetEntity extends PersistentProjectileEntity implements FlyingItemEntity, PolymerEntity {
    private boolean dealtDamage;
    private static final TrackedData<Float> ROLL = DataTracker.registerData(HatchetEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public HatchetEntity(EntityType<? extends HatchetEntity> entityType, World world) {
        super(entityType, world);
    }

    public HatchetEntity(World world, LivingEntity owner, ItemStack stack) {
        super(EntityRegistry.HATCHET, owner, world, stack, stack);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ROLL, 0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isInGround()) {
            this.dataTracker.set(ROLL, (float) (this.dataTracker.get(ROLL) - MathHelper.RADIANS_PER_DEGREE * this.getVelocity().lengthSquared() * 15) % MathHelper.TAU);
        }
    }

    @Override
    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        if (this.dealtDamage) {
            return null;
        }
        return super.getEntityCollision(currentPosition, nextPosition);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        float f = 8.0f;
        DamageSource damageSource = this.getDamageSources().trident(this, this.getOwner());

        if (entity instanceof LivingEntity livingEntity) {
            f = EnchantmentHelper.getDamage((ServerWorld) this.getWorld(), this.getStack(), livingEntity, damageSource, f);
        }
        this.dealtDamage = true;
        SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;
        if (entity.damage((ServerWorld) this.getWorld(), damageSource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (entity instanceof LivingEntity livingEntity) {
                    EnchantmentHelper.onTargetDamaged((ServerWorld) this.getWorld(), livingEntity, damageSource, this.getStack());

                this.onHit(livingEntity);
            }
        }
        this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
        float g = 1.0f;
        this.playSound(soundEvent, g, 1.0f);
    }


    @Override
    protected boolean tryPickup(PlayerEntity player) {
        return super.tryPickup(player) || this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return ItemRegistry.HATCHET.getDefaultStack();
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.isOwner(player) || this.getOwner() == null) {
            super.onPlayerCollision(player);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dealtDamage = nbt.getBoolean("DealtDamage", false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("DealtDamage", this.dealtDamage);
    }


    @Override
    protected float getDragInWater() {
        return 0.99f;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        var sendBase = true;
        for (int i = 0; i < data.size(); i++) {
            var roll = data.get(i);
            if (roll.id() == ROLL.id() && roll.handler() == ROLL.dataType()) {
                data.set(i, DataTracker.SerializedEntry.of(DisplayTrackedData.LEFT_ROTATION, new Quaternionf().rotateY(MathHelper.HALF_PI).rotateZ((float) roll.value())));
                sendBase = false;
                break;
            }
        }

        if (initial) {
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.Item.ITEM, this.getStack().copy()));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.SCALE, new Vector3f(0.6f)));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.TRANSLATION, new Vector3f(0, -0.1f, 0)));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.INTERPOLATION_DURATION, 2));
            data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.TELEPORTATION_DURATION, 4));
            if (sendBase) {
                data.add(DataTracker.SerializedEntry.of(DisplayTrackedData.LEFT_ROTATION, new Quaternionf().rotateY(MathHelper.HALF_PI)));
            }
        }

    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    public Vec3d getSyncedPos() {
        return super.getSyncedPos().add(0, 0.1, 0);
    }

    @Override
    public ItemStack getStack() {
        return this.getItemStack();
    }
}
