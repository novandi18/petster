package com.novandiramadhan.petster.common.dummy

import com.novandiramadhan.petster.domain.model.Volunteer

object VolunteerDummy {
    val volunteers = listOf(
        Volunteer(
            uuid = "volunteer_001",
            name = "John Smith",
            email = "john.smith@example.com",
            phoneNumber = "+1-555-1234",
            address = "123 Maple Street, Springfield"
        ),
        Volunteer(
            uuid = "volunteer_002",
            name = "Jane Doe",
            email = "jane.doe@example.com",
            phoneNumber = "+1-555-5678",
            address = "456 Oak Avenue, Shelbyville"
        ),
        Volunteer(
            uuid = "volunteer_003",
            name = "Michael Brown",
            email = "michael.brown@example.com",
            phoneNumber = "+1-555-8765",
            address = "789 Pine Road, Capital City"
        ),
        Volunteer(
            uuid = "volunteer_004",
            name = "Emily Davis",
            email = "emily.davis@example.com",
            phoneNumber = "+1-555-4321",
            address = "321 Elm Street, Ogdenville"
        ),
        Volunteer(
            uuid = "volunteer_005",
            name = "David Wilson",
            email = "david.wilson@example.com",
            phoneNumber = "+1-555-6789",
            address = "654 Birch Lane, North Haverbrook"
        )
    )
}