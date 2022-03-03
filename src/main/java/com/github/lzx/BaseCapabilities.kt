package com.github.lzx

import com.github.lzx.enum.MockPlatform

class BaseCapabilities(
    var ip: String,
    var port: String,
    var sessionId: String,
    var platform: MockPlatform
) {
    var brandPackage: String? = null

    fun setBrandPackage(brandPackage: String): BaseCapabilities = this.apply {
        this@BaseCapabilities.brandPackage = brandPackage
    }
}