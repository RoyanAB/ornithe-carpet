package carpet.mixins.rule.flippinCactus;

import carpet.helpers.BlockRotator;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlock.class)
public class HopperBlockMixin {
    @Inject(method = "getPlacementState", at = @At(value = "HEAD"))
    private void injectGetPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity, CallbackInfoReturnable<BlockState> cir, @Local(argsOnly = true) Direction direction) {
        if (BlockRotator.flippinEligibility(entity))
            dir = dir.getOpposite();
    }
}
