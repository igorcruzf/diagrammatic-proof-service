package uff.br.tcc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DiagrammaticProofServiceApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<DiagrammaticProofServiceApplication>(*args)
}
