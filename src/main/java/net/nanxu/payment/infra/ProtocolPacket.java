package net.nanxu.payment.infra;

import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ProtocolPacket.
 *
 * @author: P
 **/
@Data
@Accessors(chain = true)
public class ProtocolPacket {
    /**
     * Http Header UserAgent
     */
    private String userAgent;
    /**
     * Http Header
     */
    private Map<String, String> headers;
    /**
     * Http Param
     */
    private Map<String, String> params;
    
    private String body;

    public boolean isWap() {
        return userAgent.contains("wap");
    }

    public boolean isPc() {
        return userAgent.contains("pc");
    }

    public boolean isApp() {
        return userAgent.contains("app");
    }

}
