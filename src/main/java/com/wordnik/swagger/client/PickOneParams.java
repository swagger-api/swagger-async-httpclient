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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PickOneParams that = (PickOneParams) o;

        if (!filter.equals(that.filter)) return false;
        if (!name.equals(that.name)) return false;
        if (!path.equals(that.path)) return false;
        if (!picker.equals(that.picker)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + filter.hashCode();
        result = 31 * result + picker.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PickOneParams{");
        sb.append("name='").append(name).append('\'');
        sb.append(", path='").append(path).append('\'');
        sb.append(", filter=").append(filter);
        sb.append(", picker=").append(picker);
        sb.append('}');
        return sb.toString();
    }
}
