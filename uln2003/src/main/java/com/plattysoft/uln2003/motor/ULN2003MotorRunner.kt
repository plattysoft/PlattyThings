package com.plattysoft.uln2003.motor

import com.plattysoft.uln2003.Direction
import com.plattysoft.uln2003.driver.ULN2003
import com.plattysoft.uln2003.driver.ULN2003Resolution
import com.plattysoft.uln2003.listener.motor.MotorRunner

class ULN2003MotorRunner(val uln2003: ULN2003,
                         steps: Int,
                         direction: Direction,
                         val resolution: ULN2003Resolution,
                         executionDurationNanos: Long) : MotorRunner(uln2003, steps, direction, executionDurationNanos) {

    override fun applyResolution() {
        uln2003.resolution = resolution
    }
}