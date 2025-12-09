package eu.pb4.illagerexpansion.poly;

import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;
import eu.pb4.illagerexpansion.mixin.MannequinEntityAccessor;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.illagerexpansion.mixin.poly.PlayerLikeEntityAccessor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SpellParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.*;
import java.util.function.Consumer;

public interface PlayerPolymerEntity extends PolymerEntity {
    Map<EntityType<?>, ItemStack> HEADS = new HashMap<>();

    WeakHashMap<LivingEntity, ItemDisplayElement> BANNER_ELEMENTS = new WeakHashMap<>();

    default void onCreated(LivingEntity entity) {
        var x = new ItemDisplayElement();
        var holder = new ElementHolder();
        x.setInvisible(true);
        x.setItemDisplayContext(ItemDisplayContext.HEAD);
        x.setTeleportDuration(3);
        x.setScale(new Vector3f(0.5f));
        holder.addElement(x);
        EntityAttachment.of(holder, entity);
        VirtualEntityUtils.addVirtualPassenger(entity, x.getEntityId());
        BANNER_ELEMENTS.put(entity, x);
    }

    @Override
    default void onEntityPacketSent(Consumer<Packet<?>> consumer, Packet<?> packet) {
        PolymerEntity.super.onEntityPacketSent(consumer, packet);
        if (packet instanceof ClientboundRotateHeadPacket headYawS2CPacket) {
            var ent = (Entity) this;
            consumer.accept(new ClientboundMoveEntityPacket.Rot(ent.getId(), Mth.packDegrees(headYawS2CPacket.getYHeadRot()), (byte) (ent.getXRot() * 256.0F / 360.0F), ent.onGround()));
        }
    }

    @Override
    default List<Pair<EquipmentSlot, ItemStack>> getPolymerVisibleEquipment(List<Pair<EquipmentSlot, ItemStack>> items, ServerPlayer player) {
        items.removeIf(x -> x.getFirst() == EquipmentSlot.HEAD);
        if (PolymerResourcePackUtils.hasMainPack(player)) {
            items.add(new Pair<>(EquipmentSlot.HEAD, HEADS.getOrDefault(((Entity) this).getType(), ItemStack.EMPTY)));
        }
        return PolymerEntity.super.getPolymerVisibleEquipment(items, player);
    }

    @Override
    default void onBeforeSpawnPacket(ServerPlayer player, Consumer<Packet<?>> packetConsumer) {
        var packet = PolymerEntityUtils.createMutablePlayerListPacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER));
        var profile = new GameProfile(((Entity) this).getUUID(), "", new PropertyMap(ImmutableMultimap.of("textures", this.getSkin())));
        packet.entries().add(new ClientboundPlayerInfoUpdatePacket.Entry(profile.id(), profile, false, Integer.MAX_VALUE,  GameType.ADVENTURE, Component.empty(), true,0, null));
        packetConsumer.accept(packet);
    }

    @Override
    default void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        data.removeIf(x -> x.id() >= PlayerLikeEntityAccessor.getDATA_PLAYER_MAIN_HAND().id());
        if (initial) {
            data.add(SynchedEntityData.DataValue.create(PlayerLikeEntityAccessor.getDATA_PLAYER_MODE_CUSTOMISATION(), (byte) (PolymerResourcePackUtils.hasMainPack(player) ? 0x3E : 0xFE)));
            //data.add(DataTracker.SerializedEntry.of(MannequinEntityAccessor.getPROFILE(), ProfileComponent.ofStatic(
            //        new GameProfile(((Entity) this).getUuid(), "", new PropertyMap(ImmutableMultimap.of("textures", this.getSkin())))
            //)));
            //data.add(DataTracker.SerializedEntry.of(MannequinEntityAccessor.getDESCRIPTION(), Optional.empty()));
        }
    }


    default void onTrackingStopped(ServerPlayer player) {
        player.connection.send(new ClientboundPlayerInfoRemovePacket(List.of(((Entity) this).getUUID())));
    }

    @Override
    default void onEntityTrackerTick(Set<ServerPlayerConnection> listeners) {
        var e = (AbstractIllager) this;
        if (e.getArmPose() == AbstractIllager.IllagerArmPose.SPELLCASTING) {
            var packet = new ClientboundAnimatePacket(e, e.getRandom().nextBoolean() ? 0 : 3);

            for (var p : listeners) {
                p.send(packet);
            }
        } else if (this instanceof Stunnable s && s.getStunnedState() && e.tickCount % 5 == 0) {
            var packet = new ClientboundLevelParticlesPacket(SpellParticleOption.create(ParticleTypes.EFFECT, 0xFFFFFF, 1), false, false, e.getX(), e.getEyeY(), e.getZ(), 1, 1, 1, 1, 0);
            for (var p : listeners) {
                p.send(packet);
            }
        }

        var b = BANNER_ELEMENTS.get(this);
        b.setCustomName(e.getCustomName());
        b.setCustomNameVisible(e.isCustomNameVisible());
        var stack = e.getItemBySlot(EquipmentSlot.HEAD);
        if (stack.is(ItemTags.BANNERS) && !e.isDeadOrDying()) {
            b.setItem(stack);
            b.setYaw(e.getYHeadRot());
            b.setPitch(e.getXRot());
        } else {
            b.setItem(ItemStack.EMPTY);
        }
        if (b.isDirty()) {
            b.tick();
        }
    }

    @Override
    default EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.PLAYER;
        //return EntityType.MANNEQUIN;
    }


    Property getSkin();
}
