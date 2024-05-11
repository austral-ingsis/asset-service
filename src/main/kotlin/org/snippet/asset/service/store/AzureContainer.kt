package org.snippet.asset.service.store

import com.azure.storage.blob.BlobServiceClientBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AzureContainer @Autowired constructor(val builder: BlobServiceClientBuilder){

    @Bean
    fun createObjectStore(): ObjectStore {
        val client = builder.buildAsyncClient()
        return AzureObjectStore(client)
    }
}