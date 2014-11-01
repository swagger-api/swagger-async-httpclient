package com.wordnik.swagger.client;

import com.ning.http.client.ListenableFuture;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface ServiceLocator2 {
    public ListenableFuture<Set<String>> locate(LocateParams params);
    public Set<String> locateBlocking(LocateParams params, long atMost, TimeUnit unit);

    public ListenableFuture<String> pickOne(PickOneParams params);
    public String pickOneBlocking(PickOneParams params, long atMost, TimeUnit unit);

    public ListenableFuture<Set<String>> locateAsUris(LocateParams params);
    public ListenableFuture<Set<String>> locateAsUrisBlocking(LocateParams params, long atMost, TimeUnit unit);

    public ListenableFuture<String> pickOneAsUri(PickOneParams params);
    public String pickOneAsUriBlocking(PickOneParams params, long atMost, TimeUnit unit);
}
