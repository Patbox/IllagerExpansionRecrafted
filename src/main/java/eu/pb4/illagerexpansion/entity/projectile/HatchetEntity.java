package eu.pb4.illagerexpansion.entity.projectile;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import eu.pb4.illagerexpansion.entity.EntityRegistry;
import eu.pb4.illagerexpansion.item.ItemRegistry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class HatchetEntity extends AbstractArrow implements ItemSupplier, PolymerEntity {
    private boolean dealtDamage;
    private static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(HatchetEntity.class, EntityDataSerializers.FLOAT);

    public HatchetEntity(EntityType<? extends HatchetEntity> entityType, Level world) {
        super(entityType, world);
    }

    public HatchetEntity(Level world, LivingEntity owner, ItemStack stack) {
        super(EntityRegistry.HATCHET, owner, world, stack, stack);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ROLL, 0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isInGround()) {
            this.entityData.set(ROLL, (float) (this.entityData.get(ROLL) - Mth.DEG_TO_RAD * this.getDeltaMovement().lengthSqr() * 15) % Mth.TWO_PI);
        }
    }

    @Override
    @Nullable
    protected EntityHitResult findHitEntity(Vec3 currentPosition, Vec3 nextPosition) {
        if (this.dealtDamage) {
            return null;
        }
        return super.findHitEntity(currentPosition, nextPosition);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        float f = 8.0f;
        DamageSource damageSource = this.damageSources().trident(this, this.getOwner());

        if (entity instanceof LivingEntity livingEntity) {
            f = EnchantmentHelper.modifyDamage((ServerLevel) this.level(), this.getItem(), livingEntity, damageSource, f);
        }
        this.dealtDamage = true;
        SoundEvent soundEvent = SoundEvents.TRIDENT_HIT;
        if (entity.hurtServer((ServerLevel) this.level(), damageSource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (entity instanceof LivingEntity livingEntity) {
                    EnchantmentHelper.doPostAttackEffectsWithItemSource((ServerLevel) this.level(), livingEntity, damageSource, this.getItem());

                this.doPostHurtEffects(livingEntity);
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        float g = 1.0f;
        this.playSound(soundEvent, g, 1.0f);
    }


    @Override
    protected boolean tryPickup(Player player) {
        return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ItemRegistry.HATCHET.getDefaultInstance();
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void playerTouch(Player player) {
        if (this.ownedBy(player) || this.getOwner() == null) {
            super.playerTouch(player);
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        this.dealtDamage = nbt.getBooleanOr("DealtDamage", false);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("DealtDamage", this.dealtDamage);
    }


    @Override
    protected float getWaterInertia() {
        return 0.99f;
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        var sendBase = true;
        for (int i = 0; i < data.size(); i++) {
            var roll = data.get(i);
            if (roll.id() == ROLL.id() && roll.serializer() == ROLL.serializer()) {
                data.set(i, SynchedEntityData.DataValue.create(DisplayTrackedData.LEFT_ROTATION, new Quaternionf().rotateY(Mth.HALF_PI).rotateZ((float) roll.value())));
                sendBase = false;
                break;
            }
        }

        if (initial) {
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.Item.ITEM, this.getItem().copy()));
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.SCALE, new Vector3f(0.6f)));
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.TRANSLATION, new Vector3f(0, -0.1f, 0)));
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.INTERPOLATION_DURATION, 2));
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.TELEPORTATION_DURATION, 4));
            if (sendBase) {
                data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.LEFT_ROTATION, new Quaternionf().rotateY(Mth.HALF_PI)));
            }
        }

    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    public Vec3 trackingPosition() {
        return super.trackingPosition().add(0, 0.1, 0);
    }

    @Override
    public ItemStack getItem() {
        return this.getPickupItemStackOrigin();
    }
}
