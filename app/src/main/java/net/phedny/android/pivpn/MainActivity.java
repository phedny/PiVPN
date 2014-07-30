package net.phedny.android.pivpn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity {

    private VpnAdapter vpnAdapter;

    private InterfaceAdapter interfaceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CommBroadcastReceiver receiver = new CommBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BROADCAST_CONN_STATUS);
        filter.addAction(Constants.BROADCAST_VPNS);
        filter.addAction(Constants.BROADCAST_UNKNOWN_HOST_KEY);
        filter.addAction(Constants.BROADCAST_INTERFACES);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        startService(new Intent(this, CommService.class).setAction(Constants.ACTION_CONN_STATUS));

        ListView vpnListView = (ListView) findViewById(R.id.vpn_list);
        vpnAdapter = new VpnAdapter(this);
        vpnListView.setAdapter(vpnAdapter);

        ListView interfaceListView = (ListView) findViewById(R.id.interface_list);
        interfaceAdapter = new InterfaceAdapter(this);
        interfaceListView.setAdapter(interfaceAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void doConnect(View view) {
        startService(new Intent(this, CommService.class).setAction(Constants.ACTION_RECONNECT));
    }

    public void doDisconnect(View view) {
        startService(new Intent(this, CommService.class).setAction(Constants.ACTION_DISCONNECT));
    }

    public void switchVpn(View view) {
        Switch switchView = (Switch) view;
        ViewGroup parent = (ViewGroup) view.getParent();
        TextView nameView = (TextView) parent.findViewById(R.id.vpn_name);
        String vpnName = nameView.getText().toString();
        startService(new Intent(this, CommService.class)
                .setAction(switchView.isChecked() ? Constants.ACTION_VPN_PON : Constants.ACTION_VPN_POFF)
                .putExtra(Constants.EXTRA_VPN_NAME, vpnName));
    }

    public class CommBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.BROADCAST_CONN_STATUS.equals(intent.getAction())) {
                String status = intent.getStringExtra(Constants.EXTRA_CONN_STATUS);
                TextView textView = (TextView) MainActivity.this.findViewById(R.id.connection_status);
                textView.setText(status);

                Button connectButton = (Button) MainActivity.this.findViewById(R.id.button_connect);
                connectButton.setVisibility("DISCONNECTED".equals(status) ? View.VISIBLE : View.GONE);

                Button disconnectButton = (Button) MainActivity.this.findViewById(R.id.button_disconnect);
                disconnectButton.setVisibility("CONNECTED".equals(status) ? View.VISIBLE : View.GONE);

                if ("DISCONNECTED".equals(status)) {
                    vpnAdapter.clear();
                    vpnAdapter.notifyDataSetChanged();
                    interfaceAdapter.clear();
                    interfaceAdapter.notifyDataSetChanged();
                }

            } else if (Constants.BROADCAST_UNKNOWN_HOST_KEY.equals(intent.getAction())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                StringBuilder message = new StringBuilder(getResources().getString(R.string.fingerprint_dialog_message));
                final String host = intent.getStringExtra(Constants.EXTRA_HOST);
                final String fingerprint = intent.getStringExtra(Constants.EXTRA_FINGERPRINT);
                message.append("\nHost: ").append(host);
                message.append("\nFingerprint: ").append(fingerprint);
                builder
                        .setTitle(R.string.fingerprint_dialog_title)
                        .setMessage(message)
                        .setPositiveButton(R.string.fingerprint_dialog_remember, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                startService(new Intent(MainActivity.this, CommService.class)
                                        .setAction(Constants.ACTION_RECONNECT)
                                        .putExtra(Constants.EXTRA_HOST, host)
                                        .putExtra(Constants.EXTRA_FINGERPRINT, fingerprint)
                                        .putExtra(Constants.EXTRA_REMEMBER, true));
                            }
                        })
                        .setNeutralButton(R.string.fingerprint_dialog_once, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                startService(new Intent(MainActivity.this, CommService.class)
                                        .setAction(Constants.ACTION_RECONNECT)
                                        .putExtra(Constants.EXTRA_HOST, host)
                                        .putExtra(Constants.EXTRA_FINGERPRINT, fingerprint));
                            }
                        })
                        .setNegativeButton(R.string.fingerprint_dialog_cancel, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (Constants.BROADCAST_VPNS.equals(intent.getAction())) {
                vpnAdapter.clear();
                for (Parcelable p : intent.getParcelableArrayExtra(Constants.EXTRA_VPNS)) {
                    vpnAdapter.add((Vpn) p);
                }
                vpnAdapter.notifyDataSetChanged();
            } else if (Constants.BROADCAST_INTERFACES.equals(intent.getAction())) {
                interfaceAdapter.clear();
                for (Parcelable p : intent.getParcelableArrayExtra(Constants.EXTRA_INTERFACES)) {
                    interfaceAdapter.add((Interface) p);
                }
                interfaceAdapter.notifyDataSetChanged();
            }
        }
    }
}
