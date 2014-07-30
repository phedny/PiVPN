package net.phedny.android.pivpn;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mark on 05/07/14.
 */
public class InterfaceParser {

    public static Interface[] parse(BufferedReader reader) throws IOException {
        List<Interface> interfaces = new ArrayList<Interface>();
        Interface iface = new Interface();

        while (true) {
            String line = reader.readLine();
            if (line == null) {
                return interfaces.toArray(new Interface[interfaces.size()]);
            }

            if (line.length() == 0) {
                interfaces.add(iface);
                iface = new Interface();
            }

            matchInterfaceLine(iface, line);
            matchAddressLine(iface, line);
            matchStateLine(iface, line);
            matchRxLine(iface, line);
            matchTxLine(iface, line);
            matchStatsLine(iface, line);
            matchBytesLine(iface, line);
        }
    }

    private static final Pattern INTERFACE_LINE = Pattern.compile("([^ ]*) *Link encap:([-A-Za-z ]+)(?:  HWaddr ([0-9a-f:]+))? *");
    private static void matchInterfaceLine(Interface iface, String line) {
        Matcher matcher = INTERFACE_LINE.matcher(line);
        if (matcher.matches()) {
            iface.setName(matcher.group(1));
            iface.setEncapsulation(matcher.group(2));
            iface.setHardwareAddress(matcher.group(3));
        }
    }

    private static final Pattern ADDRESS_LINE = Pattern.compile(" *inet addr:([0-9.]+)(?:  P-t-P:([0-9.]+))?(?:  Bcast:([0-9.]+))?(?:  Mask:([0-9.]+))?");
    private static void matchAddressLine(Interface iface, String line) {
        Matcher matcher = ADDRESS_LINE.matcher(line);
        if (matcher.matches()) {
            iface.setInetAddress(matcher.group(1));
            iface.setInetPeerAddress(matcher.group(2));
            iface.setInetBroadcastAddress(matcher.group(3));
            iface.setInetNetmask(matcher.group(4));
        }
    }

    private static final Pattern STATE_LINE = Pattern.compile(" *(UP )?(LOOPBACK )?(POINTOPOINT )?(BROADCAST )?(RUNNING )?(MULTICAST )? MTU:(\\d+)  Metric:(\\d+)");
    private static void matchStateLine(Interface iface, String line) {
        Matcher matcher = STATE_LINE.matcher(line);
        if (matcher.matches()) {
            iface.setUp(matcher.group(1) != null);
            iface.setLoopback(matcher.group(2) != null);
            iface.setPointToPoint(matcher.group(3) != null);
            iface.setBroadcast(matcher.group(4) != null);
            iface.setRunning(matcher.group(5) != null);
            iface.setMulticast(matcher.group(6) != null);
            iface.setMtu(Integer.parseInt(matcher.group(7)));
            iface.setMetric(Integer.parseInt(matcher.group(8)));
        }
    }

    private static final Pattern RX_LINE = Pattern.compile(" *RX packets:(\\d+) errors:(\\d+) dropped:(\\d+) overruns:(\\d+) frame:(\\d+)");
    private static void matchRxLine(Interface iface, String line) {
        Matcher matcher = RX_LINE.matcher(line);
        if (matcher.matches()) {
            iface.setRxPackets(Long.parseLong(matcher.group(1)));
            iface.setRxErrors(Long.parseLong(matcher.group(2)));
            iface.setRxDropped(Long.parseLong(matcher.group(3)));
            iface.setRxOverruns(Long.parseLong(matcher.group(4)));
            iface.setRxFrame(Long.parseLong(matcher.group(5)));
        }
    }

    private static final Pattern TX_LINE = Pattern.compile(" *TX packets:(\\d+) errors:(\\d+) dropped:(\\d+) overruns:(\\d+) carrier:(\\d+)");
    private static void matchTxLine(Interface iface, String line) {
        Matcher matcher = TX_LINE.matcher(line);
        if (matcher.matches()) {
            iface.setTxPackets(Long.parseLong(matcher.group(1)));
            iface.setTxErrors(Long.parseLong(matcher.group(2)));
            iface.setTxDropped(Long.parseLong(matcher.group(3)));
            iface.setTxOverruns(Long.parseLong(matcher.group(4)));
            iface.setTxCarrier(Long.parseLong(matcher.group(5)));
        }
    }

    private static final Pattern STATS_LINE = Pattern.compile(" *collisions:(\\d+) txqueuelen:(\\d+)");
    private static void matchStatsLine(Interface iface, String line) {
        Matcher matcher = STATS_LINE.matcher(line);
        if (matcher.matches()) {
            iface.setCollisions(Long.parseLong(matcher.group(1)));
            iface.setTxQueueLen(Long.parseLong(matcher.group(2)));
        }
    }

    private static final Pattern BYTES_LINE = Pattern.compile(" *RX bytes:(\\d+) \\(\\d+\\.\\d (?:[KMG]i?)?B\\)  TX bytes:(\\d+) \\(\\d+\\.\\d (?:[KMG]i?)?B\\)");
    private static void matchBytesLine(Interface iface, String line) {
        Matcher matcher = BYTES_LINE.matcher(line);
        if (matcher.matches()) {
            iface.setRxBytes(Long.parseLong(matcher.group(1)));
            iface.setTxBytes(Long.parseLong(matcher.group(2)));
        }
    }

}
