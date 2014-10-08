package com.wordnik.swagger.client;

import java.util.Arrays;

/**
 * User: ivan
 * Date: 10/7/14
 * Time: 8:44 PM
 */
public final class LocatableService {
    String[] hosts;
    int port;
    String contextPath;
    String[] tags;
    String apiKey;
    String pingPath;

    public LocatableService() {
        hosts = new String[0];
        port = -1;
        contextPath = "/api";
        tags = new String[0];
        apiKey = null;
        pingPath = null;
    }

    public LocatableService(String[] hosts, int port) {
        this.hosts = hosts;
        this.port = port;
        contextPath = "/api";
        tags = new String[0];
        apiKey = null;
        pingPath = null;
    }

    public LocatableService(String[] hosts, int port, String[] tags) {
        this.hosts = hosts;
        this.port = port;
        this.tags = tags;
        contextPath = "/api";
        apiKey = null;
        pingPath = null;
    }

    public LocatableService(String[] hosts, int port, String contextPath, String[] tags, String apiKey, String pingPath) {
        this.hosts = hosts;
        this.port = port;
        this.contextPath = contextPath;
        this.tags = tags;
        this.apiKey = apiKey;
        this.pingPath = pingPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocatableService that = (LocatableService) o;

        if (port != that.port) return false;
        if (apiKey != null ? !apiKey.equals(that.apiKey) : that.apiKey != null) return false;
        if (!contextPath.equals(that.contextPath)) return false;
        if (!Arrays.equals(hosts, that.hosts)) return false;
        if (pingPath != null ? !pingPath.equals(that.pingPath) : that.pingPath != null) return false;
        if (!Arrays.equals(tags, that.tags)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(hosts);
        result = 31 * result + port;
        result = 31 * result + contextPath.hashCode();
        result = 31 * result + Arrays.hashCode(tags);
        result = 31 * result + (apiKey != null ? apiKey.hashCode() : 0);
        result = 31 * result + (pingPath != null ? pingPath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LocatableService{");
        sb.append("hosts=").append(hosts == null ? "null" : Arrays.asList(hosts).toString());
        sb.append(", port=").append(port);
        sb.append(", contextPath='").append(contextPath).append('\'');
        sb.append(", tags=").append(tags == null ? "null" : Arrays.asList(tags).toString());
        sb.append(", apiKey='").append(apiKey).append('\'');
        sb.append(", pingPath='").append(pingPath).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
