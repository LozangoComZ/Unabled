package net.lozangocomz.unabled;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.WeakHashMap;

public final class NoDropTracker {
    private static final Map<Player, BlockPos> POSITIONS = new WeakHashMap<>();

    private NoDropTracker() {
    }

    public static synchronized void mark(Player player, BlockPos pos) {
        POSITIONS.put(player, pos.immutable());
    }

    public static synchronized void clear(Player player) {
        POSITIONS.remove(player);
    }

    public static synchronized boolean consume(Player player, BlockPos pos) {
        BlockPos flaggedPos = POSITIONS.get(player);
        if (pos.equals(flaggedPos)) {
            POSITIONS.remove(player);
            return true;
        }
        return false;
    }
}
