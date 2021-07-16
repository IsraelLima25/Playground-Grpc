package br.com.grpc

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class Carro(
    @field:NotBlank
    @Column(nullable = false)
    val placa: String,
    @field:NotBlank
    @Column(nullable = false)
    val modelo: String
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

}
