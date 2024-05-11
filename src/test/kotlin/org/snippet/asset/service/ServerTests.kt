package org.snippet.asset.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.snippet.asset.service.Fixtures.ANOTHER_SNIPPET
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

    @BeforeEach
    fun setUp() {
        val uri = mintUri("snippets", "basic-snippet.ps")
        deleteSnippet(uri)
    }

    @Test
    fun `can create an asset and query it`() {
        val uri = mintUri("snippets", "basic-snippet.ps")
        getSnippet(uri).expectStatus().isNotFound
        createSnippet(uri, BASIC_SNIPPET)
        val response = getSnippet(uri)
        response.expectStatus().isOk
        assert(response.hasBody(BASIC_SNIPPET))
    }

    @Test
    fun `can update an asset and query it`() {
        val uri = mintUri("snippets", "basic-snippet.ps")
        createSnippet(uri, BASIC_SNIPPET)
        updateSnippet(uri, ANOTHER_SNIPPET)
        val response = getSnippet(uri)
        response.expectStatus().isOk
        assert(response.hasBody(ANOTHER_SNIPPET))
    }

    @Test
    fun `delete of inexistant asset should return notFound`() {
        val uri = mintUri("doesnt-exist", "not-a-change")
        getSnippet(uri).expectStatus().isNotFound
        deleteSnippet(uri).expectStatus().isNotFound
    }

    @Test
    fun `delete of created asset should return noContent`() {
        val uri = mintUri("snippets", "another-snippet.ps")
        createSnippet(uri, BASIC_SNIPPET)
        getSnippet(uri).expectStatus().isOk
        deleteSnippet(uri).expectStatus().isNoContent
    }

    private fun deleteSnippet(uri: String) = client.delete().uri(uri).exchange()

    private fun getSnippet(uri: String) = client.get().uri(uri).exchange()

    private fun mintUri(key: String) = "$BASE/$key"

    private fun mintUri(container: String, key: String) = "$BASE/$container/$key"

    private fun createSnippet(uri: String, value: String) {
        val response = client.put().uri(uri).bodyValue(value).exchange()
        response.expectStatus().isCreated
        response.expectHeader().location(uri)
    }

    private fun updateSnippet(uri: String, value: String) {
        val response = client.put().uri(uri).bodyValue(value).exchange()
        response.expectStatus().isOk
    }

    private fun ResponseSpec.hasBody(value: String): Boolean {
        return readBodyAsString() == value
    }

    private fun ResponseSpec.readBodyAsString() =
        expectBody().returnResult().responseBody?.decodeToString()
}