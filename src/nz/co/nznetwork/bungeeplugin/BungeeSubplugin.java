package nz.co.nznetwork.bungeeplugin;

import nz.co.nznetwork.bungeeplugin.database.NZNDatabase;
/**
 * @author NZNetwork
 */
public interface BungeeSubplugin {
    public void onEnable(NZNetworkBungee bungee, NZNDatabase database);
    public void onDisable();
    
}
