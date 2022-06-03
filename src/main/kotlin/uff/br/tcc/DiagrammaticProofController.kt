package uff.br.tcc

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uff.br.tcc.service.DiagramService

@RestController
@RequestMapping("/diagrams")
class DiagrammaticProofController(
    @Autowired val diagramService: DiagramService
) {

    @GetMapping("/validate-homomorphism")
    fun validateHomomorphism(
        @RequestParam
        expression: String
    ) = diagramService.transformDiagramsAndValidateHomomorphism(expression)
}
