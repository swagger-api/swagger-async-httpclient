package com.wordnik.swagger.client;

import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface ServiceLocator2 {
    public Future<Set<String>> locate(LocateParams params);
    public Set<String> locateBlocking(LocateParams params, long atMost, TimeUnit unit);

    public Future<String> pickOne(PickOneParams params);
    public String pickOneBlocking(PickOneParams params, long atMost, TimeUnit unit);

    public Future<Set<String>> locateAsUris(LocateParams params);
    public Future<Set<String>> locateAsUrisBlocking(LocateParams params, long atMost, TimeUnit unit);

    public Future<String> pickOneAsUri(PickOneParams params);
    public String pickOneAsUriBlocking(PickOneParams params, long atMost, TimeUnit unit);
}
