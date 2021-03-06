package com.teepaps.fts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teepaps.fts.R;
import com.teepaps.fts.database.models.Peer;

/**
 * Shows all peers that are in range to communicate with.
 * Created by ted on 4/13/14.
 */
public class PeerListAdapter extends ArrayAdapter<Peer> {

    private static final String TAG = PeerListAdapter.class.getSimpleName();

    /**
     * Inflater for the view
     */
    LayoutInflater inflater;


    public PeerListAdapter(Context context) {
        // Start with no content for loader
        super(context, 0);

        this.inflater   = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.peer_list_item, null);
        }

        TextView tvPeerName = (TextView) convertView.findViewById(R.id.tv_peer_name);
        TextView tvPeerId = (TextView) convertView.findViewById(R.id.tv_peer_id);
        TextView tvPeerCost = (TextView) convertView.findViewById(R.id.tv_peer_cost);

        Peer peer = getItem(position);
        tvPeerName.setText(peer.getPeerName());
        tvPeerId.setText(peer.getPeerId());
        tvPeerCost.setText(String.valueOf(peer.getCost()));

        convertView.setTag(peer.getPeerId());
        return convertView;
    }
}
