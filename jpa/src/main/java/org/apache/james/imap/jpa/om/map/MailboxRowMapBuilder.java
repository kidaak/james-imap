package org.apache.james.imap.jpa.om.map;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.MapBuilder;
import org.apache.torque.map.TableMap;

/**
 * Mailbox Table
 * 
 * This class was autogenerated by Torque on:
 * 
 * [Sun Dec 09 17:45:09 GMT 2007]
 * 
 */
public class MailboxRowMapBuilder implements MapBuilder {
    /**
     * The name of this class
     */
    public static final String CLASS_NAME = MailboxRowMapBuilder.class.getName();

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

        dbMap.addTable("mailbox");
        TableMap tMap = dbMap.getTable("mailbox");
        tMap.setJavaName("MailboxRow");
        tMap
                .setOMClass(org.apache.james.imap.jpa.om.MailboxRow.class);
        tMap
                .setPeerClass(org.apache.james.imap.jpa.om.MailboxRowPeer.class);
        tMap.setDescription("Mailbox Table");
        tMap.setPrimaryKeyMethod(TableMap.NATIVE);
        tMap.setPrimaryKeyMethodInfo("mailbox_SEQ");

        ColumnMap cMap = null;

        // ------------- Column: mailbox_id --------------------
        cMap = new ColumnMap("mailbox_id", tMap);
        cMap.setType(new Long(0));
        cMap.setTorqueType("BIGINT");
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(true);
        cMap.setNotNull(true);
        cMap.setJavaName("MailboxId");
        cMap.setAutoIncrement(true);
        cMap.setProtected(false);
        cMap.setDescription("Mailbox Id");
        cMap.setInheritance("false");
        cMap.setPosition(1);
        tMap.addColumn(cMap);
        // ------------- Column: name --------------------
        cMap = new ColumnMap("name", tMap);
        cMap.setType("");
        cMap.setTorqueType("VARCHAR");
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(true);
        cMap.setJavaName("Name");
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("full-namespace-name");
        cMap.setInheritance("false");
        cMap.setSize(255);
        cMap.setPosition(2);
        tMap.addColumn(cMap);
        // ------------- Column: uid_validity --------------------
        cMap = new ColumnMap("uid_validity", tMap);
        cMap.setType(new Long(0));
        cMap.setTorqueType("BIGINT");
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(true);
        cMap.setJavaName("UidValidity");
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("the last used uid (default 0)");
        cMap.setInheritance("false");
        cMap.setPosition(3);
        tMap.addColumn(cMap);
        // ------------- Column: last_uid --------------------
        cMap = new ColumnMap("last_uid", tMap);
        cMap.setType(new Long(0));
        cMap.setTorqueType("BIGINT");
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(true);
        cMap.setJavaName("LastUid");
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("the last used uid (default 0)");
        cMap.setInheritance("false");
        cMap.setPosition(4);
        tMap.addColumn(cMap);
        // ------------- Column: message_count --------------------
        cMap = new ColumnMap("message_count", tMap);
        cMap.setType(new Integer(0));
        cMap.setTorqueType("INTEGER");
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(false);
        cMap.setJavaName("MessageCount");
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("total message number");
        cMap.setDefault("0");
        cMap.setInheritance("false");
        cMap.setPosition(5);
        tMap.addColumn(cMap);
        // ------------- Column: size --------------------
        cMap = new ColumnMap("size", tMap);
        cMap.setType(new Long(0));
        cMap.setTorqueType("BIGINT");
        cMap.setUsePrimitive(true);
        cMap.setPrimaryKey(false);
        cMap.setNotNull(false);
        cMap.setJavaName("Size");
        cMap.setAutoIncrement(false);
        cMap.setProtected(false);
        cMap.setDescription("size of this mailbox in byte");
        cMap.setDefault("0");
        cMap.setInheritance("false");
        cMap.setPosition(6);
        tMap.addColumn(cMap);
        tMap.setUseInheritance(false);
    }
}
