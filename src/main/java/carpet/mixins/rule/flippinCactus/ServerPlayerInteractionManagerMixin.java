package carpet.mixins.rule.flippinCactus;

import carpet.helpers.BlockRotator;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Inject(
            method = "useBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/state/BlockState;getBlock()Lnet/minecraft/block/Block;",
                    ordinal = 1
            ),
            cancellable = true
    )
    private void flipBlockWithCactus(PlayerEntity player, World world, ItemStack stack, InteractionHand hand, BlockPos pos, Direction face, float dx, float dy, float dz, CallbackInfoReturnable<InteractionResult> cir, @Local BlockState blockState) {

        if (BlockRotator.flipBlockWithCactus(world, pos, blockState, player, hand, face, dx, dy, dz)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
