package dev.enderdevelopment.frontier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public final class FrontierReckoning implements ModInitializer {
    @Override
    public void onInitialize() {
        FrontierState.register();
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> FrontierState.clear());
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) FrontierState.tick(player);
        });
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) FrontierState.exert(serverPlayer, 3);
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) FrontierState.exert(serverPlayer, 2);
            return ActionResult.PASS;
        });
        FrontierCommands.register();
    }
}
