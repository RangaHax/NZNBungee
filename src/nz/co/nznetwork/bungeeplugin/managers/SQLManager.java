package nz.co.nznetwork.bungeeplugin.managers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import nz.co.nznetwork.bungeeplugin.InstallScript;
import nz.co.nznetwork.bungeeplugin.NZNBungeePlugin;
import nz.co.nznetwork.bungeeplugin.configs.MainConfig;
import nz.co.nznetwork.bungeeplugin.objects.ConnectionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


public class SQLManager {

    private static ArrayList<ConnectionHandler> connections = new ArrayList<ConnectionHandler>();

    /**
     */
    public static boolean initialiseConnections() {
    	Connection connection = null;
		try {
			connection = DriverManager.getConnection( "jdbc:mysql://" + MainConfig.host + ":" + MainConfig.port + "/" + MainConfig.database, MainConfig.username, MainConfig.password );
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'servers'");
			int count = 0;
			while(rs.next()) count++;
	    	rs.close();
	    	stmt.close();
	    	connection.close();
	    	if(count == 0) InstallScript.install();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
        connection = null;
        for ( int i = 0; i < MainConfig.threads; i++ ) {
            try {
                Class.forName( "com.mysql.jdbc.Driver" );
                connection = DriverManager.getConnection( "jdbc:mysql://" + MainConfig.host + ":" + MainConfig.port + "/" + MainConfig.database, MainConfig.username, MainConfig.password );
            } catch ( SQLException | ClassNotFoundException ex ) {
                System.out.println( ChatColor.DARK_RED + "SQL is unable to conect" );
                return false;
            }
            connections.add( new ConnectionHandler( connection ) );
            connection = null;
        }

        ProxyServer.getInstance().getScheduler().schedule( NZNBungeePlugin.instance, new Runnable() {
            public void run() {
                Iterator<ConnectionHandler> cons = connections.iterator();
                while ( cons.hasNext() ) {
                    ConnectionHandler con = cons.next();
                    if ( con.isOldConnection() ) {
                        con.closeConnection();
                        cons.remove();
                    }
                }
            }
        }, 60, 60, TimeUnit.MINUTES );
        return true;
    }

    /**
     * @return Returns a free connection from the pool of connections. Creates a new connection if there are none available
     */
    private static ConnectionHandler getConnection() {
        for ( ConnectionHandler c : connections ) {
            if ( !c.isUsed() ) {
                return c;
            }
        }
        // create a new connection as none are free
        Connection connection = null;
        ConnectionHandler ch = null;
        try {
            Class.forName( "com.mysql.jdbc.Driver" );
            connection = DriverManager.getConnection( "jdbc:mysql://" + MainConfig.host + ":" + MainConfig.port + "/" + MainConfig.database, MainConfig.username, MainConfig.password );
        } catch ( SQLException | ClassNotFoundException ex ) {
            System.out.println( "SQL is unable to create a new connection" );
        }
        ch = new ConnectionHandler( connection );
        connections.add( ch );
        System.out.println( "Created new sql connection!" );

        return ch;

    }

    /**
     * Any query which does not return a ResultSet object. Such as : INSERT,
     * UPDATE, CREATE TABLE...
     *
     * @param query
     * @throws SQLException
     */
    public static void standardQuery( String query ) throws SQLException {
        ConnectionHandler ch = getConnection();
        standardQuery( query, ch.getConnection() );
        ch.release();
    }

    /**
     * Check whether a field/entry exists in a database.
     *
     * @param query
     * @return Whether or not a result has been found in the query.
     * @throws SQLException
     */
    public static boolean existanceQuery( String query ) {
        boolean check = false;
        ConnectionHandler ch = getConnection();
        try {
            check = sqlQuery( query, ch.getConnection() ).next();
        } catch ( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ch.release();
        return check;
    }

    /**
     * Any query which returns a ResultSet object. Such as : SELECT Remember to
     * close the ResultSet object after you are done with it to free up
     * resources immediately. ----- ResultSet set =
     * sqlQuery("SELECT * FROM sometable;"); set.doSomething(); set.close();
     * -----
     *
     * @param query
     * @return ResultSet
     */
    public static ResultSet sqlQuery( String query ) {
        ResultSet res = null;
        ConnectionHandler ch = getConnection();
        res = sqlQuery( query, ch.getConnection() );
        ch.release();
        return res;
    }

    /**
     * Check whether the table name exists.
     *
     * @param table
     * @return
     */
    public static boolean doesTableExist( String table ) {
        boolean check = false;
        ConnectionHandler ch = getConnection();
        check = checkTable( table, ch.getConnection() );
        ch.release();
        return check;
    }

    protected synchronized static int standardQuery( String query, Connection connection ) throws SQLException {
        Statement statement = null;

        statement = connection.createStatement();

        int rowsUpdated = 0;
        rowsUpdated = statement.executeUpdate( query );

        statement.close();
        int rows = rowsUpdated;
        return rows;
    }

    protected synchronized static ResultSet sqlQuery( String query, Connection connection ) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch ( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ResultSet result = null;
        try {
            result = statement.executeQuery( query );
        } catch ( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    protected synchronized static boolean checkTable( String table, Connection connection ) {
        DatabaseMetaData dbm = null;
        try {
            dbm = connection.getMetaData();
        } catch ( SQLException e2 ) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        ResultSet tables = null;
        try {
            tables = dbm.getTables( null, null, table, null );
        } catch ( SQLException e1 ) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        boolean check = false;
        try {
            check = tables.next();
        } catch ( SQLException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return check;
    }

    public static void closeConnections() {
        for ( ConnectionHandler c : connections ) {
            c.closeConnection();
        }

    }
}