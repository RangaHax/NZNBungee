package nz.co.nznetwork.bungeeplugin.database;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author NZNetwork
 */
public class PlayerHistoryEntry {
    
    private final int ID; public int getID() {return ID;}
    private final String UUID; public String getUUID() {return UUID;}
    private final String name; public String getName() {return name;}
    private Timestamp lastLogin; public Timestamp getTimestamp() {return lastLogin;}
    
    public PlayerHistoryEntry(int id, String UUID, String name, Timestamp lastLogin) {
        this.ID = id;
        this.UUID = UUID;
        this.name = name;
        this.lastLogin = lastLogin;
    }
    public void updateTimestamp() {
        lastLogin = new Timestamp(Calendar.getInstance().getTime().getTime());
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof PlayerHistoryEntry) {
            PlayerHistoryEntry object = (PlayerHistoryEntry) o;
            return object.getID() == ID;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.ID;
        return hash;
    }
    
}
