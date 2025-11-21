package com.intive.aifirst.petspot

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
