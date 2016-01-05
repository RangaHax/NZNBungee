package nz.co.nznetwork.bungeeplugin.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author NZNetwork
 */
public class NZNDatabase {

    private String url = "jdbc:mysql://localhost:3306/nznetwork";
    private String user = "root";
    private String password = "";

    private Logger log;
    
    private ArrayList<Connection> activeConnections = new ArrayList<Connection>();
    private ArrayList<Connection> freeConnections = new ArrayList<Connection>();
    private int connectionCounter = 0;

    private Connection getConnection() throws SQLException {
        Connection con;
        if (freeConnections.isEmpty()) {
            con = DriverManager.getConnection(url, user, password);
            connectionCounter++;
            log.log(Level.INFO, "NZN Database - Creation Database Connection #{0}", connectionCounter);
        } else {
            con = freeConnections.remove(0);
        }
        activeConnections.add(con);
        return con;
    }
    private void releaseConnection(Connection con) {
        activeConnections.remove(con);
        try {
            if(!con.isClosed())
                freeConnections.add(con);
        } catch (SQLException ex) {}
    }

    public NZNDatabase(Logger log) {
        this.log = log;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            log.log(Level.SEVERE, "", ex);
        }
    }
    public void shutdown() {
        for(Connection con : freeConnections) {
            try {
                con.close();
            } catch (SQLException ex) {
                log.log(Level.WARNING, "", ex);
            }
        }
        freeConnections.clear();
    }

    //PlayerHistory
    public PlayerHistoryEntry getPlayer(int id) throws SQLException {
        PlayerHistoryEntry result = null;
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement("SELECT * FROM playerhistory WHERE id=?");
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            result = new PlayerHistoryEntry(rs.getInt("id"), rs.getString("uuid"), rs.getString("name"), rs.getTimestamp("lastlogin"));
        }
        rs.close();
        st.close();
        releaseConnection(con);
        return result;
    }
    public PlayerHistoryEntry getOnlinePlayer(ProxiedPlayer player) throws SQLException {
        return getOnlinePlayer(player.getUniqueId().toString(), player.getName());
    }
    public PlayerHistoryEntry getOnlinePlayer(String UUID, String name) throws SQLException {
        PlayerHistoryEntry result = null;
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement("SELECT * FROM playerhistory WHERE uuid=? AND name=? ORDER BY lastlogin DESC LIMIT 1");
        st.setString(1, UUID.replace("-", ""));
        st.setString(2, name);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            result = new PlayerHistoryEntry(rs.getInt("id"), rs.getString("uuid"), rs.getString("name"), rs.getTimestamp("lastlogin"));
            rs.close();
            st.close();
            releaseConnection(con);
            return result;
        }
        rs.close();
        st.close();
        st = con.prepareStatement("INSERT INTO playerhistory(uuid,name) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
        st.setString(1, UUID.replace("-", ""));
        st.setString(2, name);
        st.executeUpdate();
        rs = st.getGeneratedKeys();
        rs.next();
        result = getPlayer(rs.getInt(1));
        rs.close();
        st.close();
        releaseConnection(con);
        return result;
    }
    public void updateOnlinePlayer(PlayerHistoryEntry e) {
        try {
            Connection con = getConnection();
            PreparedStatement st = con.prepareStatement("UPDATE playerhistory SET lastlogin=?  WHERE id=?");
            e.updateTimestamp();
            st.setTimestamp(1, e.getTimestamp());
            st.setInt(2, e.getID());
            st.executeUpdate();
            st.close();
            releaseConnection(con);
        } catch (SQLException ex) {
            log.log(Level.WARNING, "", ex);
        }
    }
    
    //WhiteList
    public ArrayList<WhiteListEntry> loadWhiteList() {
        ArrayList<WhiteListEntry> entries = new ArrayList<WhiteListEntry>();
        try {
            Connection con = getConnection();
            PreparedStatement st = con.prepareStatement("SELECT playerhistory FROM whitelist WHERE verified=1");
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                entries.add(new WhiteListEntry(getPlayer(rs.getInt(1))));
            }
            rs.close();
            st.close();
            releaseConnection(con);
        } catch (SQLException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        return entries;
    }
    public WhiteListEntry checkWhiteListForUUID(WhiteListEntry entry) throws SQLException {
    WhiteListEntry result = null;
        Connection con = getConnection();
        String sql = "SELECT whitelist.playerhistory,whitelist.minecode,whitelist.verified "+
                "FROM whitelist INNER JOIN playerhistory ON whitelist.playerhistory = playerhistory.id "+
                "WHERE playerhistory.uuid=?";
        PreparedStatement st = con.prepareStatement(sql);
        st.setString(1, entry.getUUID());
        ResultSet rs = st.executeQuery();
        if(rs.next()) {
            if(rs.getBoolean("verified")) result = new WhiteListEntry(getPlayer(rs.getInt(1)));
            else result = new WhiteListEntry(getPlayer(rs.getInt(1)),rs.getString("minecode"));
        }
        rs.close();
        st.close();
        releaseConnection(con);
        return result;
    }
    public void addWhiteListEntry(WhiteListEntry entry) throws SQLException {
        Connection con = getConnection();
        PreparedStatement st = con.prepareStatement("INSERT INTO whitelist(playerhistory,minecode) VALUES (?,?)");
        st.setInt(1, entry.getHistoryKey());
        st.setString(2, entry.getCode());
        st.executeUpdate();
        st.close();
        releaseConnection(con);
    }

//    public static void main(String[] arsg) {
//        NZNDatabase nd = new NZNDatabase();
//        nd.test();
//    }
//
//    public void test() {
//        try {
//            System.out.println("Free: " + freeConnections.size() + "\tActive: " + activeConnections.size());
//            Connection con1 = getConnection();
//            System.out.println("Free: " + freeConnections.size() + "\tActive: " + activeConnections.size());
//            Connection con2 = getConnection();
//            System.out.println("Free: " + freeConnections.size() + "\tActive: " + activeConnections.size());
//            releaseConnection(con1);
//            System.out.println("Free: " + freeConnections.size() + "\tActive: " + activeConnections.size());
//            Connection con3 = getConnection();
//            System.out.println("Free: " + freeConnections.size() + "\tActive: " + activeConnections.size());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
