package com.ringcentral.ta.glip.test

import com.github.lzx.AppiumMocker
import com.github.lzx.BaseCapabilities
import com.github.lzx.enums.MockPlatform
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory


class AppiumMockerKotlinTest {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val sessionId = "1cfa967f-7b11-4032-8011-99a5d5a833f8"
    private val port = "4730"
    private val host = "10.32.46.216"
    private val platform = MockPlatform.ANDROID
    private val brandPackage :String? = null
    private lateinit var capabilities: BaseCapabilities

    @Before
    fun setUpCaps(){
        capabilities = BaseCapabilities(ip = host, port = port, sessionId = sessionId, platform = platform)
        brandPackage?.let { capabilities.applyBrandPackage(it) }
    }

    @Test
    fun mockAndroidDriver() {
        capabilities.platform = MockPlatform.ANDROID
        val appiumDriver = AppiumMocker.mock(baseCapabilities = capabilities) as AndroidDriver
        logger.info(appiumDriver.pageSource)
    }

    @Test
    fun mockIOSDriver() {
        capabilities.platform = MockPlatform.IOS
        val appiumDriver = AppiumMocker.mock(baseCapabilities = capabilities) as IOSDriver
        logger.info(appiumDriver.pageSource)
    }
}
