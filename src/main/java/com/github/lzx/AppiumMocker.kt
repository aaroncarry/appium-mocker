package com.github.lzx

import com.github.lzx.enum.MockPlatform
import io.appium.java_client.AppiumDriver
import io.appium.java_client.AppiumExecutionMethod
import io.appium.java_client.MobileCommand
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.internal.JsonToMobileElementConverter
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.AppiumCommandExecutor
import io.appium.java_client.remote.AppiumW3CHttpCommandCodec
import javassist.ClassPool
import javassist.CtNewConstructor
import org.openqa.selenium.remote.*
import org.openqa.selenium.remote.html5.RemoteLocationContext
import org.openqa.selenium.remote.internal.JsonToWebElementConverter
import java.net.URL

object AppiumMocker {
    fun mock(baseCapabilities: BaseCapabilities): AppiumDriver<*> {
        val mockDriver = addEmptyConstructorAndGetDriver(baseCapabilities.platform)
        reInitDriverBySessionId(baseCapabilities = baseCapabilities, driver = mockDriver)
        return mockDriver
    }

    private fun addEmptyConstructorAndGetDriver(mockPlatform: MockPlatform): AppiumDriver<*> {
        val appiumDriverClassName = "io.appium.java_client.AppiumDriver"
        val defaultGenericMobileDriverClassName = "io.appium.java_client.DefaultGenericMobileDriver"
        val classPool = ClassPool.getDefault()
        val appiumDriverCtClass = classPool.get(appiumDriverClassName)
        val defaultGenericMobileDriverCtClass = classPool.get(defaultGenericMobileDriverClassName)
        val defaultGenericMobileDriverConstructor = CtNewConstructor.make(
            "public ${defaultGenericMobileDriverCtClass.simpleName}() {super();}",
            defaultGenericMobileDriverCtClass
        )
        defaultGenericMobileDriverCtClass.addConstructor(defaultGenericMobileDriverConstructor)
        defaultGenericMobileDriverCtClass.toClass()

        val appiumDriverConstructor =
            CtNewConstructor.make("public ${appiumDriverCtClass.simpleName}() {}", appiumDriverCtClass)
        appiumDriverCtClass.addConstructor(appiumDriverConstructor)
        appiumDriverCtClass.toClass()

        val specialDriverClassName = when (mockPlatform) {
            MockPlatform.ANDROID -> "io.appium.java_client.android.AndroidDriver"
            MockPlatform.IOS -> "io.appium.java_client.ios.IOSDriver"
        }

        val specialDriverCtClass = classPool.get(specialDriverClassName)
        val specialDriverConstructor =
            CtNewConstructor.make("public ${specialDriverCtClass.simpleName}() {}", specialDriverCtClass)
        specialDriverCtClass.addConstructor(specialDriverConstructor)
        val specialDriverClass = specialDriverCtClass.toClass()

        return when (mockPlatform) {
            MockPlatform.ANDROID -> specialDriverClass.newInstance() as AndroidDriver<*>
            MockPlatform.IOS -> specialDriverClass.newInstance() as IOSDriver<*>
        }
    }

    private fun reInitDriverBySessionId(baseCapabilities: BaseCapabilities, driver: AppiumDriver<*>) {
        val remoteAddress = URL("http://${baseCapabilities.ip}:${baseCapabilities.port}/wd/hub")
        val appiumDriverClazz = AppiumDriver::class.java
        val remoteWebDriverClazz = appiumDriverClazz.superclass.superclass
        val sessionIdOfDriver = remoteWebDriverClazz.getDeclaredField("sessionId")
        sessionIdOfDriver.setAccessible(true)
        sessionIdOfDriver.set(driver, SessionId(baseCapabilities.sessionId))

        val cap = DesiredCapabilities()
        cap.setJavascriptEnabled(true)
        cap.setCapability("appPackage", baseCapabilities.brandPackage)
        cap.setCapability("platformName", if (baseCapabilities.platform.name.toLowerCase() == "android") "android" else "ios")
        cap.setCapability(
            "automationName",
            if (baseCapabilities.platform.name.toLowerCase() == "android") "uiautomator2" else "XCUITest"
        )
        val capabilitiesOfDriver = remoteWebDriverClazz.getDeclaredField("capabilities")
        capabilitiesOfDriver.setAccessible(true)
        capabilitiesOfDriver.set(driver, cap)


        val executor = AppiumCommandExecutor(MobileCommand.commandRepository, remoteAddress)
        val appiumCommandExecutorClass = AppiumCommandExecutor::class.java

        val setCommandCodec = appiumCommandExecutorClass.getDeclaredMethod("setCommandCodec", CommandCodec::class.java)
        setCommandCodec.setAccessible(true)
        setCommandCodec.invoke(executor, AppiumW3CHttpCommandCodec())

        val getAdditionalCommands = appiumCommandExecutorClass.getDeclaredMethod("getAdditionalCommands")
        getAdditionalCommands.setAccessible(true)

        val httpCommandExecutorClazz = appiumCommandExecutorClass.superclass
        val defineCommand =
            httpCommandExecutorClazz.getDeclaredMethod("defineCommand", String::class.java, CommandInfo::class.java)
        defineCommand.setAccessible(true)
        (getAdditionalCommands.invoke(executor) as Map<String, CommandInfo>).forEach { (K, V) ->
            defineCommand.invoke(
                executor,
                K,
                V
            )
        }

        val setResponseCodec =
            appiumCommandExecutorClass.getDeclaredMethod("setResponseCodec", ResponseCodec::class.java)
        setResponseCodec.setAccessible(true)
        setResponseCodec.invoke(executor, Dialect.W3C.responseCodec)

        val setCommandExecutor =
            remoteWebDriverClazz.getDeclaredMethod("setCommandExecutor", CommandExecutor::class.java)
        setCommandExecutor.setAccessible(true)
        setCommandExecutor.invoke(driver, executor)

        /**
         * this.executeMethod = new AppiumExecutionMethod(this);
        locationContext = new RemoteLocationContext(executeMethod);
        super.setErrorHandler(errorHandler);
        this.remoteAddress = executor.getAddressOfRemoteServer();
        this.setElementConverter(new JsonToMobileElementConverter(this, this));
         */

        val executeMethod = appiumDriverClazz.getDeclaredField("executeMethod")
        executeMethod.setAccessible(true)
        executeMethod.set(driver, AppiumExecutionMethod(driver as AppiumDriver<*>))

        val locationContext = appiumDriverClazz.getDeclaredField("locationContext")
        locationContext.setAccessible(true)
        locationContext.set(driver, RemoteLocationContext(executeMethod.get(driver) as ExecuteMethod))

        val errorHandler = appiumDriverClazz.getDeclaredField("errorHandler")
        errorHandler.setAccessible(true)

        val setErrorHandler = remoteWebDriverClazz.getDeclaredMethod("setErrorHandler", ErrorHandler::class.java)
        setErrorHandler.setAccessible(true)
        setErrorHandler.invoke(driver, errorHandler.get(driver))

        val driverRemoteAddress = appiumDriverClazz.getDeclaredField("remoteAddress")
        driverRemoteAddress.setAccessible(true)
        driverRemoteAddress.set(driver, remoteAddress)

        val setElementConverter =
            remoteWebDriverClazz.getDeclaredMethod("setElementConverter", JsonToWebElementConverter::class.java)
        setElementConverter.setAccessible(true)
        setElementConverter.invoke(driver, JsonToMobileElementConverter(driver))
    }
}