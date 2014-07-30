package net.phedny.android.pivpn;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mark on 05/07/14.
 */
public final class Interface implements Parcelable {

    private String name;
    private String encapsulation;
    private String hardwareAddress;
    private String inetAddress;
    private String inetPeerAddress;
    private String inetBroadcastAddress;
    private String inetNetmask;
    private boolean up;
    private boolean loopback;
    private boolean pointToPoint;
    private boolean broadcast;
    private boolean running;
    private boolean multicast;
    private int mtu;
    private int metric;
    private long rxPackets;
    private long rxErrors;
    private long rxDropped;
    private long rxOverruns;
    private long rxFrame;
    private long txPackets;
    private long txErrors;
    private long txDropped;
    private long txOverruns;
    private long txCarrier;
    private long collisions;
    private long txQueueLen;
    private long rxBytes;
    private long txBytes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncapsulation() {
        return encapsulation;
    }

    public void setEncapsulation(String encapsulation) {
        this.encapsulation = encapsulation;
    }

    public String getHardwareAddress() {
        return hardwareAddress;
    }

    public void setHardwareAddress(String hardwareAddress) {
        this.hardwareAddress = hardwareAddress;
    }

    public String getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(String inetAddress) {
        this.inetAddress = inetAddress;
    }

    public String getInetPeerAddress() {
        return inetPeerAddress;
    }

    public void setInetPeerAddress(String inetPeerAddress) {
        this.inetPeerAddress = inetPeerAddress;
    }

    public String getInetBroadcastAddress() {
        return inetBroadcastAddress;
    }

    public void setInetBroadcastAddress(String inetBroadcastAddress) {
        this.inetBroadcastAddress = inetBroadcastAddress;
    }

    public String getInetNetmask() {
        return inetNetmask;
    }

    public void setInetNetmask(String inetNetmask) {
        this.inetNetmask = inetNetmask;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isLoopback() {
        return loopback;
    }

    public void setLoopback(boolean loopback) {
        this.loopback = loopback;
    }

    public boolean isPointToPoint() {
        return pointToPoint;
    }

    public void setPointToPoint(boolean pointToPoint) {
        this.pointToPoint = pointToPoint;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isMulticast() {
        return multicast;
    }

    public void setMulticast(boolean multicast) {
        this.multicast = multicast;
    }

    public int getMtu() {
        return mtu;
    }

    public void setMtu(int mtu) {
        this.mtu = mtu;
    }

    public int getMetric() {
        return metric;
    }

    public void setMetric(int metric) {
        this.metric = metric;
    }

    public long getRxPackets() {
        return rxPackets;
    }

    public void setRxPackets(long rxPackets) {
        this.rxPackets = rxPackets;
    }

    public long getRxErrors() {
        return rxErrors;
    }

    public void setRxErrors(long rxErrors) {
        this.rxErrors = rxErrors;
    }

    public long getRxDropped() {
        return rxDropped;
    }

    public void setRxDropped(long rxDropped) {
        this.rxDropped = rxDropped;
    }

    public long getRxOverruns() {
        return rxOverruns;
    }

    public void setRxOverruns(long rxOverruns) {
        this.rxOverruns = rxOverruns;
    }

    public long getRxFrame() {
        return rxFrame;
    }

    public void setRxFrame(long rxFrame) {
        this.rxFrame = rxFrame;
    }

    public long getTxPackets() {
        return txPackets;
    }

    public void setTxPackets(long txPackets) {
        this.txPackets = txPackets;
    }

    public long getTxErrors() {
        return txErrors;
    }

    public void setTxErrors(long txErrors) {
        this.txErrors = txErrors;
    }

    public long getTxDropped() {
        return txDropped;
    }

    public void setTxDropped(long txDropped) {
        this.txDropped = txDropped;
    }

    public long getTxOverruns() {
        return txOverruns;
    }

    public void setTxOverruns(long txOverruns) {
        this.txOverruns = txOverruns;
    }

    public long getTxCarrier() {
        return txCarrier;
    }

    public void setTxCarrier(long txCarrier) {
        this.txCarrier = txCarrier;
    }

    public long getCollisions() {
        return collisions;
    }

    public void setCollisions(long collisions) {
        this.collisions = collisions;
    }

    public long getTxQueueLen() {
        return txQueueLen;
    }

    public void setTxQueueLen(long txQueueLen) {
        this.txQueueLen = txQueueLen;
    }

    public long getRxBytes() {
        return rxBytes;
    }

    public void setRxBytes(long rxBytes) {
        this.rxBytes = rxBytes;
    }

    public long getTxBytes() {
        return txBytes;
    }

    public void setTxBytes(long txBytes) {
        this.txBytes = txBytes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(encapsulation);
        parcel.writeString(hardwareAddress);
        parcel.writeString(inetAddress);
        parcel.writeString(inetPeerAddress);
        parcel.writeString(inetBroadcastAddress);
        parcel.writeString(inetNetmask);
        parcel.writeInt(up ? 1 : 0);
        parcel.writeInt(loopback ? 1 : 0);
        parcel.writeInt(pointToPoint ? 1 : 0);
        parcel.writeInt(broadcast ? 1 : 0);
        parcel.writeInt(running ? 1 : 0);
        parcel.writeInt(multicast ? 1 : 0);
        parcel.writeInt(mtu);
        parcel.writeInt(metric);
        parcel.writeLong(rxPackets);
        parcel.writeLong(rxErrors);
        parcel.writeLong(rxDropped);
        parcel.writeLong(rxOverruns);
        parcel.writeLong(rxFrame);
        parcel.writeLong(txPackets);
        parcel.writeLong(txErrors);
        parcel.writeLong(txDropped);
        parcel.writeLong(txOverruns);
        parcel.writeLong(txCarrier);
        parcel.writeLong(collisions);
        parcel.writeLong(txQueueLen);
        parcel.writeLong(rxBytes);
        parcel.writeLong(txBytes);
    }

    public static final Creator<Interface> CREATOR = new Creator<Interface>() {
        @Override
        public Interface createFromParcel(Parcel parcel) {
            Interface iface = new Interface();
            iface.setName(parcel.readString());
            iface.setEncapsulation(parcel.readString());
            iface.setHardwareAddress(parcel.readString());
            iface.setInetAddress(parcel.readString());
            iface.setInetPeerAddress(parcel.readString());
            iface.setInetBroadcastAddress(parcel.readString());
            iface.setInetNetmask(parcel.readString());
            iface.setUp(parcel.readInt() != 0);
            iface.setLoopback(parcel.readInt() != 0);
            iface.setPointToPoint(parcel.readInt() != 0);
            iface.setBroadcast(parcel.readInt() != 0);
            iface.setRunning(parcel.readInt() != 0);
            iface.setMulticast(parcel.readInt() != 0);
            iface.setMtu(parcel.readInt());
            iface.setMetric(parcel.readInt());
            iface.setRxPackets(parcel.readLong());
            iface.setRxErrors(parcel.readLong());
            iface.setRxDropped(parcel.readLong());
            iface.setRxOverruns(parcel.readLong());
            iface.setRxFrame(parcel.readLong());
            iface.setTxPackets(parcel.readLong());
            iface.setTxErrors(parcel.readLong());
            iface.setTxDropped(parcel.readLong());
            iface.setTxOverruns(parcel.readLong());
            iface.setTxCarrier(parcel.readLong());
            iface.setCollisions(parcel.readLong());
            iface.setTxQueueLen(parcel.readLong());
            iface.setRxBytes(parcel.readLong());
            iface.setTxBytes(parcel.readLong());
            return iface;
        }

        @Override
        public Interface[] newArray(int i) {
            return new Interface[i];
        }
    };

    @Override
    public String toString() {
        return "Interface{" +
                "name='" + name + '\'' +
                ", encapsulation='" + encapsulation + '\'' +
                ", hardwareAddress='" + hardwareAddress + '\'' +
                ", inetAddress='" + inetAddress + '\'' +
                ", inetPeerAddress='" + inetPeerAddress + '\'' +
                ", inetBroadcastAddress='" + inetBroadcastAddress + '\'' +
                ", inetNetmask='" + inetNetmask + '\'' +
                ", up=" + up +
                ", loopback=" + loopback +
                ", pointToPoint=" + pointToPoint +
                ", broadcast=" + broadcast +
                ", running=" + running +
                ", multicast=" + multicast +
                ", mtu=" + mtu +
                ", metric=" + metric +
                ", rxPackets=" + rxPackets +
                ", rxErrors=" + rxErrors +
                ", rxDropped=" + rxDropped +
                ", rxOverruns=" + rxOverruns +
                ", rxFrame=" + rxFrame +
                ", txPackets=" + txPackets +
                ", txErrors=" + txErrors +
                ", txDropped=" + txDropped +
                ", txOverruns=" + txOverruns +
                ", txCarrier=" + txCarrier +
                ", collisions=" + collisions +
                ", txQueueLen=" + txQueueLen +
                ", rxBytes=" + rxBytes +
                ", txBytes=" + txBytes +
                '}';
    }
}
