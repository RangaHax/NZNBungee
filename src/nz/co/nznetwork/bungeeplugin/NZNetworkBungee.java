package nz.co.nznetwork.bungeeplugin;

import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import nz.co.nznetwork.bungeeplugin.database.NZNDatabase;
import nz.co.nznetwork.bungeeplugin.database.PlayerHistoryEntry;
import nz.co.nznetwork.bungeeplugin.subplugins.PlayerHistory;
import nz.co.nznetwork.bungeeplugin.subplugins.WhiteList;

/**
 *
 * @author NZNetwork
 */
public class NZNetworkBungee extends Plugin {

    ArrayList<BungeeSubplugin> subplugins;
    NZNDatabase database;
    PlayerHistory players;
    
    @Override
    public void onEnable() {
        subplugins = new ArrayList<BungeeSubplugin>();
        database = new NZNDatabase(getLogger());
        players = new PlayerHistory();
        getProxy().getPluginManager().registerCommand(this, new BungeeTestCommand());
        
        
        subplugins.add(players);//this should always be index 0
        subplugins.add(new WhiteList());
        for(BungeeSubplugin plugin: subplugins) plugin.onEnable(this, database);
    }
    
    @Override
    public void onDisable() {
        for(BungeeSubplugin plugin: subplugins) plugin.onDisable();
        database.shutdown();
    }
    
    public PlayerHistoryEntry getOnlinePlayerHistory(String UUID) {
        return players.getOnlinePlayer(UUID);
    }
    
    public TextComponent getStaticMessage(String message) {
        if(message.equals("database error")) {
            TextComponent text = new TextComponent("Database Error, please try again later");
            text.setColor(ChatColor.RED);
            return text;
        }
        return new TextComponent(message);
    }
}
