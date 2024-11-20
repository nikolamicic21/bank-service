package rs.micke21

import rs.micke21.User.Account

interface UserStorage {
    fun account(user: User): Account
    fun createAccount(user: User, initialDeposit: Money): Account
    fun deposit(user: User, amount: Money)
    fun withdraw(user: User, amount: Money)
    fun transfer(from: User, to: User, amount: Money)
}

class InMemoryUserStorage : UserStorage {

    private val userAccountStorage: MutableMap<User, Account> = mutableMapOf()

    override fun account(user: User): Account {
        if (!userAccountStorage.containsKey(user)) {
            throw AccountDoesNotExist()
        }

        return userAccountStorage.getValue(user)
    }

    override fun createAccount(user: User, initialDeposit: Money): Account {
        if (userAccountStorage.containsKey(user)) {
            throw AccountAlreadyCreated()
        }

        userAccountStorage[user] = user.Account(initialDeposit)

        return userAccountStorage.getValue(user)
    }

    override fun deposit(user: User, amount: Money) {
        if (!userAccountStorage.containsKey(user)) {
            throw AccountDoesNotExist()
        }

        val account = userAccountStorage.getValue(user)
        userAccountStorage[user] = user.Account(account.amount().plus(amount))
    }

    override fun withdraw(user: User, amount: Money) {
        if (!userAccountStorage.containsKey(user)) {
            throw AccountDoesNotExist()
        }

        val account = userAccountStorage.getValue(user)
        if (account.amount() < amount) {
            throw InsufficientFundsException()
        }

        userAccountStorage[user] = user.Account(account.amount().minus(amount))
    }

    override fun transfer(from: User, to: User, amount: Money) {
        withdraw(from, amount)
        deposit(to, amount)
    }
}