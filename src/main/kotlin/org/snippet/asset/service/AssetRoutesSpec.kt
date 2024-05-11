package org.snippet.asset.service

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import reactor.core.publisher.Flux
import io.swagger.v3.oas.annotations.parameters.RequestBody as RequestBodyDoc

@RequestMapping("/v1/asset")
interface AssetRoutesSpec {

    @GetMapping("/{container}/{key}")
    @Operation(
        summary = "Get an asset",
        parameters = [
            Parameter(name = "container", description = "container in which to add the asset, don't include '/'", required = true, example = "snippets"),
            Parameter(name = "key", description = "name of the asset", required = true, example = "basic-snippet.ps")
        ],
        responses = [
            ApiResponse(responseCode = "404", content = [Content()], description = "Asset was not found"),
            ApiResponse(responseCode = "200", content = [
                Content(mediaType = "text/event-stream", schema = Schema(implementation = String::class))
            ])
        ]
    )
    suspend fun getAsset(@PathVariable container: String, @PathVariable key: String): Flow<DataBuffer>

    @PostMapping("/{container}/{key}")
    @Operation(
        summary = "Create an asset",
        parameters = [
            Parameter(name = "container", description = "container in which to add the asset, don't include '/'", required = true, example = "snippets"),
            Parameter(name = "key", description = "name of the asset", required = true, example = "basic-snippet.ps")
        ],
        requestBody = RequestBodyDoc(content = [Content(schema = Schema(implementation = String::class))]),
        responses = [
            ApiResponse(responseCode = "201", content = [Content()], headers = [
                Header(name = "Location")
            ]),
            ApiResponse(responseCode = "409", content = [Content(schema = Schema(implementation = String::class), mediaType = "text/plain")])
        ]
    )
    suspend fun createAsset(@PathVariable container: String, @PathVariable key: String, @RequestBody value: Flux<DataBuffer>): ResponseEntity<Unit>

    @DeleteMapping("/{container}/{key}")
    @Operation(
        summary = "Delete an asset",
        parameters = [
            Parameter(name = "container", description = "container in which to add the asset, don't include '/'", required = true, example = "snippets"),
            Parameter(name = "key", description = "name of the asset", required = true, example = "basic-snippet.ps")
        ],
        responses = [
            ApiResponse(responseCode = "404", content = [Content()], description = "Asset was not found"),
            ApiResponse(responseCode = "200", content = [Content()])
        ]
    )
    suspend fun deleteAsset(@PathVariable container: String, @PathVariable key: String): ResponseEntity<Unit>
}