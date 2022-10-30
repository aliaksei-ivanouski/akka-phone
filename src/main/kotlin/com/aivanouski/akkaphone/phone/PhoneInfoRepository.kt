package com.aivanouski.akkaphone.phone

import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface PhoneInfoRepository: CrudRepository<PhoneInfo, UUID> {

    @Cacheable(cacheNames = ["phones"])
    @Query("select pi.imei from PhoneInfo pi")
    fun findAllIds(): List<UUID>
}
