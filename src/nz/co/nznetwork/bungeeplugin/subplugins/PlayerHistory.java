package nz.co.nznetwork.bungeeplugin.subplugins;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import nz.co.nznetwork.bungeeplugin.BungeeSubplugin;
import nz.co.nznetwork.bungeeplugin.NZNetworkBungee;
import nz.co.nznetwork.bungeeplugin.database.NZNDatabase;
import nz.co.nznetwork.bungeeplugin.database.PlayerHistoryEntry;

/**
 *
 * @author NZNetwork
 */
public class PlayerHistory implements BungeeSubplugin, Listener {
    private ArrayList<PlayerHistoryEntry> onlinePlayers;
    private NZNDatabase database;
    private NZNetworkBungee plugin;
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer p = event.getPlayer();
        PlayerHistoryEntry entry;
        try {
            entry = database.getOnlinePlayer(p.getUniqueId().toString().replaceAll("-", ""), p.getName());
            onlinePlayers.add(entry);
        } catch (SQLException ex) {
            p.disconnect(plugin.getStaticMessage("database error"));
            plugin.getLogger().log(Level.SEVERE, "", ex);
        }
    }
    
    @EventHandler
    public void playerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer p = event.getPlayer();
        PlayerHistoryEntry entry = getOnlinePlayer(p.getUniqueId().toString());
        database.updateOnlinePlayer(entry);
        onlinePlayers.remove(entry);
    }
    public PlayerHistoryEntry getOnlinePlayer(String UUID) {
        String id = UUID.replace("-", "");
        for(PlayerHistoryEntry e : onlinePlayers) {
            if(e.getUUID().equals(id)) return e;
        }
        return null;
    }
    
    
    @Override
    public void onEnable(NZNetworkBungee bungee, NZNDatabase database) {
        bungee.getProxy().getPluginManager().registerListener(bungee, this);
        this.database = database;
        this.plugin = bungee;
        onlinePlayers = new ArrayList<PlayerHistoryEntry>();
        Collection<ProxiedPlayer> players = bungee.getProxy().getPlayers();
        for(ProxiedPlayer p : players) {
            try {
                PlayerHistoryEntry entry = database.getOnlinePlayer(p.getUniqueId().toString().replaceAll("-", ""), p.getName());
                onlinePlayers.add(entry);
            } catch (SQLException ex) {
                p.disconnect(plugin.getStaticMessage("database error"));
                plugin.getLogger().log(Level.SEVERE, "", ex);
            }
        }
        bungee.getLogger().info("NZN Player History Enabled");
    }

    @Override
    public void onDisable() {
        onlinePlayers.clear();
    }
}
