package com.jd.seckill;

public class BaseUtil {
    public static final int MAC_OS = 1;
    public static final int WINDOWS_OS = 2;
    public static final int LINUX_OS = 3;

    public static int getCurrentSystem(){
        String osName = System.getProperty("os.name");
        System.out.println(osName);
        if (osName.startsWith("Mac OS")) {
            return MAC_OS;
        } else if (osName.startsWith("Windows")) {
            return WINDOWS_OS;
        } else {
            return LINUX_OS;
        }
    }

}
