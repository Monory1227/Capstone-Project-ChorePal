package com.example.chorepalcowboysg6

data class HouseholdMemberRow(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val userType: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val zip: String = "",
    val email: String = ""
)

data class ChildOption(
    val uid: String = "",
    val displayName: String = ""
)

data class ChoreRow(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val dueDate: String = "",
    val rewardAmount: String = "",
    val assignedChildUid: String = "",
    val assignedChildName: String = "",
    val createdByUid: String = "",
    val createdByName: String = "",
    val createdByRole: String = "",
    val householdId: String = "",
    val status: String = "OPEN"
)

fun formatRole(role: String): String {
    return when (role.uppercase()) {
        "PARENT" -> "Parent"
        "ADULT" -> "Adult"
        "CHILD" -> "Child"
        else -> role
    }
}


