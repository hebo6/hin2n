package wang.switchy.hin2n.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class SubnetRouteTest {

    @Test
    public void parsesCidrAndGateway() {
        SubnetRoute route = SubnetRoute.fromCidr(
                "192.168.1.0/24",
                "10.150.99.200");

        assertEquals("192.168.1.0", route.getNetwork());
        assertEquals(24, route.getPrefixLength());
        assertEquals("10.150.99.200", route.getGatewayIp());
    }

    @Test
    public void acceptsDefaultRoute() {
        SubnetRoute route = SubnetRoute.fromCidr(
                "0.0.0.0/0",
                "10.150.99.200");

        assertEquals("0.0.0.0/0", route.getCidr());
    }

    @Test
    public void rejectsHostAddressAsNetwork() {
        assertThrows(IllegalArgumentException.class, () ->
                SubnetRoute.fromCidr("192.168.1.1/24", "10.150.99.200"));
    }

    @Test
    public void rejectsInvalidPrefixLength() {
        assertThrows(IllegalArgumentException.class, () ->
                SubnetRoute.fromCidr("192.168.1.0/33", "10.150.99.200"));
    }

    @Test
    public void rejectsInvalidGateway() {
        assertThrows(IllegalArgumentException.class, () ->
                SubnetRoute.fromCidr("192.168.1.0/24", "not-an-ip"));
    }
}
