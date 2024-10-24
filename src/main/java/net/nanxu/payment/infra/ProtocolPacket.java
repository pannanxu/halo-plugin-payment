package net.nanxu.payment.infra;

import java.util.Map;
import lombok.Data;

/**
 * ProtocolPacket.
 *
 * @author: P
 **/
@Data
public class ProtocolPacket {
    /**
     * Http Header UserAgent
     */
    private String userAgent;
    /**
     * Http Header
     */
    private Map<String, String> headers;

    public boolean isWap() {
        return userAgent.contains("wap");
    }

    public boolean isPc() {
        return userAgent.contains("pc");
    }

}
