package net.lozangocomz.unabled.mixin;

import net.lozangocomz.unabled.RestrictionLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public abstract class MixinBlockBehaviour {
    @Inject(method = "getDestroyProgress", at = @At("RETURN"), cancellable = true)
    private void unabled$applyBreakSpeed(BlockState state, Player player, BlockGetter level, BlockPos pos,
                                            CallbackInfoReturnable<Float> callback) {
        callback.setReturnValue(RestrictionLogic.applyBreakSpeed(player, state, level, pos, callback.getReturnValue()));
    }
}