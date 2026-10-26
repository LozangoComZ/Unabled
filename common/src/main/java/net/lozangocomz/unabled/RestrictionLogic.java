package net.lozangocomz.unabled;

import net.lozangocomz.unabled.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class RestrictionLogic {
    private RestrictionLogic() {
    }

    public static float applyBreakSpeed(Player player, BlockState state, BlockGetter level, BlockPos pos, float speed) {
        NoDropTracker.clear(player);
        if (player.isCreative() || Services.PLATFORM.isFakePlayer(player)) {
            return speed;
        }

        Settings settings = Config.get();
        if (state.getDestroySpeed(level, pos) < settings.breakingSpeedThreshold()
                || isIgnored(state, settings)) {
            return speed;
        }

        ItemStack tool = player.getMainHandItem();
        boolean correctTool = tool.isCorrectToolForDrops(state);
        boolean digTool = tool.getItem() instanceof DiggerItem;
        int breakMode = correctTool
                ? settings.requiredToolsMode()
                : digTool ? settings.digToolsMode() : settings.unequippedMode();

        applyDamage(player, tool, digTool, settings);

        if (breakMode == 3) {
            return 0.0F;
        }
        if (breakMode == 2 && player instanceof ServerPlayer) {
            NoDropTracker.mark(player, pos);
        }
        return speed;
    }

    private static void applyDamage(Player player, ItemStack tool, boolean digTool, Settings settings) {
        if (!(player instanceof ServerPlayer serverPlayer) || settings.damagePlayer() <= 0.0) {
            return;
        }

        if ((settings.damageMode() == 2 && tool.isEmpty())
                || (settings.damageMode() == 3 && !digTool)) {
            serverPlayer.hurt(serverPlayer.level().damageSources().generic(), (float) settings.damagePlayer());
        }
    }

    private static boolean isIgnored(BlockState state, Settings settings) {
        for (String entry : settings.ignoredIds()) {
            if (entry.startsWith("#")) {
                ResourceLocation id = ResourceLocation.tryParse(entry.substring(1));
                if (id != null && state.is(TagKey.create(Registries.BLOCK, id))) {
                    return true;
                }
            } else {
                ResourceLocation id = ResourceLocation.tryParse(entry);
                if (id != null && BuiltInRegistries.BLOCK.getKey(state.getBlock()).equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }
}