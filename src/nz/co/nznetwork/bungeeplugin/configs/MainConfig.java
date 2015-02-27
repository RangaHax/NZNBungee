package nz.co.nznetwork.bungeeplugin.configs;

import java.io.File;

import configlibrary.Config;

public class MainConfig {
    public static String configpath = File.separator + "plugins" + File.separator + "NZNBungee" + File.separator + "config.yml";
    public static Config config = new Config( configpath );
    
    /* MySQL Information */
    public static String host = config.getString( "database.host", "localhost" );
    public static String database = config.getString( "database.database", "minecraft" );
    public static String port = config.getString( "database.port", "3306" );
    public static String username = config.getString( "database.username", "minecraft" );
    public static String password = config.getString( "database.password", "nznetworkA" );
    public static int threads = config.getInt( "database.threads", 5 );
    
    public static int hardcoreTeleportTime = config.getInt("hardcoreWarmup", 30);
    public static int hardcoreTeleportCool = config.getInt("hardcoreCooldown", 60);
}
