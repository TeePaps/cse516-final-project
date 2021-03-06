package com.teepaps.fts.services;

import android.content.Intent;
import android.util.Log;

import com.teepaps.fts.routing.RoutingTable;
import com.teepaps.fts.ui.MainActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Service that waits for clients to connect. When they do, this host will send it's routing table
 * to the connected client.
 *
 * @author Created by ted on 4/18/14.
 */
public class RoutingTableTransferService extends AbstractTransferService {

    /**
     * Action to receive the table
     */
    public static final String ACTION_RECEIVE_TABLE    = "com.teepaps.fts.RECEIVE_TABLE";

    /**
     * Action to send the table
     */
    private static final String ACTION_SEND_TABLE       = "com.teepaps.fts.SEND_TABLE";

    /**
     * Key for routing table object serializable extra
     */
    public static final String EXTRA_ROUTING_TABLE     = "routing_table";

    /**
     * Notification to send back to the activity that registered
     */
    public static final String BROADCAST_NOTIFICATION  = "routing_table";

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

    public RoutingTableTransferService(String name) {
        super(name);
    }

    public RoutingTableTransferService() {
        super("RoutingTableTransferService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        host    = intent.getStringExtra(EXTRA_HOST);
        port    = intent.getIntExtra(EXTRA_PORT, 8888);
        routingTable = (RoutingTable) intent.getSerializableExtra(EXTRA_ROUTING_TABLE);

        // Send or receive a Routing Table
        String action = intent.getAction();
        if (action.equals(ACTION_RECEIVE_TABLE)) {
            doServerWork();
        } else if (action.equals(ACTION_SEND_TABLE)) {
            if (routingTable != null) {
                doClientWork();
            }
        }

    }    /**

     * Read the RoutingTable object from the stream
     */
    protected void doServerWork() {
        try {
            // Create a server socket and wait for client connections. This call blocks until a
            // connection is accepted from a client
            ServerSocket serverSocket = new ServerSocket(port);
            Socket client = serverSocket.accept();


             // If this code is reached, a client has connected and transferred data.
             // Get the RoutingTable object from the stream.
            ObjectInputStream inputStream = new ObjectInputStream(client.getInputStream());
            RoutingTable routingTable = (RoutingTable) inputStream.readObject();
            if (routingTable != null) {
                routingTable.merge(routingTable);
            }
            publishResults();

            serverSocket.close();
        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.getMessage());
        } catch (ClassNotFoundException e) {
                Log.w(MainActivity.TAG, "Something weird happened. Couldn't parse the input stream");
        }
    }

    /**
     * Write the RoutingTable object to the stream
     */
    protected void doClientWork() {

        Socket socket = new Socket();
        byte buf[]  = new byte[1024];

        try {
             //Create a client socket with the host, port, and timeout information.
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), 500);

            // Simply write the routing table object to the output stream
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(routingTable);

            outputStream.close();
        } catch (FileNotFoundException e) {
            //catch logic
        } catch (IOException e) {
            //catch logic
        }
        // Clean up any open sockets when done transferring or if an exception occurred.
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
    }

    /**
     * Send a broadcast to the activity that registered this receiver
     */
    protected void publishResults() {
        Intent intent = new Intent(BROADCAST_NOTIFICATION);
        intent.putExtra(EXTRA_ROUTING_TABLE, routingTable);
        sendBroadcast(intent);
    }
}
