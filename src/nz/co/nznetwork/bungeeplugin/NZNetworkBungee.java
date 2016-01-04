package nz.co.nznetwork.bungeeplugin;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author NZNetwork
 */
public class NZNetworkBungee extends Plugin {

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerCommand(this, new BungeeTestCommand());
    }
    
}
