package org.snippet.asset.service

import kotlinx.coroutines.flow.Flow
import org.snippet.asset.service.store.ObjectStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.net.URI

@RestController
class AssetRoutes @Autowired constructor(private val store: ObjectStore): AssetRoutesSpec {

    override suspend fun getAsset(@PathVariable container: String, @PathVariable key: String): Flow<DataBuffer> {
        return store.get(container, key)
    }

    override suspend fun createAsset(@PathVariable container: String, @PathVariable key: String, @RequestBody value: Flux<DataBuffer>): ResponseEntity<Unit> {
        val result = store.store(container, key, value)
        return if (result.isUpdate) ok().build()
        else created(URI("/v1/asset/${result.key}")).build()
    }

    override suspend fun deleteAsset(@PathVariable container: String, @PathVariable key: String): ResponseEntity<Unit> {
        val deleted = store.delete(container, key)
        return if (deleted) noContent().build()
        else notFound().build()
    }
}