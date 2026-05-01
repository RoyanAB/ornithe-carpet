package carpet.mixins.rule.flippinCactus;

import carpet.helpers.BlockRotator;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Direction.class)
public class DirectionMixin {
    @Inject(method = "nearest", at = @At(value = "RETURN"), cancellable = true)
    private static void injectNearest(BlockPos pos, LivingEntity entity, CallbackInfoReturnable<Direction> cir) {
        if (BlockRotator.flippinEligibility(entity))
            cir.setReturnValue(cir.getReturnValue().getOpposite());
    }
}
