package org.snippet.asset.service.store

import com.azure.storage.blob.BlobAsyncClient
import com.azure.storage.blob.BlobServiceAsyncClient
import com.azure.storage.blob.models.BlobStorageException
import com.azure.storage.blob.models.ParallelTransferOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.nio.ByteBuffer
import kotlin.math.log

class AzureObjectStore(private val client: BlobServiceAsyncClient) : ObjectStore {

    private val logger = LoggerFactory.getLogger(AzureObjectStore::class.java)

    data class BlobAlreadyExists(val container: String, val key: String): Exception()

    companion object {
        val UPLOAD_OPTIONS = ParallelTransferOptions()
    }

    override suspend fun store(container: String, key: String, data: Flux<DataBuffer>): String {
        logger.info("Attempting to store blob to container $container with key $key")
        val buffers = toByteBuffers(data)
        try {
            getClient(container, key).upload(buffers, UPLOAD_OPTIONS).awaitSingle()
            logger.info("Stored blob to container $container with key $key")
            return "$container/$key"
        } catch (e: IllegalArgumentException) {
            throw BlobAlreadyExists(container, key)
        }
    }

    override suspend fun get(container: String, key: String): Flow<DataBuffer> {
        logger.info("Attempting to retrieve blob to container $container with key $key")
        return toDataBuffers(getClient(container, key).downloadStream())
    }

    override suspend fun delete(container: String, key: String): Boolean {
        logger.info("Attempting to delete blob to container $container with key $key")
        val result = getClient(container, key).deleteIfExists().awaitSingle()
        logger.info("Deleted blob to container $container with key $key")
        return result
    }

    private suspend fun getClient(container: String, key: String): BlobAsyncClient {
        val containerClient = client.getBlobContainerAsyncClient(container)
        containerClient.createIfNotExists().awaitSingle()
        return containerClient.getBlobAsyncClient(key)
    }

    private fun toByteBuffers(data: Flux<DataBuffer>) = data.flatMapSequential {
        it.readableByteBuffers()
            .iterator()
            .toFlux()
    }

    private fun toDataBuffers(data: Flux<ByteBuffer>) =  data.asFlow().map { DefaultDataBufferFactory().wrap(it) }
}