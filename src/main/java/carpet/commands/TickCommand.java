package carpet.commands;

import carpet.CarpetSettings;
import carpet.fakes.MinecraftServerF;
import carpet.helpers.ServerTickRateManager;
import carpet.network.ServerNetworkHandler;
import carpet.utils.Messenger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class TickCommand extends CarpetAbstractCommand {
    @Override
    public String getName() {
        return "tick";
    }

    @Override
    public String getUsage(CommandSource commandSource) {
        return "Usage: /tick <freeze|step|rate|superhot|warp> [options]";
    }

    @Override
    public boolean canUse(MinecraftServer server, CommandSource source) {
        return canUseCommand(source, CarpetSettings.commandTick);
    }

    @Override
    public void run(MinecraftServer minecraftServer, CommandSource commandSource, String[] strings) throws CommandException {
        if (strings.length == 0) {
            throw new IncorrectUsageException(getUsage(commandSource));
        }

        if (strings.length == 1) {
            String action = strings[0].toLowerCase();
            switch (action) {
                case "freeze":
                    toggleFreeze(commandSource, false);
                    break;
                case "step":
                    step(commandSource, 1);
                    break;
                case "rate":
                    queryTps(commandSource);
                    break;
                case "superhot":
                    toggleSuperHot(commandSource);
                    break;
                case "warp":
                    // todo tick warp toggle
                    setWarp(commandSource, 0, null);
                    break;
                default:
                    break;
            }
            return;
        }

        if (strings.length == 2 && "freeze".equalsIgnoreCase(strings[0])) {
            if ("status".equalsIgnoreCase(strings[1])) {
                freezeStatus(commandSource);
            } else if ("deep".equalsIgnoreCase(strings[1])) {
                toggleFreeze(commandSource, true);
            } else if ("on".equalsIgnoreCase(strings[1])) {
                setFreeze(commandSource, false, true);
            } else if ("off".equalsIgnoreCase(strings[1])) {
                setFreeze(commandSource, false, false);
            }
            return;
        }

        if (strings.length == 2 && "step".equalsIgnoreCase(strings[0])) {
            step(commandSource, parseInt(strings[1], 1, 72000));
            return;
        }

        if (strings.length == 2 && "rate".equalsIgnoreCase(strings[0])) {
            setTps(commandSource, (float) parseDouble(strings[1], 0.1F, 10000.0F));
            return;
        }

        if (strings.length == 2 && "warp".equalsIgnoreCase(strings[0])) {
            setWarp(commandSource, parseInt(strings[1], 0), null);
            return;
        }

        if (strings.length == 3 && "freeze".equalsIgnoreCase(strings[0]) && "on".equalsIgnoreCase(strings[1]) && "deep".equalsIgnoreCase(strings[2])) {
            setFreeze(commandSource, true, true);
            return;
        }

        if (strings.length == 3 && "warp".equalsIgnoreCase(strings[0])) {
            setWarp(commandSource, parseInt(strings[1], 1), strings[2]);
        }
    }

    @Override
    public List<String> getSuggestions(MinecraftServer minecraftServer, CommandSource commandSource, String[] strings, @Nullable BlockPos blockPos) {
        if (strings.length == 1) {
            return suggestMatching(strings, "freeze", "step", "rate", "superhot", "warp");
        } else if (strings.length == 2) {
            if ("freeze".equalsIgnoreCase(strings[0]))
                return suggestMatching(strings, "status", "deep", "on", "off");
            if ("step".equalsIgnoreCase(strings[0]))
                return suggestMatching(strings, "20");
            if ("rate".equalsIgnoreCase(strings[0]))
                return suggestMatching(strings, "20");
            if ("warp".equalsIgnoreCase(strings[0]))
                return suggestMatching(strings, "3600", "72000");
        } else if (strings.length == 3) {
            if ("freeze".equalsIgnoreCase(strings[0]) && "on".equalsIgnoreCase(strings[1]))
                return suggestMatching(strings, "deep");
        }
        return Collections.emptyList();
    }

    private static int freezeStatus(CommandSource source) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        if (trm.gameIsPaused()) {
            Messenger.m(source, "gi Freeze Status: Game is " + (trm.deeplyFrozen() ? "deeply " : "") + "frozen");
        } else {
            Messenger.m(source, "gi Freeze Status: Game runs normally");
        }
        return 1;
    }

    private static int setFreeze(CommandSource source, boolean isDeep, boolean freeze) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        trm.setFrozenState(freeze, isDeep);
        if (trm.gameIsPaused()) {
            Messenger.m(source, "gi Game is " + (isDeep ? "deeply " : "") + "frozen");
        } else {
            Messenger.m(source, "gi Game runs normally");
        }
        return 1;
    }

    private static int toggleFreeze(CommandSource source, boolean isDeep) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        return setFreeze(source, isDeep, !trm.gameIsPaused());
    }

    private static int step(CommandSource source, int advance) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        if (trm.gameIsPaused()) {
            trm.stepGameIfPaused(advance);
            Messenger.m(source, "gi Stepping " + advance + " tick" + (advance != 1 ? "s" : ""));
        } else {
            Messenger.m(source, "gi Unable to step the game - the game must be frozen first");
        }
        return 1;
    }

    private static int setTps(CommandSource source, float tps) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        trm.setTickRate(tps, true);
        queryTps(source);
        return (int) tps;
    }

    private static int queryTps(CommandSource source) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();

        Messenger.m(source, "w Current tps is: ", String.format("wb %.1f", trm.tickrate()));
        return (int) trm.tickrate();
    }

    private static int toggleSuperHot(CommandSource source) {
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        trm.setSuperHot(!trm.isSuperHot());
        ServerNetworkHandler.updateSuperHotStateToConnectedPlayers(source.getServer());
        if (trm.isSuperHot()) {
            Messenger.m(source, "gi Superhot enabled");
        } else {
            Messenger.m(source, "gi Superhot disabled");
        }
        return 1;
    }

    private static int setWarp(CommandSource source, int advance, String tail_command) {
        ServerPlayerEntity player;
        if (source instanceof ServerPlayerEntity) {
            player = (ServerPlayerEntity) source;
        } else {
            player = null; // may be null
        }
        ServerTickRateManager trm = ((MinecraftServerF) source.getServer()).getTickRateManager();
        Text message = trm.requestGameToWarpSpeed(player, advance, tail_command, source);
        source.sendMessage(message);
        return 1;
    }
}
