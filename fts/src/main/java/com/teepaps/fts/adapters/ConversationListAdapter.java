package com.teepaps.fts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.teepaps.fts.R;
import com.teepaps.fts.database.PeerDataSource;
import com.teepaps.fts.database.models.Peer;

/**
 * Shows all previous conversations with different peers
 * Created by ted on 4/13/14.
 */
public class ConversationListAdapter extends CursorAdapter {

    private static final String TAG = ConversationListAdapter.class.getSimpleName();

    /**
     * Inflater for the view
     */
    LayoutInflater inflater;

    public ConversationListAdapter(Context context) {
        super(context, null, 0);

        this.inflater   = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.conversation_item, parent, false);

        // This line probably doesn't need to be here.
        bindView(view, context, cursor);
        //^^^^^^^^^^^^^^^^^^^^^^^^^

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Get the layout elements
        TextView tvSender = (TextView) view.findViewById(R.id.tvSender);

        // Extract the Peer model from the cursor
        Peer peer = PeerDataSource.newInstance(context).cursorToPeer(cursor);

        // Set the TextViews using the FTSMessage object
        String peerName = peer.getPeerName();
        tvSender.setText(peerName);
        view.setTag(peer.getPeerId());
    }
}
