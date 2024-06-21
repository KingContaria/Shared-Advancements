package me.contaria.sharedadvancements.mixin;

import me.contaria.sharedadvancements.interfaces.IPlayerAdvancementTracker;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Unique
    private PlayerAdvancementTracker sharedAdvancementTracker;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void initializeSharedAdvancementTracker(CallbackInfo ci) {
        this.sharedAdvancementTracker = new PlayerAdvancementTracker(
                this.server.getDataFixer(),
                (PlayerManager) (Object) this,
                this.server.getAdvancementLoader(),
                this.server.getSavePath(WorldSavePath.ADVANCEMENTS).resolve("shared-advancements.json").toFile(),
                null
        );
    }

    @Inject(
            method = "getAdvancementTracker",
            at = @At("HEAD"),
            cancellable = true
    )
    private void useSharedAdvancementTracker(ServerPlayerEntity player, CallbackInfoReturnable<PlayerAdvancementTracker> cir) {
        cir.setReturnValue(this.sharedAdvancementTracker);
    }

    @Inject(
            method = "onPlayerConnect",
            at = @At("RETURN")
    )
    private void reloadSharedAdvancementTrackerOnLogin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        ((IPlayerAdvancementTracker) this.sharedAdvancementTracker).sharedAdvancements$sendInitialData(player);
    }

    @Inject(
            method = "saveAllPlayerData",
            at = @At("RETURN")
    )
    private void saveSharedAdvancementTracker(CallbackInfo ci) {
        this.sharedAdvancementTracker.save();
    }
}
