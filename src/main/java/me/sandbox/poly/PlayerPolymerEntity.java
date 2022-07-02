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
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.List;
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

    @Override
    default EntityType<?> getPolymerEntityType() {
        return EntityType.PLAYER;
    }


    Property getSkin();
}
