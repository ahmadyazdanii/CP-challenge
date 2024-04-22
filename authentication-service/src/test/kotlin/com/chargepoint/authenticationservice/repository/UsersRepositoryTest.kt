package com.chargepoint.authenticationservice.repository

import com.chargepoint.authenticationservice.model.User
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

class UsersRepositoryTest {
    private val usersRepository = UsersRepository()

    @Nested
    @DisplayName("When findOneById was called")
    inner class WhenFindOneByIdWasCalled {
        private val existsUser = User("af22009d-efc8-4cc4-8802-fa7b2bb98330", true)
        private val notExistsUser = User("af", true)

        @Test
        fun `Should return a user instance if exists`() {
            assertEquals(
                usersRepository.findOneById(existsUser.id),
                existsUser
            )
        }

        @Test
        fun `Should not return a user if not exists`() {
            assertNull(
                usersRepository.findOneById(notExistsUser.id)
            )
        }
    }
}