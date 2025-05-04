package com.sebastian.heartbreaker_pvp.time_limit;

public class TimeLimitManager {
    public static Integer GLOBAL_TIME_LIMIT = 60 * 60;
    public static Boolean GLOBAL_TIME_LIMIT_ENABLED = true;

    private static int secondClock;
    public static void serverTick() {
        secondClock++;
        if(secondClock >= 20) {
            secondClock = 0;
            //Calls Every sec

            if(!GLOBAL_TIME_LIMIT_ENABLED) {
                //Send infiniteTimePacket
            }

        }
    }
}
