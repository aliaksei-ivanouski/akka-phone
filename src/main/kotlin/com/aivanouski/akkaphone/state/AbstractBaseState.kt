package com.aivanouski.akkaphone.state

import com.fasterxml.jackson.annotation.JsonIgnore

abstract class AbstractBaseState(
    @JsonIgnore
    open val entityId: String
)