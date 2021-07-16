package br.com.grpc

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CarrosEndPointTest(
    val grpcClient: CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub,
    val repository: CarroRepository
) {

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve adicionar um novo carro`() {
        val response = grpcClient.adicionar(
            CarroRequest.newBuilder()
                .setModelo("GOl")
                .setPlaca("axd-4585")
                .build()
        )

        with(response) {
            assertNotNull(id)
            assertTrue(repository.existsById(id))
        }
    }

    @Test
    fun `nao deve adicionar novo carro quando carro com placa já existente`() {
        //cenário
        val existente = repository.save(Carro(modelo = "Palio", placa = "OIP-tyri"))
        //ação
        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(
                CarroRequest.newBuilder().setPlaca("Ferrari")
                    .setPlaca(existente.placa).build()
            )
        }
        //validação
        with(erro) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("carro com placa existente", status.description)
        }
    }

    @Test
    fun `nao deve adicionar novo carro quando dados de entrada forem invalidos`(){

        //ação
        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(
                CarroRequest.newBuilder().setPlaca("")
                    .setPlaca("").build()
            )
        }
        //validação
        with(erro) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("dados de entrada inválidos", status.description)
            //TODO verificar as violações da bean validations
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub? {
            return CarrosGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}
