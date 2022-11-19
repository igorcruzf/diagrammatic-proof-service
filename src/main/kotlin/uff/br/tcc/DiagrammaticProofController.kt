package uff.br.tcc

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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

    @Operation(
        summary = "Validate homomorphism between expressions of BGL (Basic graph logic)",
        description = "Returns 200 if successful"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful Operation"),
        ]
    )
    @GetMapping("/validate-homomorphism")
    fun validateHomomorphism(
        @RequestParam
        expression: String,

        @RequestParam(required = false, defaultValue = "")
        hypotheses: List<String>
    ) = diagramService.validateHomomorphism(expression, hypotheses)
}
