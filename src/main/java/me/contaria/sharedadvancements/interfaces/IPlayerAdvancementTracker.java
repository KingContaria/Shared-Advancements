package me.contaria.sharedadvancements.interfaces;

import net.minecraft.server.network.ServerPlayerEntity;

public interface IPlayerAdvancementTracker {
    void sharedAdvancements$sendInitialData(ServerPlayerEntity player);
}
