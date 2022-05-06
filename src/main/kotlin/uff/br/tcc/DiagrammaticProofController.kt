package uff.br.tcc

import uff.br.tcc.model.Diagram
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/diagrams")
class DiagrammaticProofController {

//    @GetMapping("/validate-homomorphism")
//    fun validateHomomorphism(@RequestBody diagrams: String) {
//        val (leftDiagram, rightDiagram) = diagrams.toDiagramPair()
//    }

//    private fun String.toDiagramPair(): Pair<Diagram, Diagram> {
//        val diagrams = split("\\subseteq")
//        if(diagrams.count() != 2) throw Exception("Tem que ter dois diagramas")
//        return Pair(Diagram(), Diagram())
//    }
}