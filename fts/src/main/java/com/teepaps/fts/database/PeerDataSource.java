package com.teepaps.fts.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.common.io.BaseEncoding;
import com.teepaps.fts.database.models.DataModel;
import com.teepaps.fts.database.models.Peer;
import com.teepaps.fts.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ted on 3/25/14.
 */
public class PeerDataSource extends AbstractDataSource {

    //******** STATIC DATA MEMBERS ********
    //*************************************

    /**
     * Name of table
     */
    private static final String TABLE_NAME          = "peers";

    /**
     * User visible name of the Peer
     */
    private static final String KEY_PEER_NAME       = "peer_name";

    /**
     * Unique ID of Peer
     */
    private static final String KEY_PEER_ID         = "peer_id";

    /**
     * Shared key for encryption
     */
    private static final String KEY_SHARED_KEY      = "shared_key";

    /**
     * What is the cost (how many hops) to get to the peer.
     */
    private static final String KEY_COST            = "cost";

    /**
     * Is a connection to this peer open
     */
    private static final String KEY_IS_CONNECTED    = "is_connected";

    /**
     * Has this peer sent or received any messages previously?
     */
    private static final String KEY_HAS_CHATTED     = "has_chatted";

    /**
     * SQL statement to create table
     */
    private static final String COLUMN_DEFS    =
            DatabaseHelper.KEY_ROW_ID + " INTEGER PRIMARY KEY, "
            + KEY_PEER_NAME + " TEXT, "
            + KEY_PEER_ID + " TEXT, "
            + KEY_SHARED_KEY + " TEXT, "
            + KEY_COST + " INTEGER, "
            + KEY_IS_CONNECTED + " BOOLEAN NOT NULL CHECK (" + KEY_IS_CONNECTED + " IN (0,1)), "
            + KEY_HAS_CHATTED + " BOOLEAN NOT NULL CHECK (" + KEY_HAS_CHATTED + " IN (0,1))";

    //******** NON-STATIC METHODS ********
    //************************************

    /**
     * Constructor used to access database.
     * @param context
     */
    public PeerDataSource(Context context) {
        super(context);
    }

    /**
     * Static constructor
     * @param context
     */
    public static PeerDataSource newInstance(Context context) {
        return new PeerDataSource(context);
    }

    /**
     * Create a new Peer entry in the database and return the new Peer created
     *
     * @param peerId
     * @param sharedKey
     * @return
     */
    public Peer createPeer(String peerName, String peerId, byte[] sharedKey) {
        ContentValues values = getContentValues(peerName, peerId, sharedKey, 1, 0, 0);

        return (Peer) create(values);
    }

    private ContentValues getContentValues(String peerName, String peerId, byte[] sharedKey,
                                           int cost, int isConnected, int hasChatted)
    {
        ContentValues values = new ContentValues();
        if (peerName != null) values.put(KEY_PEER_NAME, peerName);
        if (peerId != null) values.put(KEY_PEER_ID, peerId);
        if (sharedKey != null) values.put(KEY_SHARED_KEY, BaseEncoding.base64().encode(sharedKey));
        if (cost > -1) values.put(KEY_COST, cost);
        if (isConnected > -1) values.put(KEY_IS_CONNECTED, isConnected);
        if (hasChatted > -1) values.put(KEY_HAS_CHATTED, hasChatted);

        return values;
    }

    /**
     * Retrieve a list of all the Peers in the database
     *
     * @return
     */
    public List<Peer> getAllPeers() {
        open();
        List<Peer> peers = new ArrayList<Peer>();

        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

        cursor.moveToFirst();
        while ((cursor != null) && !cursor.isAfterLast()) {
            Peer peer = cursorToPeer(cursor);
            peers.add(peer);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return peers;
    }

    public Peer getPeer(String peerId) {
        Peer peer = null;

        open();
        Log.d(MainActivity.TAG, KEY_PEER_ID + " = " + peerId);
        Cursor cursor = database.query(TABLE_NAME, null, KEY_PEER_ID + " = ?",
                new String[] { peerId }, null, null, null);
        if (cursor.moveToFirst()) {
            peer = cursorToPeer(cursor);
            cursor.close();
        }

        return peer;
    }

    /**
     * Wrapper to extract a Peer from a cursor
     *
     * @param cursor
     */
    public Peer cursorToPeer(Cursor cursor) {
        return (Peer) cursorToModel(cursor);
    }

    /**
     * Wrapper to extract a Peer from a cursor
     *
     * @param cursor
     */
    @Override
    protected DataModel cursorToModel(Cursor cursor) {
        Peer peer = new Peer();
        peer.setRowId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.KEY_ROW_ID)));
        peer.setPeerName(cursor.getString(cursor.getColumnIndex(KEY_PEER_NAME)));
        peer.setPeerId(cursor.getString(cursor.getColumnIndex(KEY_PEER_ID)));
        peer.setSharedKey(cursor.getString(cursor.getColumnIndex(KEY_SHARED_KEY)));
        peer.setCost(cursor.getInt(cursor.getColumnIndex(KEY_COST)));
        peer.setConnected(cursor.getInt(cursor.getColumnIndex(KEY_IS_CONNECTED)));
        peer.setChatted(cursor.getInt(cursor.getColumnIndex(KEY_HAS_CHATTED)));

        return peer;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String getColumnDefs() {
        return COLUMN_DEFS;
    }

    /**
     * Returns a cursor for all peers than have previously sent or recieved a message.
     * @return
     */
    public Cursor getConnectedPeers() {
        open();
        Cursor cursor = database.query(TABLE_NAME, null, KEY_COST + " > 0",
                null, null, null, null);
        return cursor;
    }

    public void updatePeer(Peer peer) {
        open();
        ContentValues cvUpdate = getContentValues(peer.getPeerName(), peer.getPeerId(),
                peer.getSharedKeyBytes(), peer.getCost(), 0, 0);
        database.update(TABLE_NAME, cvUpdate, KEY_PEER_ID + "=?", new String[] {peer.getPeerId()});
        close();
    }

    /**
     * Returns a cursor for all peers than have previously sent or recieved a message.
     * @return
     */
    public Cursor getChattedPeers() {
        open();
        Cursor cursor = database.query(TABLE_NAME, null, KEY_HAS_CHATTED + " = 1",
                null, null, null, null);
        return cursor;
    }

     /**
     * Returns a cursor for all peers than have previously sent or recieved a message.
     * @return
     */
    public Cursor getVisiblePeers() {
        open();
        Cursor cursor = database.query(TABLE_NAME, null, DatabaseHelper.KEY_ROW_ID + " > 0",
                null, null, null, null);
        return cursor;

    }
}

