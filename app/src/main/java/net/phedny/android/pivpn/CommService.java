package net.phedny.android.pivpn;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mark on 29/06/14.
 */
public class CommService extends Service {

    private static final Pattern PPPD_PATTERN = Pattern.compile("/usr/sbin/pppd call (.*)");

    private static final Pattern VPN_PATTERN = Pattern.compile("[^.].*[^~]");

    private ScheduledExecutorService executor;

    private volatile Session session;

    private List<String> peers = new ArrayList<String>();

    private List<String> connectedPeers = new ArrayList<String>();

    private boolean initialized;

    private void broadcast(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastStatus() {
        String status;
        if (session == null) {
            status = Constants.CONN_STATUS_DISCONNECTED;
        } else if (session.isConnected()) {
            status = Constants.CONN_STATUS_CONNECTED;
        } else {
            status = Constants.CONN_STATUS_CONNECTING;
        }
        broadcast(new Intent(Constants.BROADCAST_CONN_STATUS)
                .putExtra(Constants.EXTRA_CONN_STATUS, status));
    }

    private void broadcastVpns() {
        Vpn vpns[] = new Vpn[peers.size()];
        for (int i = 0; i < vpns.length; i++) {
            String vpnName = peers.get(i);
            vpns[i] = new Vpn();
            vpns[i].setName(vpnName);
            if (connectedPeers.contains(vpnName)) {
                vpns[i].setConnected(true);
            }
        }
        broadcast(new Intent(Constants.BROADCAST_VPNS)
                .putExtra(Constants.EXTRA_VPNS, vpns));
    }

    private void initialize() {
        initialize(null, null, false);
    }

    private void initialize(String host, final String fingerprint, final boolean remember) {
        initialized = true;

        if (executor == null) {
            executor = Executors.newSingleThreadScheduledExecutor();
        }

        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (session != null) {
                    session.disconnect();
                    session = null;
                }

                final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CommService.this);
                String hostname = preferences.getString("pi_hostname", null);
                String username = preferences.getString("pi_username", null);
                String password = preferences.getString("pi_password", null);
                final String hostKey = preferences.getString("pi_hostkey", null);

                if (hostname == null || username == null || password == null) {
                    broadcast(new Intent(Constants.BROADCAST_REQUIRE_CONFIG));
                }

                try {
                    JSch jsch = new JSch();
                    jsch.setHostKeyRepository(new HostKeyRepository() {
                        private String hex(byte[] input) {
                            final String chars = "0123456789abcdef";
                            StringBuilder hexString = new StringBuilder();
                            for (int i = 0; i < input.length; i++) {
                                int value = 0xff & input[i];
                                hexString.append(chars.charAt(value >>> 4));
                                hexString.append(chars.charAt(0xf & value));
                                hexString.append(':');
                            }
                            hexString.setLength(hexString.length() - 1);
                            return hexString.toString();
                        }

                        private byte[] md5(byte[] input) {
                            try {
                                MessageDigest md = MessageDigest.getInstance("MD5");
                                return md.digest(input);
                            } catch (GeneralSecurityException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        private void askFingerprintCheck(String host, byte[] key) {
                            broadcast(new Intent(Constants.BROADCAST_UNKNOWN_HOST_KEY)
                                    .putExtra(Constants.EXTRA_HOST, host)
                                    .putExtra(Constants.EXTRA_FINGERPRINT, hex(md5(key))));
                        }

                        @Override
                        public int check(String host, byte[] key) {
                            if (fingerprint != null) {
                                if (fingerprint.equals(hex(md5(key)))) {
                                    if (remember) {
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("pi_hostkey", hex(key));
                                        editor.commit();
                                    }
                                    return OK;
                                } else {
                                    askFingerprintCheck(host, key);
                                    return NOT_INCLUDED;
                                }
                            } else if (hostKey == null) {
                                askFingerprintCheck(host, key);
                                return NOT_INCLUDED;
                            } else if (hostKey.equals(hex(key))) {
                                return OK;
                            } else {
                                askFingerprintCheck(host, key);
                                return CHANGED;
                            }
                        }

                        @Override
                        public void add(HostKey hostkey, UserInfo ui) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public void remove(String host, String type) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public void remove(String host, String type, byte[] key) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public String getKnownHostsRepositoryID() {
                            return null;
                        }

                        @Override
                        public HostKey[] getHostKey() {
                            return new HostKey[0];
                        }

                        @Override
                        public HostKey[] getHostKey(String host, String type) {
                            return new HostKey[0];
                        }
                    });
                    session = jsch.getSession(username, hostname);
                    broadcastStatus();
                    session.setPassword(password);
                    session.connect(5000);
                    broadcastStatus();

                    ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
                    sftp.connect();
                    Vector<ChannelSftp.LsEntry> peerList = sftp.ls("/etc/ppp/peers");
                    peers.clear();
                    for (ChannelSftp.LsEntry peer : peerList) {
                        if (!peer.getAttrs().isDir() && VPN_PATTERN.matcher(peer.getFilename()).matches()) {
                            peers.add(peer.getFilename());
                        }
                    }
                    sftp.disconnect();
                    broadcastVpns();

                    beat();
                } catch (SftpException e) {
                    Log.e("Comm", "Failed to list peers", e);
                    broadcast(new Intent(Constants.BROADCAST_ERROR)
                            .putExtra(Constants.EXTRA_ERROR, e.getMessage()));
                    session.disconnect();
                    session = null;
                    broadcastStatus();
                    stopSelf();
                } catch (JSchException e) {
                    Log.e("Comm", "Failed to connect", e);
                    broadcast(new Intent(Constants.BROADCAST_ERROR)
                            .putExtra(Constants.EXTRA_ERROR, e.getMessage()));
                    session.disconnect();
                    session = null;
                    broadcastStatus();
                    stopSelf();
                }
            }
        });
    }

    private void beat() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!session.isConnected()) {
                    executor.shutdown();
                    executor = null;
                    session = null;
                    broadcastStatus();
                    initialize();
                    return;
                }

                ChannelExec channel = null;
                try {
                    channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand("/sbin/ifconfig");
                    InputStream stream = channel.getInputStream();
                    channel.connect();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    Interface[] interfaces = InterfaceParser.parse(reader);

                    stream.close();
                    channel.disconnect();
                    channel = null;

                    broadcast(new Intent(Constants.BROADCAST_INTERFACES)
                            .putExtra(Constants.EXTRA_INTERFACES, interfaces));

                    channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand("/bin/ps h -C pppd -o cmd");
                    stream = channel.getInputStream();
                    channel.connect();

                    reader = new BufferedReader(new InputStreamReader(stream));
                    List<String> connectedPeers = new ArrayList<String>();
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }

                        Matcher matcher = PPPD_PATTERN.matcher(line);
                        if (matcher.matches()) {
                            connectedPeers.add(matcher.group(1));
                        }
                    }

                    if (!CommService.this.connectedPeers.equals(connectedPeers)) {
                        CommService.this.connectedPeers = connectedPeers;
                        broadcastVpns();
                    }
                } catch (JSchException e) {
                    if (channel != null) {
                        channel.disconnect();
                    }
                    Log.e("Comm", "Failed to open channel", e);
                    broadcast(new Intent(Constants.BROADCAST_ERROR)
                            .putExtra(Constants.EXTRA_ERROR, e.getMessage()));
                } catch (IOException e) {
                    if (channel != null) {
                        channel.disconnect();
                    }
                    Log.e("Comm", "Failed to communicate over channel", e);
                    broadcast(new Intent(Constants.BROADCAST_ERROR)
                            .putExtra(Constants.EXTRA_ERROR, e.getMessage()));
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void setVpnStatus(final String vpn, final boolean enabled) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                ChannelExec channel = null;
                try {
                    channel = (ChannelExec) session.openChannel("exec");
                    channel.setCommand("/usr/bin/sudo /usr/bin/" + (enabled ? "pon" : "poff") + " " + vpn);
                    channel.connect();
                    channel.disconnect();
                    channel = null;
                } catch (JSchException e) {
                    if (channel != null) {
                        channel.disconnect();
                    }
                    Log.e("Comm", "Failed to open channel", e);
                    broadcast(new Intent(Constants.BROADCAST_ERROR)
                            .putExtra(Constants.EXTRA_ERROR, e.getMessage()));
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Comm", "> " + intent.getAction());
        if (Constants.ACTION_RECONNECT.equals(intent.getAction())) {
            if (intent.hasExtra(Constants.EXTRA_HOST)) {
                initialize(intent.getStringExtra(Constants.EXTRA_HOST),
                        intent.getStringExtra(Constants.EXTRA_FINGERPRINT),
                        intent.hasExtra(Constants.EXTRA_REMEMBER));
            } else {
                initialize();
            }
        }

        if (!initialized && !Constants.ACTION_DISCONNECT.equals(intent.getAction())) {
            initialize();
        }

        if (Constants.ACTION_CONN_STATUS.equals(intent.getAction())) {
            broadcastStatus();
            broadcastVpns();
        } else if (Constants.ACTION_DISCONNECT.equals(intent.getAction())) {
            session.disconnect();
            session = null;
            broadcastStatus();
            stopSelf();
        } else if (Constants.ACTION_VPN_PON.equals(intent.getAction())) {
            setVpnStatus(intent.getStringExtra(Constants.EXTRA_VPN_NAME), true);
        } else if (Constants.ACTION_VPN_POFF.equals(intent.getAction())) {
            setVpnStatus(intent.getStringExtra(Constants.EXTRA_VPN_NAME), false);
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
