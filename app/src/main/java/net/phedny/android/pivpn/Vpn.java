package net.phedny.android.pivpn;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mark on 13/07/14.
 */
public class Vpn implements Parcelable {

    private String name;
    private boolean connected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(connected ? 1 : 0);
    }

    public static final Creator<Vpn> CREATOR = new Creator<Vpn>() {
        @Override
        public Vpn createFromParcel(Parcel parcel) {
            Vpn vpn = new Vpn();
            vpn.setName(parcel.readString());
            vpn.setConnected(parcel.readInt() != 0);
            return vpn;
        }

        @Override
        public Vpn[] newArray(int i) {
            return new Vpn[i];
        }
    };

    @Override
    public String toString() {
        return "Vpn{" +
                "name='" + name + '\'' +
                ", connected=" + connected +
                '}';
    }
}
