package com.seongekim.tcc.logic;

import java.util.List;
import java.util.Map;

public class ExperimentLayer {
    private boolean enabled;
    private String name;
    private String ip;
    private int iterations;

    private Map<Integer, List<RollingClock>> results;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public Map<Integer, List<RollingClock>> getResults() {
        return results;
    }

    public void setResults(Map<Integer, List<RollingClock>> results) {
        this.results = results;
    }
}
