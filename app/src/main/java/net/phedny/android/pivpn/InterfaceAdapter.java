package net.phedny.android.pivpn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by mark on 13/07/14.
 */
public class InterfaceAdapter extends ArrayAdapter<Interface> {
    public InterfaceAdapter(Context context) {
        super(context, R.layout.interface_list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Interface iface = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.interface_list, parent, false);
        }

        TextView stateView = (TextView) convertView.findViewById(R.id.interface_state);
        stateView.setText(iface.isRunning() ? "1" : "0");

        TextView nameView = (TextView) convertView.findViewById(R.id.interface_name);
        nameView.setText(iface.getName());

        TextView statsView = (TextView) convertView.findViewById(R.id.interface_stats);
        statsView.setText("RX:" + iface.getRxBytes() + " / TX:" + iface.getTxBytes());

        TextView ipView = (TextView) convertView.findViewById(R.id.interface_ip);
        if (iface.getInetAddress() == null) {
            ipView.setText("-");
        } else {
            ipView.setText(iface.getInetAddress());
        }

        TextView peerIpView = (TextView) convertView.findViewById(R.id.interface_peer_ip);
        if (iface.getInetPeerAddress() == null) {
            peerIpView.setText("-");
        } else {
            peerIpView.setText(iface.getInetPeerAddress());
        }

        return convertView;
    }
}
