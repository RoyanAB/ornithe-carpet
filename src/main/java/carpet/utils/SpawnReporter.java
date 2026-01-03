package carpet.utils;

import net.minecraft.entity.living.mob.MobCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpawnReporter {
    public static final HashMap<Integer, HashMap<MobCategory, Integer>> MOB_CAPS = new HashMap<>();

    static {
        MOB_CAPS.put(-1, new HashMap<>());
        MOB_CAPS.put(0, new HashMap<>());
        MOB_CAPS.put(1, new HashMap<>());
    }

    public static List<Text> printMobcapsForDimension(World world, int dim, String name, boolean multiLine) {
        List<Text> list = new ArrayList<>();


        return list;
    }
}
