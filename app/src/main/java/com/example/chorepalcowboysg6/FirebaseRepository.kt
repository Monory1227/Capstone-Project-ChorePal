package com.example.chorepalcowboysg6

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun registerParent(
        firstName: String,
        lastName: String,
        dob: String,
        address: String,
        city: String,
        state: String,
        zip: String,
        email: String,
        password: String
    ): Result<Unit> {
        return runCatching {
            val authResult = auth.createUserWithEmailAndPassword(
                email.trim(),
                password
            ).await()

            val uid = authResult.user?.uid ?: error("Missing uid")

            val householdRef = db.collection("households").document()
            val householdId = householdRef.id

            db.runBatch { batch ->
                batch.set(
                    householdRef,
                    mapOf(
                        "id" to householdId,
                        "name" to "${lastName.trim()} Household",
                        "ownerUid" to uid,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )

                batch.set(
                    db.collection("users").document(uid),
                    mapOf(
                        "uid" to uid,
                        "firstName" to firstName.trim(),
                        "lastName" to lastName.trim(),
                        "dob" to dob.trim(),
                        "address" to address.trim(),
                        "city" to city.trim(),
                        "state" to state.trim(),
                        "zip" to zip.trim(),
                        "email" to email.trim(),
                        "role" to "PARENT",
                        "householdId" to householdId,
                        "active" to true,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )

                batch.set(
                    db.collection("households")
                        .document(householdId)
                        .collection("members")
                        .document(uid),
                    mapOf(
                        "uid" to uid,
                        "firstName" to firstName.trim(),
                        "lastName" to lastName.trim(),
                        "dob" to dob.trim(),
                        "address" to address.trim(),
                        "city" to city.trim(),
                        "state" to state.trim(),
                        "zip" to zip.trim(),
                        "email" to email.trim(),
                        "role" to "PARENT",
                        "householdId" to householdId,
                        "createdBy" to uid,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
            }.await()
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return runCatching {
            auth.signInWithEmailAndPassword(email.trim(), password).await()
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun loadCurrentUserProfile(): Result<Map<String, Any?>> {
        return runCatching {
            val uid = auth.currentUser?.uid ?: error("No logged-in user")
            val snapshot = db.collection("users").document(uid).get().await()
            snapshot.data ?: emptyMap()
        }
    }

    suspend fun createMemberAccount(
        firstName: String,
        lastName: String,
        dob: String,
        address: String,
        city: String,
        state: String,
        zip: String,
        email: String,
        password: String,
        role: String
    ): Result<Unit> {
        return runCatching {
            val normalizedRole = role.trim().uppercase()
            if (normalizedRole != "CHILD" && normalizedRole != "ADULT") {
                error("Only child or adult accounts can be created here.")
            }

            val currentUid = auth.currentUser?.uid ?: error("No logged-in user")
            val currentUserSnap = db.collection("users").document(currentUid).get().await()

            val currentRole = currentUserSnap.getString("role").orEmpty().uppercase()
            if (currentRole != "PARENT" && currentRole != "ADULT") {
                error("Only parent or adult accounts can create member accounts.")
            }

            val householdId = currentUserSnap.getString("householdId").orEmpty()
            if (householdId.isBlank()) {
                error("Missing household id")
            }

            val cleanEmail = email.trim()

            val existingUserQuery = db.collection("users")
                .whereEqualTo("email", cleanEmail)
                .get()
                .await()

            if (!existingUserQuery.isEmpty) {
                error("An account with that email already exists.")
            }

            val newUid = createSecondaryAuthUser(
                email = cleanEmail,
                password = password
            )

            db.runBatch { batch ->
                batch.set(
                    db.collection("users").document(newUid),
                    mapOf(
                        "uid" to newUid,
                        "firstName" to firstName.trim(),
                        "lastName" to lastName.trim(),
                        "dob" to dob.trim(),
                        "address" to address.trim(),
                        "city" to city.trim(),
                        "state" to state.trim(),
                        "zip" to zip.trim(),
                        "email" to cleanEmail,
                        "role" to normalizedRole,
                        "householdId" to householdId,
                        "active" to true,
                        "createdBy" to currentUid,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )

                batch.set(
                    db.collection("households")
                        .document(householdId)
                        .collection("members")
                        .document(newUid),
                    mapOf(
                        "uid" to newUid,
                        "firstName" to firstName.trim(),
                        "lastName" to lastName.trim(),
                        "dob" to dob.trim(),
                        "address" to address.trim(),
                        "city" to city.trim(),
                        "state" to state.trim(),
                        "zip" to zip.trim(),
                        "email" to cleanEmail,
                        "role" to normalizedRole,
                        "householdId" to householdId,
                        "createdBy" to currentUid,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                )
            }.await()
        }
    }

    suspend fun loadHouseholdMembers(): Result<List<HouseholdMemberRow>> {
        return runCatching {
            val currentUid = auth.currentUser?.uid ?: error("No logged-in user")
            val currentUserSnap = db.collection("users").document(currentUid).get().await()
            val householdId = currentUserSnap.getString("householdId").orEmpty()

            if (householdId.isBlank()) {
                return@runCatching emptyList()
            }

            val membersSnap = db.collection("households")
                .document(householdId)
                .collection("members")
                .get()
                .await()

            membersSnap.documents
                .map { it.toHouseholdMemberRow() }
                .sortedWith(
                    compareBy<HouseholdMemberRow> {
                        when (it.userType.uppercase()) {
                            "PARENT" -> 0
                            "ADULT" -> 1
                            "CHILD" -> 2
                            else -> 3
                        }
                    }.thenBy { it.firstName.uppercase() }
                        .thenBy { it.lastName.uppercase() }
                )
        }
    }

    suspend fun loadMemberById(memberUid: String): Result<HouseholdMemberRow> {
        return runCatching {
            val currentUid = auth.currentUser?.uid ?: error("No logged-in user")
            val currentUserSnap = db.collection("users").document(currentUid).get().await()
            val householdId = currentUserSnap.getString("householdId").orEmpty()

            if (householdId.isBlank()) {
                error("Missing household id")
            }

            val memberSnap = db.collection("households")
                .document(householdId)
                .collection("members")
                .document(memberUid)
                .get()
                .await()

            if (!memberSnap.exists()) {
                error("Member not found")
            }

            memberSnap.toHouseholdMemberRow()
        }
    }

    suspend fun updateMember(
        memberUid: String,
        firstName: String,
        lastName: String,
        address: String,
        city: String,
        state: String,
        zip: String,
        email: String
    ): Result<Unit> {
        return runCatching {
            val currentUid = auth.currentUser?.uid ?: error("No logged-in user")
            val currentUserSnap = db.collection("users").document(currentUid).get().await()
            val householdId = currentUserSnap.getString("householdId").orEmpty()

            if (householdId.isBlank()) {
                error("Missing household id")
            }

            val updates = mapOf(
                "firstName" to firstName.trim(),
                "lastName" to lastName.trim(),
                "address" to address.trim(),
                "city" to city.trim(),
                "state" to state.trim(),
                "zip" to zip.trim(),
                "email" to email.trim()
            )

            db.collection("users")
                .document(memberUid)
                .update(updates)
                .await()

            db.collection("households")
                .document(householdId)
                .collection("members")
                .document(memberUid)
                .update(updates)
                .await()
        }
    }

    suspend fun deleteMember(memberUid: String): Result<Unit> {
        return runCatching {
            val currentUid = auth.currentUser?.uid ?: error("No logged-in user")
            if (memberUid == currentUid) {
                error("You cannot delete the currently logged-in account from here.")
            }

            val currentUserSnap = db.collection("users").document(currentUid).get().await()
            val householdId = currentUserSnap.getString("householdId").orEmpty()

            if (householdId.isBlank()) {
                error("Missing household id")
            }

            db.collection("households")
                .document(householdId)
                .collection("members")
                .document(memberUid)
                .delete()
                .await()

            db.collection("users")
                .document(memberUid)
                .delete()
                .await()
        }
    }

    suspend fun loadHouseholdChildren(): Result<List<ChildOption>> {
        return runCatching {
            val currentUid = auth.currentUser?.uid ?: error("No logged-in user")
            val currentUserSnap = db.collection("users").document(currentUid).get().await()
            val householdId = currentUserSnap.getString("householdId").orEmpty()

            if (householdId.isBlank()) {
                return@runCatching emptyList()
            }

            val membersSnap = db.collection("households")
                .document(householdId)
                .collection("members")
                .get()
                .await()

            membersSnap.documents
                .map { it.toHouseholdMemberRow() }
                .filter { it.userType.equals("CHILD", ignoreCase = true) }
                .sortedBy { "${it.firstName} ${it.lastName}" }
                .map {
                    ChildOption(
                        uid = it.uid,
                        displayName = "${it.firstName} ${it.lastName}".trim()
                    )
                }
        }
    }

    suspend fun createChore(
        title: String,
        description: String,
        dueDate: String,
        rewardAmount: String,
        assignedChildUid: String,
        assignedChildName: String
    ): Result<Unit> {
        return runCatching {
            val currentUid = auth.currentUser?.uid ?: error("No logged-in user")
            val userSnap = db.collection("users").document(currentUid).get().await()

            val role = userSnap.getString("role").orEmpty().uppercase()
            if (role != "PARENT" && role != "ADULT") {
                error("Only parent or adult accounts can create chores.")
            }

            val householdId = userSnap.getString("householdId").orEmpty()
            if (householdId.isBlank()) {
                error("Missing household id")
            }

            val createdByName =
                "${userSnap.getString("firstName").orEmpty()} ${userSnap.getString("lastName").orEmpty()}".trim()

            val choreRef = db.collection("households")
                .document(householdId)
                .collection("chores")
                .document()

            choreRef.set(
                mapOf(
                    "id" to choreRef.id,
                    "title" to title.trim(),
                    "description" to description.trim(),
                    "dueDate" to dueDate.trim(),
                    "rewardAmount" to rewardAmount.trim(),
                    "assignedChildUid" to assignedChildUid,
                    "assignedChildName" to assignedChildName.trim(),
                    "createdByUid" to currentUid,
                    "createdByName" to createdByName,
                    "createdByRole" to role,
                    "householdId" to householdId,
                    "status" to "OPEN",
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    suspend fun loadChores(): Result<List<ChoreRow>> {
        return runCatching {
            val currentUid = auth.currentUser?.uid ?: error("No logged-in user")
            val userSnap = db.collection("users").document(currentUid).get().await()

            val householdId = userSnap.getString("householdId").orEmpty()
            val role = userSnap.getString("role").orEmpty().uppercase()

            if (householdId.isBlank()) {
                return@runCatching emptyList()
            }

            val choresSnap = db.collection("households")
                .document(householdId)
                .collection("chores")
                .get()
                .await()

            val statusOrder = mapOf(
                "REJECTED" to 0,
                "OPEN" to 1,
                "IN PROGRESS" to 2,
                "COMPLETED" to 3,
                "APPROVED" to 4
            )

            val loadedChores = choresSnap.documents
                .map { it.toChoreRow() }
                .sortedWith(
                    compareBy<ChoreRow> { statusOrder[it.status.uppercase()] ?: 99 }
                        .thenBy { it.dueDate }
                        .thenBy { it.title.uppercase() }
                )

            if (role == "CHILD") {
                loadedChores.filter { it.assignedChildUid == currentUid }
            } else {
                loadedChores
            }
        }
    }

    suspend fun loadChoreById(choreId: String): Result<ChoreRow> {
        return runCatching {
            val currentUid = auth.currentUser?.uid ?: error("No logged-in user")
            val userSnap = db.collection("users").document(currentUid).get().await()
            val householdId = userSnap.getString("householdId").orEmpty()

            if (householdId.isBlank()) {
                error("Missing household id")
            }

            val choreSnap = db.collection("households")
                .document(householdId)
                .collection("chores")
                .document(choreId)
                .get()
                .await()

            if (!choreSnap.exists()) {
                error("Chore not found")
            }

            choreSnap.toChoreRow()
        }
    }

    suspend fun updateChore(
        choreId: String,
        title: String,
        description: String,
        dueDate: String,
        rewardAmount: String,
        assignedChildUid: String,
        assignedChildName: String
    ): Result<Unit> {
        return runCatching {
            val currentUid = auth.currentUser?.uid ?: error("No logged-in user")
            val userSnap = db.collection("users").document(currentUid).get().await()

            val role = userSnap.getString("role").orEmpty().uppercase()
            if (role != "PARENT" && role != "ADULT") {
                error("Only parent or adult accounts can edit chores.")
            }

            val householdId = userSnap.getString("householdId").orEmpty()
            if (householdId.isBlank()) {
                error("Missing household id")
            }

            db.collection("households")
                .document(householdId)
                .collection("chores")
                .document(choreId)
                .update(
                    mapOf(
                        "title" to title.trim(),
                        "description" to description.trim(),
                        "dueDate" to dueDate.trim(),
                        "rewardAmount" to rewardAmount.trim(),
                        "assignedChildUid" to assignedChildUid,
                        "assignedChildName" to assignedChildName.trim(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
        }
    }

    suspend fun updateChoreStatus(
        choreId: String,
        newStatus: String
    ): Result<Unit> {
        return runCatching {
            val normalizedStatus = newStatus.trim().uppercase()
            if (
                normalizedStatus != "IN PROGRESS" &&
                normalizedStatus != "COMPLETED"
            ) {
                error("Invalid status")
            }

            val currentUid = auth.currentUser?.uid ?: error("No logged-in user")
            val userSnap = db.collection("users").document(currentUid).get().await()
            val householdId = userSnap.getString("householdId").orEmpty()
            val role = userSnap.getString("role").orEmpty().uppercase()

            if (householdId.isBlank()) {
                error("Missing household id")
            }

            if (role != "CHILD") {
                error("Only child accounts can update chore status here.")
            }

            val choreRef = db.collection("households")
                .document(householdId)
                .collection("chores")
                .document(choreId)

            val choreSnap = choreRef.get().await()
            if (!choreSnap.exists()) {
                error("Chore not found")
            }

            val assignedChildUid = choreSnap.getString("assignedChildUid").orEmpty()
            if (assignedChildUid != currentUid) {
                error("You can only update chores assigned to you.")
            }

            choreRef.update(
                mapOf(
                    "status" to normalizedStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    suspend fun reviewChore(
        choreId: String,
        approved: Boolean
    ): Result<Unit> {
        return runCatching {
            val currentUid = auth.currentUser?.uid ?: error("No logged-in user")
            val userSnap = db.collection("users").document(currentUid).get().await()

            val role = userSnap.getString("role").orEmpty().uppercase()
            if (role != "PARENT" && role != "ADULT") {
                error("Only parent or adult accounts can review chores.")
            }

            val householdId = userSnap.getString("householdId").orEmpty()
            if (householdId.isBlank()) {
                error("Missing household id")
            }

            val reviewerName =
                "${userSnap.getString("firstName").orEmpty()} ${userSnap.getString("lastName").orEmpty()}".trim()

            val choreRef = db.collection("households")
                .document(householdId)
                .collection("chores")
                .document(choreId)

            val choreSnap = choreRef.get().await()
            if (!choreSnap.exists()) {
                error("Chore not found")
            }

            val currentStatus = choreSnap.getString("status").orEmpty().uppercase()
            if (currentStatus != "COMPLETED") {
                error("Only completed chores can be approved or rejected.")
            }

            val reviewedStatus = if (approved) "APPROVED" else "REJECTED"

            choreRef.update(
                mapOf(
                    "status" to reviewedStatus,
                    "reviewedByUid" to currentUid,
                    "reviewedByName" to reviewerName,
                    "reviewedAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    private suspend fun createSecondaryAuthUser(
        email: String,
        password: String
    ): String {
        val primaryApp = FirebaseApp.getInstance()
        val options: FirebaseOptions = primaryApp.options
        val secondaryAppName = "memberCreatorApp"

        val existingSecondaryApp = FirebaseApp.getApps(primaryApp.applicationContext)
            .firstOrNull { it.name == secondaryAppName }

        val secondaryApp = existingSecondaryApp ?: FirebaseApp.initializeApp(
            primaryApp.applicationContext,
            options,
            secondaryAppName
        ) ?: error("Failed to initialize secondary Firebase app.")

        val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)

        return try {
            val authResult = secondaryAuth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.uid ?: error("Missing new member uid")
        } finally {
            secondaryAuth.signOut()
        }
    }

    private fun DocumentSnapshot.toHouseholdMemberRow(): HouseholdMemberRow {
        return HouseholdMemberRow(
            uid = getString("uid").orEmpty().ifBlank { id },
            firstName = getString("firstName").orEmpty(),
            lastName = getString("lastName").orEmpty(),
            userType = getString("role").orEmpty(),
            address = getString("address").orEmpty(),
            city = getString("city").orEmpty(),
            state = getString("state").orEmpty(),
            zip = getString("zip").orEmpty(),
            email = getString("email").orEmpty()
        )
    }

    private fun DocumentSnapshot.toChoreRow(): ChoreRow {
        return ChoreRow(
            id = getString("id").orEmpty().ifBlank { id },
            title = getString("title").orEmpty(),
            description = getString("description").orEmpty(),
            dueDate = getString("dueDate").orEmpty(),
            rewardAmount = getString("rewardAmount").orEmpty(),
            assignedChildUid = getString("assignedChildUid").orEmpty(),
            assignedChildName = getString("assignedChildName").orEmpty(),
            createdByUid = getString("createdByUid").orEmpty(),
            createdByName = getString("createdByName").orEmpty(),
            createdByRole = getString("createdByRole").orEmpty(),
            householdId = getString("householdId").orEmpty(),
            status = getString("status").orEmpty().ifBlank { "OPEN" }
        )
    }
}

