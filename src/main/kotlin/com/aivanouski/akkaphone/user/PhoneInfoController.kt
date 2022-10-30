package com.aivanouski.akkaphone.user

import com.aivanouski.akkaphone.actor.PhoneBookingService
import com.aivanouski.akkaphone.phone.PhoneInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class PhoneInfoController(
    private val service: PhoneBookingService,
    private val phoneInfoRepository: PhoneInfoRepository
) {

    @GetMapping("/phones/{imei}/info")
    suspend fun getPhoneInfo(
        @PathVariable("imei") phoneUuid: UUID
    ) = service.getPhoneInfo(phoneUuid)

    @GetMapping("/phones/info")
    suspend fun getAllPhoneInfos() = service.getAllPhonesInfo(
        withContext(Dispatchers.IO) {
            phoneInfoRepository.findAllIds()
        }
    )
}
