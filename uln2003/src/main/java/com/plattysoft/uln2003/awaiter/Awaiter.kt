package com.plattysoft.uln2003.awaiter

interface Awaiter {
    fun await(millis: Long, nanos: Int)
}