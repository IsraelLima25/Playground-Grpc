package br.com.grpc

import io.grpc.ManagedChannelBuilder

fun main() {

    val chanel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build()

    val client = FuncionarioServiceGrpc.newBlockingStub(chanel)
    val request = FuncionarioRequest.newBuilder()
        .setNome("Israel")
        .setCpf("124584545")
        .setSalario(200.0)
        .setIdade(30)
        .setAtivo(true)
        .setCargo(Cargo.DEV) // Dev não imprime pois o grpc considera o primeiro valor de ENUM como default, economizando
        // e agregando performace no trafégo dos dados na rede
        .addEnderecos(
            FuncionarioRequest.Endereco.newBuilder()
                .setLogradouro("Rua das rosas")
                .setCep("41290546")
                .setComplemento("Casa 18").build()
        )
        .build()

    val response:FuncionarioResponse = client.cadastrar(request)
    println(response)
}