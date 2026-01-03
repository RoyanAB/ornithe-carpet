package carpet.mixins.log.loggers.mobcaps;

import carpet.utils.SpawnReporter;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.living.mob.MobCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    @Definition(id = "mobCategory", local = @Local(type = MobCategory.class))
    @Definition(id = "getCap", method = "Lnet/minecraft/entity/living/mob/MobCategory;getCap()I")
    @Definition(id = "MOB_CAPACITY_CHUNK_AREA", field = "Lnet/minecraft/world/NaturalSpawner;MOB_CAPACITY_CHUNK_AREA:I")
    @Expression("mobCategory.getCap() * ? / MOB_CAPACITY_CHUNK_AREA")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"), remap = false)
    private int logMobCap(int cap, @Local ServerWorld world, @Local MobCategory mobCategory) {
        int dim = world.dimension.getType().getId();
        // only get the mobcap when mobcap is updating, not modifying
        SpawnReporter.MOB_CAPS.get(dim).put(mobCategory, cap);
        return cap;
    }
}
