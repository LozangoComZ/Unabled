package net.lozangocomz.unabled.mixin;

import net.lozangocomz.unabled.NoDropTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class MixinBlock {
    @Inject(
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void unabled$preventDrops(BlockState state, Level level, BlockPos pos,
                                                BlockEntity blockEntity, Entity entity, ItemStack tool,
                                                CallbackInfo callback) {
        unabled$cancelDrops(entity, pos, callback);
    }

    private static void unabled$cancelDrops(Entity entity, BlockPos pos, CallbackInfo callback) {
        if (entity instanceof Player player && NoDropTracker.consume(player, pos)) {
            callback.cancel();
        }
    }
}
