package org.fogbowcloud.cli.utils;

import org.fogbowcloud.cli.exceptions.FogbowCLIException;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class KeyValueUtilTest {

    @Test
    public void testConvertKeyValueListToMap() throws FogbowCLIException {
        String parameters = "key1=value1,key2=value2,key3=value3";

        Map<String, String> keyValues = new KeyValueUtil.KeyValueConverter().convert(parameters);

        Assert.assertEquals(3, keyValues.size());
        Assert.assertEquals(Arrays.asList(new String[] {"key1", "key2", "key3"}),
                new ArrayList<>(keyValues.keySet()));
        Assert.assertEquals(Arrays.asList(new String[] {"value1", "value2", "value3"}),
                new ArrayList<>(keyValues.values()));
    }
}
