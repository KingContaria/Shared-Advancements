package me.contaria.sharedadvancements.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.contaria.sharedadvancements.interfaces.IPlayerAdvancementTracker;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin implements IPlayerAdvancementTracker {

    @Shadow
    @Final
    private PlayerManager field_25325;

    @Shadow
    @Final
    private Map<Advancement, AdvancementProgress> advancementToProgress;

    @Shadow
    @Final
    private Set<Advancement> visibleAdvancements;

    @WrapOperation(
            method = "sendUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"
            )
    )
    private void sendSharedAdvancementPackets(ServerPlayNetworkHandler networkHandler, Packet<?> packet, Operation<Void> original) {
        for (ServerPlayerEntity player : this.field_25325.getPlayerList()) {
            original.call(player.networkHandler, packet);
        }
    }

    @Override
    public void sharedAdvancements$sendInitialData(ServerPlayerEntity player) {
        Map<Identifier, AdvancementProgress> toSetProgress = new HashMap<>();
        for (Map.Entry<Advancement, AdvancementProgress> progress : this.advancementToProgress.entrySet()) {
            if (!this.visibleAdvancements.contains(progress.getKey())) {
                continue;
            }
            toSetProgress.put(progress.getKey().getId(), progress.getValue());
        }
        player.networkHandler.sendPacket(new AdvancementUpdateS2CPacket(true, new LinkedHashSet<>(this.visibleAdvancements), Collections.emptySet(), toSetProgress));
    }
}
