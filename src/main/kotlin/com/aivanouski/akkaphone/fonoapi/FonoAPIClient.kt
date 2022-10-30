package com.aivanouski.akkaphone.fonoapi

import com.aivanouski.akkaphone.LoggerDelegate
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeCreator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration

@Component
class FonoAPIClient(
    private val objectMapper: ObjectMapper,
    @Value("\${fonoapi.key}") private val apiKey: String
) {

    private val logger by LoggerDelegate()

    private val apiUrl = "https://fonoapi.freshpixl.com/v1"
    private val httpClient: HttpClient = HttpClient.newBuilder().build()

    fun getPhoneInfo(brand: String?, device: String?): JsonNode? {
        val body = """
            {
                "token": "$apiKey"
                "brand": "$brand"
                "device": "$device"
            }
        """.trimIndent()
        val req = HttpRequest.newBuilder()
            .uri(URI.create("$apiUrl/getdevice"))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(5))
            .build()

        val response = httpClient.send(req, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() >= 400) {
            logger.error("Error requesting FonoAPI: statusCode=${response.statusCode()}, body=${response.body()}")
            return null
        }

        return objectMapper.readTree(response.body())
    }

}
