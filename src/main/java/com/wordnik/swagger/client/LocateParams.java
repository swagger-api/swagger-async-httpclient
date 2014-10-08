package com.wordnik.swagger.client;


import com.wordnik.swagger.client.filters.AllowAll;
import com.wordnik.swagger.client.pickers.PassThrough;

public final class LocateParams {
    String name;
    HostFilter filter;
    HostPicker2 picker;

    public LocateParams() {
        filter = new AllowAll();
        picker = new PassThrough();
    }

    public LocateParams(String name, HostFilter filter, HostPicker2 picker) {
        this.name = name;
        this.filter = filter;
        this.picker = picker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocateParams that = (LocateParams) o;

        if (!filter.equals(that.filter)) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (!picker.equals(that.picker)) return false;

        return true;
    }

    
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + filter.hashCode();
        result = 31 * result + picker.hashCode();
        return result;
    }
}