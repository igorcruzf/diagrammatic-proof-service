package com.br.uff.tcc.service

import com.br.uff.tcc.utils.atomicDiagram
import com.br.uff.tcc.utils.normalIntersectionDiagram
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HomomorphismValidatorTest {

    @Test
    fun `R intersection S in included in R` (){
        val leftDiagram = normalIntersectionDiagram("R", "S")
        val rightDiagram = atomicDiagram("R")

        assertTrue(HomomorphismValidator(leftDiagram, rightDiagram).validate())
    }

    @Test
    fun `R intersection S in included in S` (){
        val leftDiagram = normalIntersectionDiagram("R", "S")
        val rightDiagram = atomicDiagram("S")

        assertTrue(HomomorphismValidator(leftDiagram, rightDiagram).validate())
    }

    @Test
    fun `R is not included in R intersection S`(){
        val rightDiagram = normalIntersectionDiagram("R", "S")
        val leftDiagram = atomicDiagram("R")

        assertFalse(HomomorphismValidator(leftDiagram, rightDiagram).validate())
    }

    @Test
    fun `S is not included in R intersection S`(){
        val rightDiagram = normalIntersectionDiagram("R", "S")
        val leftDiagram = atomicDiagram("S")

        assertFalse(HomomorphismValidator(leftDiagram, rightDiagram).validate())
    }

    @Test
    fun `R intersection S is included in S intersection R and vice versa`(){
        val rightDiagram = normalIntersectionDiagram("R", "S")
        val leftDiagram = normalIntersectionDiagram("S", "R")

        assertTrue(HomomorphismValidator(leftDiagram, rightDiagram).validate())
        assertTrue(HomomorphismValidator(rightDiagram, leftDiagram).validate())
    }

    @Test
    fun `R intersection S is included in R intersection R`(){
        val rightDiagram = normalIntersectionDiagram("R", "R")
        val leftDiagram = normalIntersectionDiagram("R", "S")

        assertTrue(HomomorphismValidator(leftDiagram, rightDiagram).validate())
    }

}