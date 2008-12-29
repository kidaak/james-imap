package org.apache.james.imap.jpa.om.map;

import org.apache.james.imap.jpa.om.Init;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.MapBuilder;
import org.apache.torque.map.TableMap;

/**
 * This class was autogenerated by Torque on:
 * 
 * [Sun Dec 09 17:45:09 GMT 2007]
 * 
 */
public class MessageBodyMapBuilder implements MapBuilder {
    /**
     * The name of this class
     */
    public static final String CLASS_NAME = MessageBodyMapBuilder.class.getName();

    /**
     * The database map.
     */
    private DatabaseMap dbMap = null;

    /**
     * Tells us if this DatabaseMapBuilder is built so that we don't have to
     * re-build it every time.
     * 
     * @return true if this DatabaseMapBuilder is built
     */
    public boolean isBuilt() {
        return (dbMap != null);
    }

    /**
     * Gets the databasemap this map builder built.
     * 
     * @return the databasemap
     */
    public DatabaseMap getDatabaseMap() {
        return this.dbMap;
    }

    /**
     * The doBuild() method builds the DatabaseMap
     * 
     * @throws TorqueException
     */
    public synchronized void doBuild() throws TorqueException {
        if (isBuilt()) {
            return;
        }
        dbMap = Torque.getDatabaseMap("mailboxmanager");

        dbMap.addTable("message_body");
        TableMap tMap = dbMap.getTable("message_body");
        Init.populateMessageBody(tMap);
    }

}
