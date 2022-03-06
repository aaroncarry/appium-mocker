# appium-mocker
A tool that can mock out an existing Appium session, supports both Java and Kotlin.

# How to install latest appium-mocker Beta/Snapshots
```
<dependency>
    <groupId>com.github.lzx</groupId>
    <artifactId>appium-mocker</artifactId>
    <version>latest commit ID from master branch</version>
</dependency>
```


# Mock(Attach) a appium session

It is very simple to use, but the premise of using it is that there is already an existing session somewhere.

If not, we can create one via command line or desktop or something else.

e.g. create a appium session via curl command
> curl -X POST "http://{ip_address}:{port}/wd/hub/session/" -H 'content-type: application/json' -d '{"desiredCapabilities": {"platformName":"iOS","deviceName": "iPhone 8","udid": "{udid}","automationName": "XCUITest","noReset":"true","newCommandTimeout":"60000","NoReset":"true"}}'

get session info from response
> {"value":{"webStorageEnabled":false,"locationContextEnabled":false,"browserName":"","platform":"MAC","javascriptEnabled":true,"databaseEnabled":false,"takesScreenshot":true,"networkConnectionEnabled":false,"platformName":"iOS","deviceName":"iPhone 8","udid":"00008020-000304302699002E","automationName":"XCUITest","noReset":true,"newCommandTimeout":6000000,"NoReset":"true"},"sessionId":"fdddff85-0c28-4885-9b72-b38905e86c9c","status":0}

Just provide basic information, appium mocker will help you mock out an appium driver for testing

# Simple example in Kotlin
```aidl
    val capabilities= BaseCapabilities(ip = host, port = port, sessionId = sessionId, platform = platform)
    val appiumDriver = AppiumMocker.mock(baseCapabilities = capabilities) as AndroidDriver
    logger.info(appiumDriver.pageSource)
```

# Simple example in Java
```aidl
    BaseCapabilities capabilities = new BaseCapabilities(host,port,sessionId,platform);
    AndroidDriver androidDriver = (AndroidDriver) AppiumMocker.INSTANCE.mock(capabilities);
    logger.info(androidDriver.getPageSource());
```
