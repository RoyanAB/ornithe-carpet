package carpet.helpers;

import net.minecraft.server.entity.living.player.ServerPlayerEntity;

public final class CarefulBreakHelper {
    public final static ThreadLocal<ServerPlayerEntity> miningPlayer = new ThreadLocal<>();
}
