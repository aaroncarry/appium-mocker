package com.ringcentral.ta.glip.test

import com.github.lzx.AppiumMocker
import com.github.lzx.BaseCapabilities
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
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.remote.*
import org.openqa.selenium.remote.html5.RemoteLocationContext
import org.openqa.selenium.remote.internal.JsonToWebElementConverter
import org.slf4j.LoggerFactory
import java.net.URL


class AppiumMockerTest {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val sessionId = "1cfa967f-7b11-4032-8011-99a5d5a833f8"
    private val port = "4730"
    private val host = "10.32.46.216"
    private val platform = MockPlatform.ANDROID
    private val brandPackage :String? = null
    private lateinit var capabilities: BaseCapabilities

    @Before
    fun setUpCaps(){
        capabilities= BaseCapabilities(ip = host, port = port, sessionId = sessionId, platform = platform)
        brandPackage?.let { capabilities.setBrandPackage(it) }
    }

    @Test
    fun mockAndroidDriver() {
        val appiumDriver = AppiumMocker.mock(baseCapabilities = capabilities) as AndroidDriver
        logger.info(appiumDriver.pageSource)
    }

    @Test
    fun mockIOSDriver() {
        val appiumDriver = AppiumMocker.mock(baseCapabilities = capabilities) as IOSDriver
        logger.info(appiumDriver.pageSource)
    }
}
