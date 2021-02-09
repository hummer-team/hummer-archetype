package ${package}.support.utils;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import io.netty.handler.codec.http.HttpHeaders;

public class IpUtil {
    private static final String UNKNOWN_STRING = "unknown";

    private IpUtil() {

    }

    public static String getRemoteAddr(HttpHeaders headers) {
        if (headers == null) {
            return "";
        }
        String ip = headers.get("Real_Ip");
        if (!Strings.isNullOrEmpty(ip)) {
            return ip.trim();
        }
        ip = headers.get("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = headers.get("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = headers.get("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = headers.get("remote_addr");
        }
        if (!Strings.isNullOrEmpty(ip)) {
            Iterable<String> ips = Splitter.on(",").split(ip);
            return Iterables.get(ips, 0, "").trim();
        }

        return ip;
    }
}
