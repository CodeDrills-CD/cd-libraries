package io.codedrills.libraries.clients.kotlin

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import io.codedrills.proto.external.ContestServiceGrpcKt.ContestServiceCoroutineStub
import io.codedrills.proto.external.GetScoreboardRequest
import io.codedrills.proto.external.GetScoreboardResponse
import java.io.Closeable
import java.util.concurrent.TimeUnit

class KotlinClient(private val channel: ManagedChannel) : Closeable {

    private fun getStub() : ContestServiceCoroutineStub {
        val stub = ContestServiceCoroutineStub(channel)

        // Create metadata and set headers
        val metadata = Metadata()
        metadata.put(Metadata.Key.of("x-auth-key", Metadata.ASCII_STRING_MARSHALLER), "your-key")

        // Attach metadata to the stub
        val stubWithHeaders = MetadataUtils.attachHeaders(stub, metadata)

        return stubWithHeaders
    }

    suspend fun getScoreboard(id: Int) : GetScoreboardResponse {
        val request = GetScoreboardRequest.newBuilder().setContestId(id).build()
        val response = getStub().getScoreboard(request)
        return response
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

/**
 * Greeter, uses first argument as name to greet if present;
 * greets "world" otherwise.
 */
suspend fun main(args: Array<String>) {
    val port = 6565

    val channel = ManagedChannelBuilder.forAddress("site.api.staging.codedrills.io", port).usePlaintext().build()

    val client = KotlinClient(channel)

    val id = 112
    val resp = client.getScoreboard(id)
    print(resp)
}