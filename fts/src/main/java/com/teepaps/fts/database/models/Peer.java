package com.teepaps.fts.database.models;

import android.content.Context;

import com.google.common.io.BaseEncoding;
import com.teepaps.fts.database.MessageDataSource;
import com.teepaps.fts.utils.ConversionUtils;

/**
 * Handles message passing to the user and from the user. Abstracts away the
 * lower layers to use the public peer methods as an API for passing the
 * messages.
 * <p>
 * Created by ted on 3/25/14.
 * </p>
 */
public class Peer extends DataModel {

    //******** STATIC DATA MEMBERS ********
    //*************************************

    public static final int CONNECTION_WIFI         = 0x1;
    public static final int CONNECTION_BLUETOOTH    = 0x2;

    //******** NON-STATIC DATA MEMBERS ********
    //*****************************************

    /**
     * User visible name of peer˜

     */
    private String peerName;

    /**
     * Unique ID of the Peer so that the application knows to communicate with
     * the proper Peer.
     */
    private String peerId;

    /**
     * Shared key between the user and peer to encrypt/decrypt messages.
     */
    private String sharedKey;

   /**
     * What is the cost (how many hops) to get to the peer.
     */
    private int cost;

    /**
     * Is a connection to this 'Peer' open?
     */
    private boolean isConnected;

    /**
     * Has this 'Peer' sent or received messages previously?
     */
    private boolean hasChatted;

    /**
     * Send a message to this peer.
     * @param message
     */
    public void send(Context context, String message) {
        MessageDataSource dataSource = new MessageDataSource(context);
        dataSource.createMessage("user", peerId, "");

    }

    /**
     * Receive a message, if present, from this peer.
     * @return
     */
    public String receive() {
        String message = null;
        return message;
    }

    //******** GETTERS ********
    //*************************

    public String getPeerName() {
        return peerName;
    }

    public String getPeerId() {
//        if (peerId != null) {
//            return peerId.replace("_", ":");
//        }
//        return null;
        return peerId;
    }

    public int getCost() {
        return cost;
    }

     /**
     * Decodes the base64 representation of the key
     * @return
     */
    public byte[] getSharedKeyBytes() {
        if (getSharedKeyEncoded() != null) {
            return BaseEncoding.base64().decode(getSharedKeyEncoded());
        }
        return null;
    }

    public String getSharedKeyEncoded() {
        return sharedKey;
    }

    //******** SETTERS ********
    //*************************

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public void setPeerId(String peerId) {
//        if (peerId != null) {
//            this.peerId = peerId.replace(":", "_");
//        }
        this.peerId = peerId;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    public void setSharedKey(byte[] key) {
        this.sharedKey = BaseEncoding.base64().encode(key);
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public void setChatted(boolean hasChatted) {
        this.hasChatted = hasChatted;
    }

    /**
     * Allows to write to this model using SQLite's INTEGER value.
     * @param isConnected
     */
    public void setConnected(int isConnected) {
        setConnected(ConversionUtils.intToBoolean(isConnected));
    }

    /**
     * Allows to write to this model using SQLite's INTEGER value.
     * @param isConnected
     */
    public void setChatted(int isConnected) {
        setChatted(ConversionUtils.intToBoolean(isConnected));
    }
}
