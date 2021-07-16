package br.com.grpc

import com.google.protobuf.Any
import com.google.rpc.BadRequest
import com.google.rpc.Code
import com.google.rpc.ErrorDetailsProto
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FretesGrpcServer : FretesServiceGrpc.FretesServiceImplBase() {

    private val logger = LoggerFactory.getLogger(FretesGrpcServer::class.java)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {

        logger.info("Calculando frete para request: $request")

        val cep = request?.cep

        if (cep == null || cep.isBlank()) {
            val error = Status.INVALID_ARGUMENT
                .withDescription("cep deve ser informado")
                .asRuntimeException()
            responseObserver?.onError(error)
        }

        if (!cep!!.matches("[0-9]{5}-[0-9]{3}".toRegex())) {
            val error = Status.INVALID_ARGUMENT
                .withDescription("cep inválido")
                .augmentDescription("formato esperado deve ser 99999-999")
                .asRuntimeException()
            responseObserver?.onError(error)
        }

        // Simular uma verificação de segurança
        // Tratando um erro com uma mensagem mais detalhada
        if (cep.endsWith("333")) {
            val statusProto = com.google.rpc.Status.newBuilder()
                .setCode(Status.Code.PERMISSION_DENIED.value())
                .setMessage("usuario não pode acessar esse recurso")
                .addDetails(
                    Any.pack(
                        ErrorDetails.newBuilder()
                            .setCode(401)
                            .setMessage("token expirado")
                            .build()
                    )
                )
                .build()
            val e = StatusProto.toStatusRuntimeException(statusProto)
            responseObserver?.onError(e)
        }

        var valor = 0.0
        try {
            valor = Random.nextDouble(from = 0.0, until = 140.0)
            if (valor > 100) {
                throw IllegalStateException("Erro inesperado ao executar lógica de negócio")
            }
        } catch (e: Exception) {
            responseObserver?.onError(
                Status.INTERNAL.withDescription(e.message)
                    .withCause(e).asRuntimeException() // anexado ao Status, mas não enviado ao client
            )
        }

        val response: CalculaFreteResponse = CalculaFreteResponse.newBuilder()
            .setCep(request!!.cep)
            .setValor(valor)
            .build()

        logger.info("Frete calculado: $response")

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }

}