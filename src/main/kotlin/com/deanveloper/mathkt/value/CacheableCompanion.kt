package com.deanveloper.mathkt.value

import java.util.*

/**
 * @author Dean B on 12/8/2016.
 */
open class CacheableCompanion<in T, out U>(private val uCreator: (T) -> U) {
    private val cache = WeakHashMap<T, U>()
    
    operator fun get(value: T): U = cache.getOrPut(value) { uCreator(value) }
    
    fun valueOf(value: T) = get(value)
}