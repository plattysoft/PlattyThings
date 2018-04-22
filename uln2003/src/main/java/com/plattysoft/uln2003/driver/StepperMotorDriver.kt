package com.plattysoft.uln2003.driver

import com.plattysoft.uln2003.Direction

abstract class StepperMotorDriver : AutoCloseable {
    open var direction: Direction = Direction.CLOCKWISE

    abstract fun open()

    abstract fun performStep(stepDuration: StepDuration)
}