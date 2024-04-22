package com.chargepoint.authenticationservice.repository

import com.chargepoint.authenticationservice.model.User
import org.springframework.stereotype.Repository

@Repository
class UsersRepository {
    private val users = arrayOf(
        User("af22009d-efc8-4cc4-8802-fa7b2bb98330", true),
        User("bb72649d-9246-423c-a56e-c8b92117dcdd", false),
        User("af22009d-efc8-4cc4-8", true),
        User("68276308-0731-4961-b", false),
        User("018f0127-8ee2-77a5-9f85-f4ad4f40a068-018f0127-8ee2-77a5-9f85-f4ad4f40a068-018f01", true),
        User("018f0128-d256-7041-b35f-851d55182364-018f0128-d256-7041-b35f-851d55182364-018f01", false),
    )

    fun findOneById(userId: String): User? {
        return users.find { it.id == userId }
    }
}