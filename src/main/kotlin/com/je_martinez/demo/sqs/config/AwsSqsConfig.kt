package com.je_martinez.demo.sqs.config

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import org.springframework.context.annotation.Configuration
import aws.sdk.kotlin.services.sqs.SqsClient
import aws.smithy.kotlin.runtime.net.url.Url
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment


@Configuration
class AwsSqsConfig(
    private val env: Environment
) {
    @Bean
    fun amazonSQS(): SqsClient{
        return SqsClient{
            region = "us-east-1"
            endpointUrl = Url.parse("http://localhost:4566") // 👈 LocalStack SQS endpoint
            credentialsProvider = StaticCredentialsProvider {
                accessKeyId = env.getRequiredProperty("aws.credentials.accessKeyId")
                secretAccessKey = env.getRequiredProperty("aws.credentials.secretAccessKey")
            }
        }
    }

}