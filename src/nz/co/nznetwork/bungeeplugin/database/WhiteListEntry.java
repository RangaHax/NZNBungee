package nz.co.nznetwork.bungeeplugin.database;

import java.util.Objects;

/**
 * @author NZNetwork
 */
public class WhiteListEntry {
    
    private PlayerHistoryEntry player; public int getHistoryKey() {return player.getID();}
    public String getUUID() { return player.getUUID(); }
    public String getName() { return player.getName(); }
    private String minecode; public String getCode() { return minecode; }
    private boolean verified = true; public boolean isVerified() { return verified; }
    
    public static final int CODE_LENGTH = 5;
    
    public WhiteListEntry(PlayerHistoryEntry player) {
        this.player = player;
    }
    public WhiteListEntry(PlayerHistoryEntry player, String minecode) {
        this.player = player;
        this.minecode = minecode;
        verified = false;
    }
    public static WhiteListEntry generateEntry(PlayerHistoryEntry entry) {
        StringBuilder buffer = new StringBuilder();
	String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	int charactersLength = characters.length();
	for (int i = 0; i < CODE_LENGTH; i++) {
	    double index = Math.random() * charactersLength;
	    buffer.append(characters.charAt((int) index));
	}
        return new WhiteListEntry(entry, buffer.toString());
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof WhiteListEntry) {
            WhiteListEntry object = (WhiteListEntry) o;
            return player.getUUID().equals(object.getUUID());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.player.getUUID());
        return hash;
    }
    
}
