package cloud.fogbow.cli.ras;

import cloud.fogbow.common.exceptions.InvalidParameterException;

import java.util.HashMap;

public interface FogbowCliResource {
    HashMap getHTTPHashMap() throws InvalidParameterException;
}
