package org.snippet.asset.service

import com.azure.core.exception.HttpResponseException
import com.azure.storage.blob.models.BlobStorageException
import org.snippet.asset.service.store.AzureObjectStore
import org.snippet.asset.service.store.AzureObjectStore.*
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.internalServerError
import org.springframework.http.ResponseEntity.notFound
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.sql.Blob


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class AzureExceptionHandler {
    //other exception handlers
    @ExceptionHandler(HttpResponseException::class)
    protected fun handleAzureException(
        ex: HttpResponseException
    ): ResponseEntity<Any> {
        return when(ex) {
            is BlobStorageException -> ResponseEntity.status(ex.statusCode).build()
            else -> internalServerError().build()
        }
    }
}

@ControllerAdvice
class StoreExceptionHandler {
    @ExceptionHandler(BlobAlreadyExists::class)
    protected fun handleAzureException(
        ex: BlobAlreadyExists
    ): ResponseEntity<Any> {
        return ResponseEntity.status(409).body("${ex.container}/${ex.key} already exists")
    }
}