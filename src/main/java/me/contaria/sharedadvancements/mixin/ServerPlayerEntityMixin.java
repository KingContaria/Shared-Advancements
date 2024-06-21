package me.contaria.sharedadvancements.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @ModifyExpressionValue(
            method = {
                    "tick",
                    "getAdvancementTracker"
            },
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;advancementTracker:Lnet/minecraft/advancement/PlayerAdvancementTracker;",
                    opcode = Opcodes.GETFIELD
            )
    )
    private PlayerAdvancementTracker setSharedAdvancementTrackerOwner(PlayerAdvancementTracker sharedAdvancementTracker) {
        sharedAdvancementTracker.setOwner((ServerPlayerEntity) (Object) this);
        return sharedAdvancementTracker;
    }
}
