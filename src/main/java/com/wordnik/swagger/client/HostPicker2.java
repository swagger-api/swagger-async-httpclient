package com.wordnik.swagger.client;

/**
 * User: ivan
 * Date: 10/7/14
 * Time: 8:42 PM
 */
public interface HostPicker2 {
    public LocatableService[] pick(LocatableService[] services, String serviceName);
}
