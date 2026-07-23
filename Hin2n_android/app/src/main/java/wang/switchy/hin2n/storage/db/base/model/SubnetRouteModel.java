package wang.switchy.hin2n.storage.db.base.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

@Entity(nameInDb = "SUBNET_ROUTE")
public class SubnetRouteModel {

    @Id(autoincrement = true)
    private Long id;
    private long settingId;
    private String network;
    private int prefixLength;
    private String gatewayIp;
    private int routeOrder;

    public SubnetRouteModel(Long id, long settingId, String network, int prefixLength,
                            String gatewayIp, int routeOrder) {
        this.id = id;
        this.settingId = settingId;
        this.network = network;
        this.prefixLength = prefixLength;
        this.gatewayIp = gatewayIp;
        this.routeOrder = routeOrder;
    }

    public SubnetRouteModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getSettingId() {
        return settingId;
    }

    public void setSettingId(long settingId) {
        this.settingId = settingId;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public int getPrefixLength() {
        return prefixLength;
    }

    public void setPrefixLength(int prefixLength) {
        this.prefixLength = prefixLength;
    }

    public String getGatewayIp() {
        return gatewayIp;
    }

    public void setGatewayIp(String gatewayIp) {
        this.gatewayIp = gatewayIp;
    }

    public int getRouteOrder() {
        return routeOrder;
    }

    public void setRouteOrder(int routeOrder) {
        this.routeOrder = routeOrder;
    }
}
