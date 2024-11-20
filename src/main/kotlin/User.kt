package rs.micke21

import java.math.BigDecimal

typealias Money = BigDecimal

class User(val name: String, private val userStorage: UserStorage) {

    inner class Account(amount: Money) {

        private var _amount: Money = amount

        fun amount(): Money = _amount

        fun user(): User = this@User

        fun deposit(amount: Money): Account {
            userStorage.deposit(this@User, amount)
            val account = userStorage.account(this@User)
            _amount = account.amount()

            return account
        }

        fun withdraw(amount: Money): Account {
            userStorage.withdraw(this@User, amount)
            val account = userStorage.account(this@User)
            _amount = account.amount()

            return account
        }

        fun transfer(to: User, amount: Money): Account {
            userStorage.transfer(this@User, to, amount)
            val account = userStorage.account(this@User)
            _amount = account.amount()

            return account
        }
    }

    fun createAccount(amount: Money): Account {
        return userStorage.createAccount(this, amount)
    }

    fun account(): Account {
        return userStorage.account(this)
    }
}