package pt.isel.ipw.repository

interface TransactionManager {
    fun <R> run(block: Transaction.() -> R): R
}