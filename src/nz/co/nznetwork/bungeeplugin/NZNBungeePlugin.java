package nz.co.nznetwork.bungeeplugin;

import net.md_5.bungee.api.plugin.Plugin;

public class NZNBungeePlugin extends Plugin {
	public static NZNBungeePlugin instance;
	
	public void onEnable() {
    	instance = this;
    }
}
