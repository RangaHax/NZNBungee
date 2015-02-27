package nz.co.nznetwork.bungeeplugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.events.MappingEndEvent;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import nz.co.nznetwork.bungeeplugin.configs.MainConfig;

public class InstallScript {

	private static final String CHARSET = "utf8";
	
	private static String createServersTable = 
			"CREATE TABLE IF NOT EXISTS `servers` ("+
					"`name` varchar(20) NOT NULL,"+
					"`warmup` int(11) NOT NULL DEFAULT '0',"+
					"`cooldown` int(11) NOT NULL DEFAULT '0',"+
					"`maxhomes` int(11) NOT NULL DEFAULT '-1',"+
					"PRIMARY KEY (`name`)"+
				") ENGINE=InnoDB  DEFAULT CHARSET="+CHARSET+";";
	private static String createHomesTable = 
			"CREATE TABLE IF NOT EXISTS `homes` ("+
					"`uuid` varchar(36) NOT NULL,"+
					"`name` varchar(20) NOT NULL DEFAULT '[Default]',"+
					"`server` varchar(20) NOT NULL,"+
					"`world` varchar(20) NOT NULL,"+
					"`x` DOUBLE NOT NULL,"+
					"`y` DOUBLE NOT NULL,"+
					"`z` DOUBLE NOT NULL,"+
					"`yaw` FLOAT NOT NULL,"+
					"`pitch` FLOAT NOT NULL,"+
					"PRIMARY KEY (`uuid`, `name`),"+
					"CONSTRAINT FK_homes_servers FOREIGN KEY (name) REFERENCES servers(name) ON UPDATE CASCADE ON DELETE CASCADE"+
				") ENGINE=InnoDB  DEFAULT CHARSET="+CHARSET+";";
	
	private static String createPlayerHistoryTable =
			"CREATE TABLE IF NOT EXISTS `playerhistory` ("+
					"`phid` int(11) unsigned NOT NULL AUTO_INCREMENT,"+
					"`uuid` varchar(36) NOT NULL,"+
					"`playername` varchar(16) NOT NULL,"+
					"`timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"+
					"PRIMARY KEY (`phid`)"+
				") ENGINE=InnoDB  DEFAULT CHARSET="+CHARSET+" AUTO_INCREMENT=1;";
	private static String updatePlayerHistory =
			"INSERT INTO `playerhistory` (`phid`, `uuid`, `playername`, `timestamp`) VALUES"+
					"(1, '00000000-0000-0000-0000-000000000000', 'Console', '0000-00-00 00:00:00');";
	
	public static void install() {
		try {
			Connection connection = DriverManager.getConnection( "jdbc:mysql://" + MainConfig.host + ":" + MainConfig.port + "/" + MainConfig.database, MainConfig.username, MainConfig.password );
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(createServersTable);
			stmt.executeUpdate(createHomesTable);
			
			stmt.executeUpdate(createPlayerHistoryTable);
			stmt.executeUpdate(updatePlayerHistory);
			Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
			for(Entry<String, ServerInfo> me : servers.entrySet()) {
				String server = me.getValue().getName();
				stmt.executeUpdate("INSERT INTO servers (`name`) VALUES ('"+server+"')");
			}

			
//			sql = "INSERT INTO `bancategories` (`catID`, `catMessage`) VALUES";
//			sql += "(1, 'Language'), (2, 'Griefing another players creation'), (3, 'PvP - Killing another player'), (4, 'Arguing with another player in public chat'),";
//			sql += "(5, 'Spam in public chat'), (6, 'Advertising'), (7, 'Undefined')";
//			stmt = con.prepareStatement(sql);
//			stmt.executeUpdate();
//			stmt.close();
//			
//			
//			sql = "CREATE TABLE IF NOT EXISTS `categoryalias` (";
//			sql += "`catID` int(11) unsigned NOT NULL,";
//			sql += "`catLabel` varchar(15) NOT NULL,";
//			sql += "PRIMARY KEY (`catID`,`catLabel`),";
//			sql += "CONSTRAINT FK_catID FOREIGN KEY (catID) REFERENCES bancategories(catID) ON UPDATE CASCADE ON DELETE CASCADE)";
//			sql += "ENGINE=InnoDB  DEFAULT CHARSET="+CHARSET+";";
//			stmt = con.prepareStatement(sql);
//			stmt.execute();
//			stmt.close();
//			
//			sql = "INSERT INTO `categoryalias` (`catID`, `catLabel`) VALUES";
//			sql += "(1, 'Language'), (1, 'Swearing'), (2, 'Griefing'), (2, 'Looting'), (2, 'Theft'), (2, 'Grief'), (3, 'PvP'), (4, 'Arguing'),";
//			sql += "(5, 'Spam'), (6, 'Advertising'), (7, 'Other')";
//			stmt = con.prepareStatement(sql);
//			stmt.executeUpdate();
//			stmt.close();
//			
//			
//			sql = "CREATE TABLE IF NOT EXISTS `onlineplayers` (";
//			sql += "`phid` int(11) unsigned NOT NULL,";
//			sql += "PRIMARY KEY (`phid`),";
//			sql += "CONSTRAINT FK_onlineplayers FOREIGN KEY (phid) REFERENCES playerhistory(phid) ON UPDATE CASCADE ON DELETE CASCADE)";
//			sql += "ENGINE=InnoDB  DEFAULT CHARSET="+CHARSET+";";
//			stmt = con.prepareStatement(sql);
//			stmt.execute();
//			stmt.close();
//			
//			
//			sql = "CREATE TABLE IF NOT EXISTS `bans` (";
//			sql += "`banID` int(11) unsigned NOT NULL AUTO_INCREMENT,";
//			sql += "`phid` int(11) unsigned NOT NULL,";
//			sql += "`catID` int(11) unsigned NOT NULL,";
//			sql += "`reason` tinytext NOT NULL,";
//			sql += "`bannedby` int(11) unsigned NOT NULL,";
//			sql += "`time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,";
//			sql += "`banneduntil` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',";
//			sql += "`banned` int(1) NOT NULL DEFAULT '1',"; //0 = not banned, 1 = both banned, 2 = UUID banned, 3 = name banned
//			sql += "PRIMARY KEY (`banID`),";
//			sql += "CONSTRAINT FK_banned_player FOREIGN KEY (phid) REFERENCES playerhistory(phid) ON UPDATE RESTRICT ON DELETE RESTRICT,";
//			sql += "CONSTRAINT FK_banned_banner FOREIGN KEY (bannedby) REFERENCES playerhistory(phid) ON UPDATE RESTRICT ON DELETE RESTRICT,";
//			sql += "CONSTRAINT FK_ban_catID FOREIGN KEY (catID) REFERENCES bancategories(catID) ON UPDATE CASCADE ON DELETE CASCADE)";
//			sql += "ENGINE=InnoDB  DEFAULT CHARSET="+CHARSET+";";
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
