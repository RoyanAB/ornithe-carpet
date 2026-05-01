package carpet.mixins.rule.flippinCactus;

import carpet.helpers.BlockRotator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow
    public float yaw;

    @Inject(method = "getHorizontalFacing", at = @At(value = "RETURN"), cancellable = true)
    private void injectGetHorizontalFacing(CallbackInfoReturnable<Direction> cir) {
        if (BlockRotator.flippinEligibility((Entity) (Object) this))
            cir.setReturnValue(Direction.byIdHorizontal(MathHelper.floor((double) (this.yaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite());
    }
}
