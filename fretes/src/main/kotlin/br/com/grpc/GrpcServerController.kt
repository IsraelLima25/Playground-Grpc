package br.com.grpc

import io.micronaut.grpc.server.GrpcEmbeddedServer
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import javax.inject.Inject

@Controller("/api")
class GrpcServerController(@Inject val grpcServer: GrpcEmbeddedServer) {

    @Get("/grpc-server/stop")
    fun stop(): HttpResponse<String> {
        grpcServer.stop()
        return HttpResponse.ok("is running? ${grpcServer.isRunning}")
    }

}