package com.kito

interface Platform {
    val name : String
}

expect fun getPlatform(): Platform
