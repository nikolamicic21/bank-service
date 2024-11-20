import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import rs.micke21.*

class UserTest {

    private lateinit var userStorage: UserStorage

    @BeforeEach
    fun setUp() {
        userStorage = InMemoryUserStorage()
    }

    @Test
    fun `given user, when create account with initial deposit and user, account should be created for the user with amount == initial deposit`() {
        // given
        val user = User("user 1", userStorage)

        val initialDeposit = Money("100.00")

        // when
        val account = user.createAccount(initialDeposit)

        // then
        assertEquals(user, account.user())
        assertEquals(initialDeposit, account.amount())
    }

    @Test
    fun `given user and user's account with initial deposit 100, when deposit is invoked with 50, account amount should be equal to 150`() {
        // given
        val user = User("user 1", userStorage)
        val initialAmount = Money("100.00")
        val amountToDeposit = Money("50.00")

        // when
        val account = user.createAccount(initialAmount)
            .deposit(amountToDeposit)

        // then
        val expectedAmount = initialAmount.plus(amountToDeposit)

        assertEquals(user, account.user())
        assertEquals(expectedAmount, account.amount())
    }

    @Test
    fun `given user and user's account with initial deposit 100, when withdraw is invoked with 50, account amount should be equal to 50`() {
        // given
        val user = User("user 1", userStorage)
        val initialAmount = Money("100.00")
        val amountToWithdraw = Money("50.00")

        // when
        val account = user.createAccount(initialAmount)
            .withdraw(amountToWithdraw)

        // then
        val expectedAmount = initialAmount.minus(amountToWithdraw)

        assertEquals(user, account.user())
        assertEquals(expectedAmount, account.amount())
    }

    @Test
    fun `given user and user's account with initial deposit 100, when withdraw is invoked with 150, InsufficientFundsException is thrown`() {
        // given
        val user = User("user 1", userStorage)
        val initialAmount = Money("100.00")
        val amountToWithdraw = Money("150.00")

        // then
        assertThrows(InsufficientFundsException::class.java) {
            // when
            user.createAccount(initialAmount)
                .withdraw(amountToWithdraw)
        }
    }

    @Test
    fun `given user and user's account with initial deposit 100, when account is invoked, then account amount is 100`() {
        // given
        val user = User("user 1", userStorage)
        val initialAmount = Money("100.00")

        // when
        val account = user.createAccount(initialAmount)

        // then
        assertEquals(user, account.user())
        assertEquals(initialAmount, account.amount())
    }

    @Test
    fun `given user1 with initial deposit 100 and user2 with initial deposit 50, when transfer 20 from user1 to user2, then user1 account amount is 80 and user2 account amount is 70`() {
        // given
        val user1 = User("user 1", userStorage)
        val initialAmount1 = Money("100.00")
        val user1Account = user1.createAccount(initialAmount1)

        val user2 = User("user 2", userStorage)
        val initialAmount2 = Money("50.00")
        user2.createAccount(initialAmount2)

        val amountToTransfer = Money("20.00")

        // when
        user1Account.transfer(user2, amountToTransfer)

        // then
        assertEquals(user1, user1Account.user())
        assertEquals(initialAmount1.minus(amountToTransfer), user1Account.amount())

        assertEquals(user2, user2.account().user())
        assertEquals(initialAmount2.plus(amountToTransfer), user2.account().amount())
    }

    @Test
    fun `given user1 with initial deposit 100 and user2 with initial deposit 50, when transfer 120 from user1 to user2, InsufficientFundsException is thrown`() {
        // given
        val user1 = User("user 1", userStorage)
        val initialAmount1 = Money("100.00")
        val user1Account = user1.createAccount(initialAmount1)

        val user2 = User("user 2", userStorage)
        val initialAmount2 = Money("50.00")
        user2.createAccount(initialAmount2)

        val amountToTransfer = Money("120.00")

        // then
        assertThrows(InsufficientFundsException::class.java) {
            // when
            user1Account.transfer(user2, amountToTransfer)
        }
    }

    @Test
    fun `given user without account, when deposit, AccountDoesNotExist is thrown`() {
        // given
        val user = User("user 1", userStorage)

        // then
        assertThrows(AccountDoesNotExist::class.java) {
            // when
            user.account().deposit(Money.ZERO)
        }
    }

    @Test
    fun `given user without account, when withdraw, AccountDoesNotExist is thrown`() {
        // given
        val user = User("user 1", userStorage)

        // then
        assertThrows(AccountDoesNotExist::class.java) {
            // when
            user.account().withdraw(Money.ZERO)
        }
    }

    @Test
    fun `given user without account, when account, AccountDoesNotExist is thrown`() {
        // given
        val user = User("user 1", userStorage)

        // then
        assertThrows(AccountDoesNotExist::class.java) {
            // when
            user.account()
        }
    }

    @Test
    fun `given user with account, when create account with same user, AccountAlreadyCreated is thrown`() {
        // given
        val user = User("user 1", userStorage)
        user.createAccount(Money("100.00"))

        // then
        assertThrows(AccountAlreadyCreated::class.java) {
            // when
            user.createAccount(Money("100.00"))
        }
    }
}