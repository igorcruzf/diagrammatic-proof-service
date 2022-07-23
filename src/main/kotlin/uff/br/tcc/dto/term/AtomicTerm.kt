package uff.br.tcc.dto.term

data class AtomicTerm(val name: String) : ITerm {
    override fun name() = name
}
