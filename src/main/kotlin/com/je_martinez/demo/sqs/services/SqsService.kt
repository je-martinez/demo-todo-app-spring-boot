package com.je_martinez.demo.sqs.services

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.SendMessageRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.je_martinez.demo.utils.LoggerUtils
import com.je_martinez.demo.utils.LoggerUtils.logger
import kotlinx.coroutines.runBlocking
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import kotlin.math.log

@Service
class SqsService(
    private val sqsClient: SqsClient,
    private val environment: Environment
) {
    private val log by LoggerUtils.logger()

    fun sendMessage(message: Map<String, String>){
        val mapper = jacksonObjectMapper()
        val body = mapper.writeValueAsString(message)
        log.info("Sending message with messageBody $body")
        runBlocking {
            val request = SendMessageRequest{
                queueUrl = environment.getRequiredProperty("sqs.mainQueueUrl")
                messageBody = body
            }
            sqsClient.sendMessage(request)
        }
    }
}