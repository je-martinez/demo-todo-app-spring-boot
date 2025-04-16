package com.je_martinez.demo.annotations.current_user

import io.swagger.v3.oas.annotations.Hidden

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Hidden
annotation class CurrentUserId