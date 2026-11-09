package dev.enderdevelopment.frontier;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FrontierState extends PersistentState {
    private static final String SAVE_KEY = "frontier_reckoning_players";
    private final Map<UUID, PlayerState> players = new HashMap<>();

    private FrontierState() {}

    private FrontierState(NbtCompound nbt) {
        NbtCompound entries = nbt.getCompound("players");
        for (String key : entries.getKeys()) {
            try {
                players.put(UUID.fromString(key), new PlayerState(entries.getCompound(key)));
            } catch (IllegalArgumentException ignored) {
                // Ignore malformed records while preserving valid player data.
            }
        }
    }

    private static FrontierState get(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        return manager.getOrCreate(FrontierState::new, FrontierState::new, SAVE_KEY);
    }

    private PlayerState state(ServerPlayerEntity player) {
        return players.computeIfAbsent(player.getUuid(), ignored -> new PlayerState());
    }

    public static void register() {
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            FrontierState oldData = get(oldPlayer);
            FrontierState newData = get(newPlayer);
            PlayerState oldState = oldData.players.get(oldPlayer.getUuid());
            if (oldState != null) newData.players.put(newPlayer.getUuid(), oldState.copy());
            newData.markDirty();
        });
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound entries = new NbtCompound();
        players.forEach((uuid, state) -> entries.put(uuid.toString(), state.writeNbt()));
        nbt.put("players", entries);
        return nbt;
    }

    public static void exert(ServerPlayerEntity player, int amount) {
        FrontierState data = get(player.getServerWorld());
        PlayerState state = data.state(player);
        state.exertion = Math.min(1000, state.exertion + amount * 10);
        data.markDirty();
    }

    public static void tick(ServerPlayerEntity player) {
        FrontierState data = get(player.getServerWorld());
        PlayerState state = data.state(player);
        state.exertion = Math.max(0, state.exertion - 1);
        if (player.age % 20 != 0) return;
        state.thirst = Math.max(0, state.thirst - 3 - state.exertion / 100);
        player.getHungerManager().addExhaustion(0.012F + state.exertion / 100000.0F);
        if (state.thirst == 0 && player.age % 100 == 0) player.damage(player.getDamageSources().drown(), 1.0F);
        if (state.thirst <= 200 && player.age % 200 == 0) player.sendMessage(net.minecraft.text.Text.literal("You are dangerously dehydrated. Drink water."), true);
        data.markDirty();
    }

    public static int thirst(ServerPlayerEntity player) {
        return MathHelper.clamp(get(player.getServerWorld()).state(player).thirst, 0, 1000);
    }

    public static void drink(ServerPlayerEntity player) {
        FrontierState data = get(player.getServerWorld());
        PlayerState state = data.state(player);
        state.thirst = Math.min(1000, state.thirst + 350);
        data.markDirty();
    }

    public static void joinGang(ServerPlayerEntity player, String name) {
        FrontierState data = get(player.getServerWorld());
        data.state(player).gang = name;
        data.markDirty();
    }

    public static String gang(ServerPlayerEntity player) {
        return get(player.getServerWorld()).state(player).gang;
    }

    private static final class PlayerState {
        private int thirst = 1000;
        private int exertion;
        private String gang = "";

        private PlayerState() {}

        private PlayerState(NbtCompound nbt) {
            thirst = MathHelper.clamp(nbt.getInt("thirst"), 0, 1000);
            exertion = MathHelper.clamp(nbt.getInt("exertion"), 0, 1000);
            gang = nbt.getString("gang");
        }

        private PlayerState copy() {
            PlayerState copy = new PlayerState();
            copy.thirst = thirst;
            copy.exertion = exertion;
            copy.gang = gang;
            return copy;
        }

        private NbtCompound writeNbt() {
            NbtCompound nbt = new NbtCompound();
            nbt.putInt("thirst", thirst);
            nbt.putInt("exertion", exertion);
            nbt.putString("gang", gang);
            return nbt;
        }
    }
}
