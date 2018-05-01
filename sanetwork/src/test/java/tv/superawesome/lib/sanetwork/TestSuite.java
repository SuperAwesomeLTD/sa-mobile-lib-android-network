package tv.superawesome.lib.sanetwork;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import tv.superawesome.lib.sanetwork.file.TestSAFileDownloader;
import tv.superawesome.lib.sanetwork.file.TestSAFileItem;
import tv.superawesome.lib.sanetwork.request.TestSANetwork;
import tv.superawesome.lib.sanetwork.request.TestSANetworkUtils;

/**
 * Created by gabriel.coman on 30/04/2018.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestSAFileItem.class,
        TestSANetwork.class,
        TestSAFileDownloader.class,
        TestSANetworkUtils.class
})
public class TestSuite {
}
