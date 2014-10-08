package com.wordnik.swagger.client.filters;

import com.wordnik.swagger.client.HostFilter;
import com.wordnik.swagger.client.LocatableService;

/**
 * User: ivan
 * Date: 10/7/14
 * Time: 8:56 PM
 */
public class AllowAll implements HostFilter {
    @Override
    public LocatableService[] filter(LocatableService[] hosts, String[] tags) {
        return hosts;
    }
}
