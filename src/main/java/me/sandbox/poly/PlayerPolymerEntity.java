package me.sandbox.poly;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import me.sandbox.mixin.poly.PlayerEntityAccessor;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.PlayerAssociatedNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;

import java.util.*;
import java.util.function.Consumer;

public interface PlayerPolymerEntity extends PolymerEntity {
    Map<EntityType<?>, ItemStack> HEADS = new HashMap<>();

    default void onCreated(LivingEntity entity) {
        var x = new ItemDisplayElement();
        var holder = new ElementHolder();
        x.setInvisible(true);
        holder.addElement(x);

        EntityAttachment.of(holder, entity);
        VirtualEntityUtils.addVirtualPassenger(entity, x.getEntityId());
    }

    @Override
    default void onEntityPacketSent(Consumer<Packet<?>> consumer, Packet<?> packet) {
        PolymerEntity.super.onEntityPacketSent(consumer, packet);
        if (packet instanceof EntitySetHeadYawS2CPacket headYawS2CPacket) {
            var ent = (Entity) this;
            consumer.accept(new EntityS2CPacket.Rotate(ent.getId(), headYawS2CPacket.getHeadYaw(), (byte) (ent.getPitch() * 256.0F / 360.0F), ent.isOnGround()));
        }
    }

    @Override
    default List<Pair<EquipmentSlot, ItemStack>> getPolymerVisibleEquipment(List<Pair<EquipmentSlot, ItemStack>> items, ServerPlayerEntity player) {
        if (PolymerResourcePackUtils.hasMainPack(player)) {
            items.removeIf(x -> x.getFirst() == EquipmentSlot.HEAD);
            items.add(new Pair<>(EquipmentSlot.HEAD, HEADS.getOrDefault(((Entity) this).getType(), ItemStack.EMPTY)));
        }
        return PolymerEntity.super.getPolymerVisibleEquipment(items, player);
    }

    @Override
    default void onBeforeSpawnPacket(ServerPlayerEntity player, Consumer<Packet<?>> packetConsumer) {
        var packet = PolymerEntityUtils.createMutablePlayerListPacket(EnumSet.of(PlayerListS2CPacket.Action.ADD_PLAYER));
        var profile = new GameProfile(((Entity) this).getUuid(), "");
        profile.getProperties().put("textures", this.getSkin());
        packet.getEntries().add(new PlayerListS2CPacket.Entry(profile.getId(), profile, false, Integer.MAX_VALUE, GameMode.ADVENTURE, Text.empty(), null));
        packetConsumer.accept(packet);
    }

    @Override
    default void modifyRawTrackedData(List<DataTracker.SerializedEntry<?>> data, ServerPlayerEntity player, boolean initial) {
        data.add(DataTracker.SerializedEntry.of(PlayerEntityAccessor.getPLAYER_MODEL_PARTS(), (byte) (PolymerResourcePackUtils.hasMainPack(player) ? 0x3F : 0xFF)));
    }


    default void onTrackingStopped(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(new PlayerRemoveS2CPacket(List.of(((Entity) this).getUuid())));
    }

    @Override
    default void onEntityTrackerTick(Set<PlayerAssociatedNetworkHandler> listeners) {
        var e = (IllagerEntity) this;
        if (e.getState() == IllagerEntity.State.SPELLCASTING) {
            var packet = new EntityAnimationS2CPacket(e, e.getRandom().nextBoolean() ? 0 : 3);

            for (var p : listeners) {
                p.sendPacket(packet);
            }
        } else if (this instanceof Stunnable s && s.getStunnedState() && e.age % 5 == 0) {
            var packet = new ParticleS2CPacket(ParticleTypes.AMBIENT_ENTITY_EFFECT, false, e.getX(), e.getEyeY(), e.getZ(), 1, 1, 1, 1, 0);

            for (var p : listeners) {
                p.sendPacket(packet);
            }
        }
    }

    @Override
    default EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.PLAYER;
    }


    Property getSkin();
}
