package com.je_martinez.demo.features.todos.dtos.requests

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class TodoRequest @JsonCreator constructor(
    @field:NotBlank(message = "Title can't be blank.")
    @JsonProperty("title")
    val title: String,
    @JsonProperty("description")
    val description: String,
)