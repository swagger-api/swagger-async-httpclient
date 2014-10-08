package com.wordnik.swagger.client;

public interface HostFilter {
    public LocatableService[] filter(LocatableService[] hosts, String[] tags);
}