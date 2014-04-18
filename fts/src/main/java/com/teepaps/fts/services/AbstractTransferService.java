package com.teepaps.fts.services;

import android.app.IntentService;

import com.teepaps.fts.routing.RoutingTable;

/**
 * Service that waits for clients to connect. When they do, this host will send it's routing table
 * to the connected client.
 *
 * @author Created by ted on 4/18/14.
 */
public abstract class AbstractTransferService extends IntentService {

    /**
     * Key for host to connect to with the socket
     */
    private static final String EXTRA_HOST              = "host";

    /**
     * Key for port to connect the socket on
     */
    private static final String EXTRA_PORT              = "port";

    /**
     * Notification to send back to the activity that registered
     */
    private static final String BROADCAST_NOTIFICATION  = "default_notification";

    /**
     * Type of transfer service
     */
    private int type;

    /**
     * Host to transfer the routing table to
     */
    private String host;

    /**
     * Routing table to return as a result
     */
    private RoutingTable routingTable;

    public AbstractTransferService(String name) {
        super(name);
    }

    public AbstractTransferService() {
        super("AbstractTransferService");
    }

    /**
     * Read the RoutingTable object from the stream
     */
    protected abstract void doServerWork();
    /**
     * Write the RoutingTable object to the stream
     */
    protected abstract void doClientWork();

    /**
     * Send a broadcast to the activity that registered this receiver
     */
    protected abstract void publishResults();

}
