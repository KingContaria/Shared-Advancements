package me.contaria.sharedadvancements.mixin;

import net.minecraft.advancement.criterion.EffectsChangedCriterion;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mixin(EffectsChangedCriterion.Conditions.class)
public abstract class EffectsChangedCriterion$ConditionsMixin {

    @Redirect(
            method = "matches",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/predicate/entity/EntityEffectPredicate;test(Lnet/minecraft/entity/LivingEntity;)Z"
            )
    )
    private boolean shareEffectsAdvancementCheck(EntityEffectPredicate effects, LivingEntity entity) {
        Map<StatusEffect, StatusEffectInstance> statusEffects = new HashMap<>();
        for (ServerPlayerEntity player : Objects.requireNonNull(entity.getServer()).getPlayerManager().getPlayerList()) {
            statusEffects.putAll(player.getActiveStatusEffects());
        }
        return effects.test(statusEffects);
    }
}
