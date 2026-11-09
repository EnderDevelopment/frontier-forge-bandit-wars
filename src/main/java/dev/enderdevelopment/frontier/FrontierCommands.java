package dev.enderdevelopment.frontier;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public final class FrontierCommands {
    private FrontierCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
            CommandManager.literal("frontier")
                .then(CommandManager.literal("status").executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    player.sendMessage(Text.literal("Thirst: " + FrontierState.thirst(player) / 10 + "% | Gang: " + (FrontierState.gang(player).isEmpty() ? "none" : FrontierState.gang(player))), false);
                    return 1;
                }))
                .then(CommandManager.literal("drink").executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    FrontierState.drink(player);
                    player.sendMessage(Text.literal("You drink clean water."), false);
                    return 1;
                }))
                .then(CommandManager.literal("gang").then(CommandManager.argument("name", StringArgumentType.word()).executes(context -> {
                    var player = context.getSource().getPlayerOrThrow();
                    String name = StringArgumentType.getString(context, "name");
                    FrontierState.joinGang(player, name);
                    player.sendMessage(Text.literal("You joined the " + name + " gang."), false);
                    return 1;
                })))
        ));
    }
}
