package com.example.chorepalcowboysg6

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

class FirebaseRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()
) {

    suspend fun registerParent(
        firstName: String,
        lastName: String,
        username: String,
        dob: String,
        address: String,
        city: String,
        state: String,
        zip: String,
        email: String,
        password: String
    ): Result<Unit> {
        return runCatching {
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
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
                        "username" to username.trim(),
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
                        "username" to username.trim(),
                        "email" to email.trim(),
                        "role" to "PARENT",
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
        username: String,
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
            val payload = hashMapOf(
                "firstName" to firstName.trim(),
                "lastName" to lastName.trim(),
                "username" to username.trim(),
                "dob" to dob.trim(),
                "address" to address.trim(),
                "city" to city.trim(),
                "state" to state.trim(),
                "zip" to zip.trim(),
                "email" to email.trim(),
                "password" to password,
                "role" to role
            )

            functions
                .getHttpsCallable("createHouseholdMember")
                .call(payload)
                .await()
        }
    }
}