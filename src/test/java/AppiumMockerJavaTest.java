import com.github.lzx.AppiumMocker;
import com.github.lzx.BaseCapabilities;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.lzx.enums.MockPlatform;

public class AppiumMockerJavaTest {
    private Logger logger = LoggerFactory.getLogger(AppiumMockerJavaTest.class);
    private String sessionId = "1cfa967f-7b11-4032-8011-99a5d5a833f8";
    private String port = "4730";
    private String host = "10.32.46.216";
    private MockPlatform platform = MockPlatform.ANDROID;
    private String brandPackage = null;
    private BaseCapabilities capabilities;

    @Before
    public void setUpCaps(){
        capabilities = new BaseCapabilities(host,port,sessionId,platform);
        if (brandPackage != null) capabilities.applyBrandPackage(brandPackage);
    }

    @Test
    public void mockAndroidDriver(){
        capabilities.setPlatform(MockPlatform.ANDROID);
        AndroidDriver androidDriver = (AndroidDriver) AppiumMocker.INSTANCE.mock(capabilities);
        logger.info(androidDriver.getPageSource());
    }

    @Test
    public void mockIOSDriver(){
        capabilities.setPlatform(MockPlatform.IOS);
        IOSDriver androidDriver = (IOSDriver) AppiumMocker.INSTANCE.mock(capabilities);
        logger.info(androidDriver.getPageSource());
    }
}
