package nz.co.nznetwork.bungeeplugin.subplugins;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import nz.co.nznetwork.bungeeplugin.BungeeSubplugin;
import nz.co.nznetwork.bungeeplugin.NZNetworkBungee;
import nz.co.nznetwork.bungeeplugin.database.NZNDatabase;
import nz.co.nznetwork.bungeeplugin.database.PlayerHistoryEntry;
import nz.co.nznetwork.bungeeplugin.database.WhiteListEntry;

/**
 *
 * @author NZNetwork
 */
public class WhiteList implements BungeeSubplugin, Listener {

    private NZNDatabase database;
    private ArrayList<WhiteListEntry> whitelist;
    private NZNetworkBungee plugin;
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        PlayerHistoryEntry ph;
        try {
            ph = database.getOnlinePlayer(player);
        } catch (SQLException ex) {
            player.disconnect(plugin.getStaticMessage("database error"));
            plugin.getLogger().log(Level.SEVERE, "", ex);
            return;
        }
        WhiteListEntry tempEntry = new WhiteListEntry(ph);
        if(whitelist.contains(tempEntry)) return;
        try {
            tempEntry = database.checkWhiteListForUUID(tempEntry);
            if(tempEntry == null) {
                tempEntry = WhiteListEntry.generateEntry(ph);
                database.addWhiteListEntry(tempEntry);
            } else if(tempEntry.isVerified()) {
                whitelist.add(tempEntry);
                return;
            }
        } catch(SQLException ex) {
            player.disconnect(plugin.getStaticMessage("database error"));
            plugin.getLogger().log(Level.SEVERE, "", ex);
            return;
        }
        TextComponent message = new TextComponent("You need to register online\n");
        message.setColor(ChatColor.RED);
        TextComponent message2 = new TextComponent("Your signup code is: ");
        message2.setColor(ChatColor.WHITE);
        TextComponent message3 = new TextComponent(tempEntry.getCode()+"\n");
        message3.setColor(ChatColor.DARK_GREEN);
        TextComponent message4 = new TextComponent("\nSignup Here: ");
        message4.setColor(ChatColor.WHITE);
        TextComponent message5 = new TextComponent("nznetwork.co.nz/signup");
        message5.setColor(ChatColor.DARK_AQUA);
        message5.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.nznetwork.co.nz/"));
        
        message.addExtra(message2);
        message.addExtra(message3);
        message.addExtra(message4);
        message.addExtra(message5);
        
        player.disconnect(message);
        
    }

    @Override
    public void onEnable(NZNetworkBungee bungee, NZNDatabase database) {
        bungee.getProxy().getPluginManager().registerListener(bungee, this);
        this.database = database;
        this.plugin = bungee;
        whitelist = database.loadWhiteList();
        bungee.getLogger().info("NZN White List Enabled");
    }

    @Override
    public void onDisable() {}
    
}
