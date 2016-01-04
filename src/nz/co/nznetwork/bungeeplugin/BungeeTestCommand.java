package nz.co.nznetwork.bungeeplugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author NZNetwork
 */
public class BungeeTestCommand extends Command {

    public BungeeTestCommand() {
        super("bungeetest");
    }
    
    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        commandSender.sendMessage(new ComponentBuilder("Bungee Says Hello!").color(ChatColor.GREEN).create());
    }
    
}
