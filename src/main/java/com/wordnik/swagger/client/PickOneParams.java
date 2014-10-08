package com.wordnik.swagger.client;

import com.wordnik.swagger.client.filters.AllowAll;
import com.wordnik.swagger.client.pickers.PassThrough;

/**
 * User: ivan
 * Date: 10/7/14
 * Time: 9:22 PM
 */
public class PickOneParams {

    String name;
    String path;
    HostFilter filter;
    HostPicker2 picker;

    public PickOneParams() {
            filter = new AllowAll();
            picker = new PassThrough();
        }

        public PickOneParams(String name, String path, HostFilter filter, HostPicker2 picker) {
            this.name = name;
            this.path = path;
            this.filter = filter;
            this.picker = picker;
        }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HostFilter getFilter() {
        return filter;
    }

    public void setFilter(HostFilter filter) {
        this.filter = filter;
    }

    public HostPicker2 getPicker() {
        return picker;
    }

    public void setPicker(HostPicker2 picker) {
        this.picker = picker;
    }
}
