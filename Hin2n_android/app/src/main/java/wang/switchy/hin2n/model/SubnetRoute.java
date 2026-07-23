package wang.switchy.hin2n.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public final class SubnetRoute implements Parcelable {

    private final String network;
    private final int prefixLength;
    private final String gatewayIp;

    public SubnetRoute(String network, int prefixLength, String gatewayIp) {
        if (!EdgeCmd.checkIPV4(network)) {
            throw new IllegalArgumentException("Invalid route network");
        }
        if (prefixLength < 0 || prefixLength > 32) {
            throw new IllegalArgumentException("Invalid route prefix length");
        }
        if (!EdgeCmd.checkIPV4(gatewayIp)) {
            throw new IllegalArgumentException("Invalid route gateway");
        }

        int networkAddress = ipv4ToInt(network);
        int mask = prefixLength == 0 ? 0 : -1 << (32 - prefixLength);
        if ((networkAddress & mask) != networkAddress) {
            throw new IllegalArgumentException("Route destination must be a network address");
        }

        this.network = network;
        this.prefixLength = prefixLength;
        this.gatewayIp = gatewayIp;
    }

    public static SubnetRoute fromCidr(String cidr, String gatewayIp) {
        if (cidr == null) {
            throw new IllegalArgumentException("Route destination is required");
        }

        String[] parts = cidr.trim().split("/", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Route destination must use CIDR notation");
        }

        final int prefixLength;
        try {
            prefixLength = Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid route prefix length", exception);
        }

        return new SubnetRoute(parts[0], prefixLength, gatewayIp == null ? null : gatewayIp.trim());
    }

    private SubnetRoute(Parcel in) {
        network = in.readString();
        prefixLength = in.readInt();
        gatewayIp = in.readString();
    }

    public String getNetwork() {
        return network;
    }

    public int getPrefixLength() {
        return prefixLength;
    }

    public String getGatewayIp() {
        return gatewayIp;
    }

    public String getCidr() {
        return network + "/" + prefixLength;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeString(network);
        destination.writeInt(prefixLength);
        destination.writeString(gatewayIp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SubnetRoute)) {
            return false;
        }
        SubnetRoute route = (SubnetRoute) object;
        return prefixLength == route.prefixLength
                && network.equals(route.network)
                && gatewayIp.equals(route.gatewayIp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(network, prefixLength, gatewayIp);
    }

    @Override
    public String toString() {
        return getCidr() + " via " + gatewayIp;
    }

    private static int ipv4ToInt(String address) {
        String[] octets = address.split("\\.");
        int result = 0;
        for (String octet : octets) {
            result = (result << 8) | Integer.parseInt(octet);
        }
        return result;
    }

    public static final Creator<SubnetRoute> CREATOR = new Creator<SubnetRoute>() {
        @Override
        public SubnetRoute createFromParcel(Parcel source) {
            return new SubnetRoute(source);
        }

        @Override
        public SubnetRoute[] newArray(int size) {
            return new SubnetRoute[size];
        }
    };
}
