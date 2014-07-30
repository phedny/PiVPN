package net.phedny.android.pivpn;

/**
 * Created by mark on 29/06/14.
 */
public final class Constants {

    public static final String ACTION_CONN_STATUS =
            "net.phedny.android.pivpn.ACTION_CONN_STATUS";

    public static final String ACTION_RECONNECT =
            "net.phedny.android.pivpn.ACTION_RECONNECT";

    public static final String ACTION_DISCONNECT =
            "net.phedny.android.pivpn.ACTION_DISCONNECT";

    public static final String ACTION_VPN_PON =
            "net.phedny.android.pivpn.ACTION_VPN_PON";

    public static final String ACTION_VPN_POFF =
            "net.phedny.android.pivpn.ACTION_VPN_POFF";

    public static final String EXTRA_VPN_NAME =
            "net.phedny.android.pivpn.EXTRA_VPN_NAME";

    public static final String BROADCAST_VPNS =
            "net.phedny.android.pivpn.BROADCAST_VPNS";

    public static final String EXTRA_VPNS =
            "net.phedny.android.pivpn.EXTRA_VPNS";

    public static final String BROADCAST_REQUIRE_CONFIG =
            "net.phedny.android.pivpn.BROADCAST_REQUIRE_CONFIG";

    public static final String BROADCAST_UNKNOWN_HOST_KEY =
            "net.phedny.android.pivpn.BROADCAST_UNKNOWN_HOST_KEY";

    public static final String EXTRA_HOST =
            "net.phedny.android.pivpn.EXTRA_HOST";

    public static final String EXTRA_FINGERPRINT =
            "net.phedny.android.pivpn.EXTRA_FINGERPRINT";

    public static final String EXTRA_REMEMBER =
            "net.phedny.android.pivpn.EXTRA_REMEMBER";

    public static final String BROADCAST_INTERFACES =
            "net.phedny.android.pivpn.BROADCAST_INTERFACES";

    public static final String EXTRA_INTERFACES =
            "net.phedny.android.pivpn.EXTRA_INTERFACES";

    public static final String BROADCAST_ERROR =
            "net.phedny.android.pivpn.BROADCAST_ERROR";

    public static final String EXTRA_ERROR =
            "net.phedny.android.pivpn.EXTRA_ERROR";

    public static final String BROADCAST_CONN_STATUS =
            "net.phedny.android.pivpn.BROADCAST_CONN_STATUS";

    public static final String EXTRA_CONN_STATUS =
            "net.phedny.android.pivpn.CONN_STATUS";

    public static final String CONN_STATUS_DISCONNECTED =
            "DISCONNECTED";

    public static final String CONN_STATUS_CONNECTING =
            "CONNECTING";

    public static final String CONN_STATUS_CONNECTED =
            "CONNECTED";
}
