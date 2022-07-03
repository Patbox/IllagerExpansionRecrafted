package me.sandbox.poly;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import eu.pb4.polymer.api.entity.PolymerEntity;
import eu.pb4.polymer.api.utils.PolymerUtils;
import me.sandbox.mixin.poly.EntityAccessor;
import me.sandbox.mixin.poly.PlayerEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface PlayerPolymerEntity extends PolymerEntity {
    default PlayerListS2CPacket createRemoveFromList() {
        var packet = new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER);
        packet.getEntries().add(new PlayerListS2CPacket.Entry(new GameProfile(((Entity) this).getUuid(), ""), 0, GameMode.ADVENTURE, null, null));
        return packet;
    }

    @Override
    default void onBeforeSpawnPacket(Consumer<Packet<?>> packetConsumer) {
        var packet = new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER);
        var profile = new GameProfile(((Entity) this).getUuid(), "");
        profile.getProperties().put("textures", this.getSkin());
        packet.getEntries().add(new PlayerListS2CPacket.Entry(profile, Integer.MAX_VALUE, GameMode.ADVENTURE, Text.empty(), null));
        packetConsumer.accept(packet);
    }

    @Override
    default void modifyTrackedData(List<DataTracker.Entry<?>> data) {
        data.add(new DataTracker.Entry<>(PlayerEntityAccessor.getPLAYER_MODEL_PARTS(), (byte) 0xFF));
    }

    default void onTrackingStarted(ServerPlayerEntity player) {
        PolymerUtils.schedulePacket(player.networkHandler, this.createRemoveFromList(), 40);
    }


    default void onTrackingStopped(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(this.createRemoveFromList());
    }

    default void onEntityTrackerTick(Set<EntityTrackingListener> listeners) {
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
    default EntityType<?> getPolymerEntityType() {
        return EntityType.PLAYER;
    }


    Property getSkin();
}
