package org.snippet.asset.service.store

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux

data class StoreResult(val key: String, val isUpdate: Boolean)

interface ObjectStore {

    suspend fun store(container: String, key: String, data: Flux<DataBuffer>): StoreResult
    suspend fun get(container: String, key: String): Flow<DataBuffer>
    suspend fun delete(container: String, key: String): Boolean
}