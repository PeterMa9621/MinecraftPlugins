package betterWeapon.util;

import java.util.SplittableRandom;

public class Util {
    private static SplittableRandom random = new SplittableRandom();
    public static int getRandomInt(int max) {
        return random.nextInt(max);
    }
}
