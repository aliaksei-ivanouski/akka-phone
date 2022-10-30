package com.aivanouski.akkaphone.phone

import com.aivanouski.akkaphone.actor.BookingStatus
import java.time.Instant
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "phone_info")
data class PhoneInfo(
    @Id
    @Column(name = "id")
    val imei: UUID? = null,
    val brand: String? = null,
    val device: String? = null,
    val band: String? = null,
    var whoBooked: String? = null,
    @Enumerated(EnumType.STRING)
    var status: BookingStatus? = null,
    val updatedAt: Instant? = null,
    val createdAt: Instant? = null,
)
