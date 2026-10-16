package com.kito.core.cache

import kotlinx.serialization.Serializable

@Serializable
data class CacheEntry<T>(val savedAt: Long, val value: T)
