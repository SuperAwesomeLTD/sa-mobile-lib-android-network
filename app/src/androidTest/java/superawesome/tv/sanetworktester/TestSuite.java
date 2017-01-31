package superawesome.tv.sanetworktester;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SANetwork_DownloadItem_Tests.class,
        SANetwork_DownloadQueue_Tests.class,
        SANetwork_SAAsyncTask_Tests.class,
        SANetwork_SANetwork_Tests.class,
        SANetwork_SAFileDownloader_Tests.class,
        SANetwork_SAFileListDownloader_Tests.class
})
public class TestSuite {
}
