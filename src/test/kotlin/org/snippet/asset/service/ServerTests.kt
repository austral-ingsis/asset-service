package org.snippet.asset.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.snippet.asset.service.Fixtures.BASIC_SNIPPET
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureWebTestClient
class ServerTests @Autowired constructor(val client: WebTestClient) {

    private val BASE = "/v1/asset"

    @Test
    fun `can create an asset and query it`() {
        val uri = mintUri("snippets", "basic-snippet.ps")
        createSnippet(uri, BASIC_SNIPPET)
        val response = client.get().uri(uri).exchange()
        response.expectStatus().isOk
        assert(response.hasBody(BASIC_SNIPPET))
    }

    @Test
    fun `delete of inexistant asset should return notFound`() {
        val uri = mintUri("doesnt-exist", "not-a-change")
        client.get().uri(uri).exchange().expectStatus().isNotFound
        client.delete().uri(uri).exchange().expectStatus().isNotFound
    }

    @Test
    fun `delete of created asset should return noContent`() {
        val uri = mintUri("snippets", "another-snippet.ps")
        createSnippet(uri, BASIC_SNIPPET)
        client.get().uri(uri).exchange().expectStatus().isOk
        client.delete().uri(uri).exchange().expectStatus().isNoContent
    }

    private fun mintUri(key: String) = "$BASE/$key"

    private fun mintUri(container: String, key: String) = "$BASE/$container/$key"

    private fun createSnippet(uri: String, value: String) {
        val response = client.post().uri(uri).bodyValue(value).exchange()
        response.expectStatus().isCreated
        response.expectHeader().location(uri)
    }

    private fun ResponseSpec.hasBody(value: String): Boolean {
        return readBodyAsString() == value
    }

    private fun ResponseSpec.readBodyAsString() =
        expectBody().returnResult().responseBody?.decodeToString()
}