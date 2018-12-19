package org.fogbowcloud.cli.utils;

import org.fogbowcloud.cli.exceptions.FogbowCLIException;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class KeyValueReaderUtilTest {

    @Test
    public void testConvertKeyValueListToMap() throws FogbowCLIException {
        List<String> parameters = new ArrayList<>();
        parameters.add("key1=value1");
        parameters.add("key2=value2");
        parameters.add("key3=value3");

        Map<String, String> keyValues = KeyValueReaderUtil.convertKeyValueListToMap(parameters);

        Assert.assertEquals(3, keyValues.size());
        Assert.assertEquals(Arrays.asList(new String[] {"key1", "key2", "key3"}),
                new ArrayList<>(keyValues.keySet()));
        Assert.assertEquals(Arrays.asList(new String[] {"value1", "value2", "value3"}),
                new ArrayList<>(keyValues.values()));
    }
}
