package net.phedny.android.pivpn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by mark on 13/07/14.
 */
public class VpnAdapter extends ArrayAdapter<Vpn> {
    public VpnAdapter(Context context) {
        super(context, R.layout.vpn_list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Vpn vpn = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.vpn_list, parent, false);
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.vpn_name);
        nameView.setText(vpn.getName());

        Switch switchView = (Switch) convertView.findViewById(R.id.vpn_switch);
        switchView.setChecked(vpn.isConnected());

        return convertView;
    }
}
