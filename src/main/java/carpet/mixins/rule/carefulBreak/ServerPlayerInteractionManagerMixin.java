package carpet.mixins.rule.carefulBreak;

import carpet.CarpetSettings;
import carpet.helpers.CarefulBreakHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.ServerPlayerInteractionManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @WrapOperation(
        method = "tryMineBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;afterMinedByPlayer(Lnet/minecraft/world/World;Lnet/minecraft/entity/living/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/BlockState;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/item/ItemStack;)V"
        )
    )
    private void setCarefulBreakPlayer(Block instance, World world, PlayerEntity player, BlockPos pos, BlockState state,
                                       BlockEntity blockEntity, ItemStack stack, Operation<Void> original) {
        if (CarpetSettings.carefulBreak) {
            try {
                CarefulBreakHelper.miningPlayer.set(this.player);
                original.call(instance, world, player, pos, state, blockEntity, stack);
            } finally {
                CarefulBreakHelper.miningPlayer.set(null);
            }
        } else {
            original.call(instance, world, player, pos, state, blockEntity, stack);
        }
    }

}
