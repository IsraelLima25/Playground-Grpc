package br.com.grpc

import java.io.FileInputStream
import java.io.FileOutputStream

fun main() {
    val request = FuncionarioRequest.newBuilder()
        .setNome("Israel")
        .setCpf("124584545")
        .setSalario(200.0)
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

    println(request)

    // Serializar em arquivo simulando a rede
    request.writeTo(FileOutputStream("funcionario-request.bin"))

    // Deserializar em arquivo simulando a rede
    val request2 = FuncionarioRequest.newBuilder().mergeFrom(FileInputStream("funcionario-request.bin"))
    request2.setCargo(Cargo.GERENTE)
    println(request2)
}