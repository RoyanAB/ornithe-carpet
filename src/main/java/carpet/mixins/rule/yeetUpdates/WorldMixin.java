package carpet.mixins.rule.yeetUpdates;

import carpet.CarpetSettings;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class WorldMixin {
    @Inject(method = "updateNeighborComparators", at = @At("HEAD"), cancellable = true)
    public void yeetComparatorUpdates(BlockPos pos, Block block, CallbackInfo ci) {
        if (CarpetSettings.yeetComparatorUpdates) ci.cancel();
    }

    @Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
    public void yeetNeighborUpdates(BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci) {
        if (CarpetSettings.yeetNeighborUpdates) ci.cancel();
    }

    @Inject(method = "neighborStateChanged", at = @At("HEAD"), cancellable = true)
    public void yeetObserverUpdates(BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci) {
        if (CarpetSettings.yeetObserverUpdates) ci.cancel();
    }

    @WrapWithCondition(
            method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;I)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;updateObservers(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"
            )
    )
    private boolean skipObserverUpdate(World instance, BlockPos pos, Block block, @Local(argsOnly = true) int flags) {
        return (flags & 128) == 0;
    }

    @WrapOperation(
            method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;I)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/WorldChunk;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;)Lnet/minecraft/block/state/BlockState;"
            )
    )
    private BlockState skipUpdates(WorldChunk instance, BlockPos pos, BlockState state, Operation<BlockState> original, @Local(argsOnly = true) int flags) {
        if ((flags & 128) != 0) {
            // use global flags to skip update
            // carpet.mixins.rule.yeetUpdates.WorldChunkMixin
            boolean tempInitUpdateFlag = CarpetSettings.yeetInitialUpdates;
            boolean tempRmUpdateFlag = CarpetSettings.yeetRemovalUpdates;

            try {
                CarpetSettings.yeetInitialUpdates = true;
                CarpetSettings.yeetRemovalUpdates = true;

                return original.call(instance, pos, state);
            } finally {
                CarpetSettings.yeetInitialUpdates = tempInitUpdateFlag;
                CarpetSettings.yeetRemovalUpdates = tempRmUpdateFlag;
            }
        }

        return original.call(instance, pos, state);
    }
}
