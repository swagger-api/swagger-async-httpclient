package com.wordnik.swagger.client.pickers;

import com.wordnik.swagger.client.HostPicker2;
import com.wordnik.swagger.client.LocatableService;

/**
 * User: ivan
 * Date: 10/7/14
 * Time: 8:58 PM
 */
public class PassThrough implements HostPicker2 {
    @Override
    public LocatableService[] sort(LocatableService[] services, String serviceName) {
        return services;
    }
}
