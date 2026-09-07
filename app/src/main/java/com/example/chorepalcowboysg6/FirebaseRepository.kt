package com.example.chorepalcowboysg6

import android.net.Uri
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await





class FirebaseRepository(

    private val auth: FirebaseAuth =
        FirebaseAuth.getInstance(),

    private val db: FirebaseFirestore =
        FirebaseFirestore.getInstance()

) {



    /**
    =========================================================
    FIREBASE STORAGE
    =========================================================
    Used for chore photo uploads.
     */

    private val storage =
        FirebaseStorage.getInstance()



    /**
    =========================================================
    FIREBASE FUNCTIONS
    =========================================================
    Used to securely call the AI analysis backend.
     */

    private val functions =
        FirebaseFunctions.getInstance()



    /**
    =========================================================
    REGISTER PARENT
    =========================================================
     */

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

            val authResult =
                auth
                    .createUserWithEmailAndPassword(
                        email.trim(),
                        password
                    )
                    .await()



            val uid =
                authResult
                    .user
                    ?.uid
                    ?: error("Missing uid")



            val householdRef =
                db
                    .collection("households")
                    .document()



            val householdId =
                householdRef.id



            db.runBatch { batch ->



                /**
                CREATE HOUSEHOLD
                 */

                batch.set(

                    householdRef,

                    mapOf(

                        "id" to
                                householdId,

                        "name" to
                                "${lastName.trim()} Household",

                        "ownerUid" to
                                uid,

                        "createdAt" to
                                FieldValue.serverTimestamp()
                    )
                )



                /**
                CREATE USER
                 */

                batch.set(

                    db
                        .collection("users")
                        .document(uid),

                    mapOf(

                        "uid" to
                                uid,

                        "firstName" to
                                firstName.trim(),

                        "lastName" to
                                lastName.trim(),

                        "dob" to
                                dob.trim(),

                        "address" to
                                address.trim(),

                        "city" to
                                city.trim(),

                        "state" to
                                state.trim(),

                        "zip" to
                                zip.trim(),

                        "email" to
                                email.trim(),

                        "role" to
                                "PARENT",

                        "householdId" to
                                householdId,

                        "active" to
                                true,

                        "createdAt" to
                                FieldValue.serverTimestamp()
                    )
                )



                /**
                ADD PARENT TO HOUSEHOLD MEMBERS
                 */

                batch.set(

                    db
                        .collection("households")
                        .document(householdId)
                        .collection("members")
                        .document(uid),

                    mapOf(

                        "uid" to
                                uid,

                        "firstName" to
                                firstName.trim(),

                        "lastName" to
                                lastName.trim(),

                        "dob" to
                                dob.trim(),

                        "address" to
                                address.trim(),

                        "city" to
                                city.trim(),

                        "state" to
                                state.trim(),

                        "zip" to
                                zip.trim(),

                        "email" to
                                email.trim(),

                        "role" to
                                "PARENT",

                        "householdId" to
                                householdId,

                        "createdBy" to
                                uid,

                        "createdAt" to
                                FieldValue.serverTimestamp()
                    )
                )

            }.await()
        }
    }



    /**
    =========================================================
    LOGIN
    =========================================================
     */

    suspend fun login(

        email: String,
        password: String

    ): Result<Unit> {

        return runCatching {

            auth
                .signInWithEmailAndPassword(
                    email.trim(),
                    password
                )
                .await()
        }
    }



    /**
    =========================================================
    LOGOUT
    =========================================================
     */

    fun logout() {

        auth.signOut()
    }



    /**
    =========================================================
    LOAD CURRENT USER
    =========================================================
     */

    suspend fun loadCurrentUserProfile():
            Result<Map<String, Any?>> {

        return runCatching {

            val uid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val snapshot =
                db
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()



            snapshot.data
                ?: emptyMap()
        }
    }



    /**
    =========================================================
    CREATE MEMBER ACCOUNT
    =========================================================
     */

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



            val normalizedRole =
                role
                    .trim()
                    .uppercase()



            if (
                normalizedRole != "CHILD" &&
                normalizedRole != "ADULT"
            ) {

                error(
                    "Only child or adult accounts can be created here."
                )
            }



            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val currentUserSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val currentRole =
                currentUserSnap
                    .getString("role")
                    .orEmpty()
                    .uppercase()



            if (
                currentRole != "PARENT" &&
                currentRole != "ADULT"
            ) {

                error(
                    "Only parent or adult accounts can create member accounts."
                )
            }



            val householdId =
                currentUserSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                error(
                    "Missing household id"
                )
            }



            val cleanEmail =
                email.trim()



            val existingUserQuery =
                db
                    .collection("users")
                    .whereEqualTo(
                        "email",
                        cleanEmail
                    )
                    .get()
                    .await()



            if (
                !existingUserQuery.isEmpty
            ) {

                error(
                    "An account with that email already exists."
                )
            }



            val newUid =
                createSecondaryAuthUser(

                    email =
                        cleanEmail,

                    password =
                        password
                )



            db.runBatch { batch ->



                /**
                CREATE USER DOCUMENT
                 */

                batch.set(

                    db
                        .collection("users")
                        .document(newUid),

                    mapOf(

                        "uid" to
                                newUid,

                        "firstName" to
                                firstName.trim(),

                        "lastName" to
                                lastName.trim(),

                        "dob" to
                                dob.trim(),

                        "address" to
                                address.trim(),

                        "city" to
                                city.trim(),

                        "state" to
                                state.trim(),

                        "zip" to
                                zip.trim(),

                        "email" to
                                cleanEmail,

                        "role" to
                                normalizedRole,

                        "householdId" to
                                householdId,

                        "active" to
                                true,

                        "createdBy" to
                                currentUid,

                        "createdAt" to
                                FieldValue.serverTimestamp()
                    )
                )



                /**
                CREATE HOUSEHOLD MEMBER DOCUMENT
                 */

                batch.set(

                    db
                        .collection("households")
                        .document(householdId)
                        .collection("members")
                        .document(newUid),

                    mapOf(

                        "uid" to
                                newUid,

                        "firstName" to
                                firstName.trim(),

                        "lastName" to
                                lastName.trim(),

                        "dob" to
                                dob.trim(),

                        "address" to
                                address.trim(),

                        "city" to
                                city.trim(),

                        "state" to
                                state.trim(),

                        "zip" to
                                zip.trim(),

                        "email" to
                                cleanEmail,

                        "role" to
                                normalizedRole,

                        "householdId" to
                                householdId,

                        "createdBy" to
                                currentUid,

                        "createdAt" to
                                FieldValue.serverTimestamp()
                    )
                )

            }.await()
        }
    }



    /**
    =========================================================
    LOAD HOUSEHOLD MEMBERS
    =========================================================
     */

    suspend fun loadHouseholdMembers():
            Result<List<HouseholdMemberRow>> {

        return runCatching {



            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val currentUserSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val householdId =
                currentUserSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                return@runCatching emptyList<HouseholdMemberRow>()
            }



            val membersSnap =
                db
                    .collection("households")
                    .document(householdId)
                    .collection("members")
                    .get()
                    .await()



            membersSnap
                .documents
                .map {

                    it.toHouseholdMemberRow()
                }
                .sortedWith(

                    compareBy<HouseholdMemberRow> {

                        when (
                            it.userType.uppercase()
                        ) {

                            "PARENT" -> 0

                            "ADULT" -> 1

                            "CHILD" -> 2

                            else -> 3
                        }

                    }.thenBy {

                        it.firstName.uppercase()

                    }.thenBy {

                        it.lastName.uppercase()
                    }
                )
        }
    }



    /**
    =========================================================
    LOAD MEMBER
    =========================================================
     */

    suspend fun loadMemberById(
        memberUid: String
    ): Result<HouseholdMemberRow> {

        return runCatching {



            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val currentUserSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val householdId =
                currentUserSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                error(
                    "Missing household id"
                )
            }



            val memberSnap =
                db
                    .collection("households")
                    .document(householdId)
                    .collection("members")
                    .document(memberUid)
                    .get()
                    .await()



            if (
                !memberSnap.exists()
            ) {

                error(
                    "Member not found"
                )
            }



            memberSnap
                .toHouseholdMemberRow()
        }
    }



    /**
    =========================================================
    UPDATE MEMBER
    =========================================================
     */

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



            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val currentUserSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val householdId =
                currentUserSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                error(
                    "Missing household id"
                )
            }



            val updates =
                mapOf(

                    "firstName" to
                            firstName.trim(),

                    "lastName" to
                            lastName.trim(),

                    "address" to
                            address.trim(),

                    "city" to
                            city.trim(),

                    "state" to
                            state.trim(),

                    "zip" to
                            zip.trim(),

                    "email" to
                            email.trim()
                )



            db
                .collection("users")
                .document(memberUid)
                .update(updates)
                .await()



            db
                .collection("households")
                .document(householdId)
                .collection("members")
                .document(memberUid)
                .update(updates)
                .await()
        }
    }



    /**
    =========================================================
    DELETE MEMBER
    =========================================================
     */

    suspend fun deleteMember(
        memberUid: String
    ): Result<Unit> {

        return runCatching {



            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            if (
                memberUid == currentUid
            ) {

                error(
                    "You cannot delete the currently logged-in account from here."
                )
            }



            val currentUserSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val householdId =
                currentUserSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                error(
                    "Missing household id"
                )
            }



            db
                .collection("households")
                .document(householdId)
                .collection("members")
                .document(memberUid)
                .delete()
                .await()



            db
                .collection("users")
                .document(memberUid)
                .delete()
                .await()
        }
    }



    /**
    =========================================================
    LOAD CHILDREN
    =========================================================
     */

    suspend fun loadHouseholdChildren():
            Result<List<ChildOption>> {

        return runCatching {



            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val currentUserSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val householdId =
                currentUserSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                return@runCatching emptyList<ChildOption>()
            }



            val membersSnap =
                db
                    .collection("households")
                    .document(householdId)
                    .collection("members")
                    .get()
                    .await()



            membersSnap
                .documents
                .map {

                    it.toHouseholdMemberRow()
                }
                .filter {

                    it.userType.equals(
                        "CHILD",
                        ignoreCase = true
                    )
                }
                .sortedBy {

                    "${it.firstName} ${it.lastName}"
                }
                .map {

                    ChildOption(

                        uid =
                            it.uid,

                        displayName =
                            "${it.firstName} ${it.lastName}"
                                .trim()
                    )
                }
        }
    }



    /**
    =========================================================
    CREATE CHORE
    =========================================================
     */

    suspend fun createChore(

        title: String,
        description: String,
        dueDate: String,
        rewardAmount: String,
        assignedChildUid: String,
        assignedChildName: String

    ): Result<Unit> {

        return runCatching {



            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val userSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val role =
                userSnap
                    .getString("role")
                    .orEmpty()
                    .uppercase()



            if (
                role != "PARENT" &&
                role != "ADULT"
            ) {

                error(
                    "Only parent or adult accounts can create chores."
                )
            }



            val householdId =
                userSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                error(
                    "Missing household id"
                )
            }



            val createdByName =
                "${
                    userSnap
                        .getString("firstName")
                        .orEmpty()
                } ${
                    userSnap
                        .getString("lastName")
                        .orEmpty()
                }"
                    .trim()



            val choreRef =
                db
                    .collection("households")
                    .document(householdId)
                    .collection("chores")
                    .document()



            choreRef
                .set(

                    mapOf(

                        "id" to
                                choreRef.id,

                        "title" to
                                title.trim(),

                        "description" to
                                description.trim(),

                        "dueDate" to
                                dueDate.trim(),

                        "rewardAmount" to
                                rewardAmount.trim(),

                        "assignedChildUid" to
                                assignedChildUid,

                        "assignedChildName" to
                                assignedChildName.trim(),

                        "createdByUid" to
                                currentUid,

                        "createdByName" to
                                createdByName,

                        "createdByRole" to
                                role,

                        "householdId" to
                                householdId,

                        "status" to
                                "OPEN",

                        /**
                        AI PHOTO FIELDS
                         */

                        "photoUrl" to
                                "",

                        "photoStoragePath" to
                                "",

                        "aiStatus" to
                                "",

                        "aiConfidence" to
                                0.0,

                        "aiAnalysis" to
                                "",

                        "aiIssues" to
                                emptyList<String>(),

                        "createdAt" to
                                FieldValue.serverTimestamp(),

                        "updatedAt" to
                                FieldValue.serverTimestamp()
                    )
                )
                .await()
        }
    }



    /**
    =========================================================
    LOAD CHORES
    =========================================================
     */

    suspend fun loadChores():
            Result<List<ChoreRow>> {

        return runCatching {



            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val userSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val householdId =
                userSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            val role =
                userSnap
                    .getString("role")
                    .orEmpty()
                    .uppercase()



            if (
                householdId.isBlank()
            ) {

                return@runCatching emptyList<ChoreRow>()
            }



            val choresSnap =
                db
                    .collection("households")
                    .document(householdId)
                    .collection("chores")
                    .get()
                    .await()



            val statusOrder =
                mapOf(

                    "REJECTED" to 0,

                    "OPEN" to 1,

                    "IN PROGRESS" to 2,

                    "COMPLETED" to 3,

                    "APPROVED" to 4
                )



            val loadedChores =
                choresSnap
                    .documents
                    .map {

                        it.toChoreRow()
                    }
                    .sortedWith(

                        compareBy<ChoreRow> {

                            statusOrder[
                                it.status.uppercase()
                            ] ?: 99

                        }.thenBy {

                            it.dueDate

                        }.thenBy {

                            it.title.uppercase()
                        }
                    )



            if (
                role == "CHILD"
            ) {

                loadedChores.filter {

                    it.assignedChildUid ==
                            currentUid
                }

            } else {

                loadedChores
            }
        }
    }



    /**
    =========================================================
    LOAD ONE CHORE
    =========================================================
     */

    suspend fun loadChoreById(
        choreId: String
    ): Result<ChoreRow> {

        return runCatching {



            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val userSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val householdId =
                userSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                error(
                    "Missing household id"
                )
            }



            val choreSnap =
                db
                    .collection("households")
                    .document(householdId)
                    .collection("chores")
                    .document(choreId)
                    .get()
                    .await()



            if (
                !choreSnap.exists()
            ) {

                error(
                    "Chore not found"
                )
            }



            choreSnap
                .toChoreRow()
        }
    }



    /**
    =========================================================
    UPDATE CHORE
    =========================================================
     */

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



            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val userSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val role =
                userSnap
                    .getString("role")
                    .orEmpty()
                    .uppercase()



            if (
                role != "PARENT" &&
                role != "ADULT"
            ) {

                error(
                    "Only parent or adult accounts can edit chores."
                )
            }



            val householdId =
                userSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                error(
                    "Missing household id"
                )
            }



            db
                .collection("households")
                .document(householdId)
                .collection("chores")
                .document(choreId)
                .update(

                    mapOf(

                        "title" to
                                title.trim(),

                        "description" to
                                description.trim(),

                        "dueDate" to
                                dueDate.trim(),

                        "rewardAmount" to
                                rewardAmount.trim(),

                        "assignedChildUid" to
                                assignedChildUid,

                        "assignedChildName" to
                                assignedChildName.trim(),

                        "updatedAt" to
                                FieldValue.serverTimestamp()
                    )
                )
                .await()
        }
    }



    /**
    =========================================================
    UPDATE CHORE STATUS
    =========================================================
     */

    suspend fun updateChoreStatus(

        choreId: String,
        newStatus: String

    ): Result<Unit> {

        return runCatching {



            val normalizedStatus =
                newStatus
                    .trim()
                    .uppercase()



            if (
                normalizedStatus !=
                "IN PROGRESS" &&
                normalizedStatus !=
                "COMPLETED"
            ) {

                error(
                    "Invalid status"
                )
            }



            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val userSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val householdId =
                userSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            val role =
                userSnap
                    .getString("role")
                    .orEmpty()
                    .uppercase()



            if (
                householdId.isBlank()
            ) {

                error(
                    "Missing household id"
                )
            }



            if (
                role != "CHILD"
            ) {

                error(
                    "Only child accounts can update chore status here."
                )
            }



            val choreRef =
                db
                    .collection("households")
                    .document(householdId)
                    .collection("chores")
                    .document(choreId)



            val choreSnap =
                choreRef
                    .get()
                    .await()



            if (
                !choreSnap.exists()
            ) {

                error(
                    "Chore not found"
                )
            }



            val assignedChildUid =
                choreSnap
                    .getString(
                        "assignedChildUid"
                    )
                    .orEmpty()



            if (
                assignedChildUid !=
                currentUid
            ) {

                error(
                    "You can only update chores assigned to you."
                )
            }



            choreRef
                .update(

                    mapOf(

                        "status" to
                                normalizedStatus,

                        "updatedAt" to
                                FieldValue.serverTimestamp()
                    )
                )
                .await()
        }
    }



    /**
    =========================================================
    AI PHOTO CHORE SUBMISSION
    =========================================================

    Child:
    selects photo
    ↓
    upload to Firebase Storage
    ↓
    save photo URL on chore
    ↓
    set status COMPLETED
    ↓
    call analyzeChorePhoto Cloud Function
    ↓
    AI result saved to chore
     */

    suspend fun submitChorePhoto(

        choreId: String,
        photoUri: Uri

    ): Result<Unit> {

        return runCatching {



            /**
            CURRENT CHILD
             */

            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val userSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val role =
                userSnap
                    .getString("role")
                    .orEmpty()
                    .uppercase()



            if (
                role != "CHILD"
            ) {

                error(
                    "Only child accounts can submit chore photos."
                )
            }



            /**
            HOUSEHOLD
             */

            val householdId =
                userSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                error(
                    "Missing household id"
                )
            }



            /**
            LOAD CHORE
             */

            val choreRef =
                db
                    .collection("households")
                    .document(householdId)
                    .collection("chores")
                    .document(choreId)



            val choreSnap =
                choreRef
                    .get()
                    .await()



            if (
                !choreSnap.exists()
            ) {

                error(
                    "Chore not found"
                )
            }



            /**
            VERIFY CHORE BELONGS TO CHILD
             */

            val assignedChildUid =
                choreSnap
                    .getString(
                        "assignedChildUid"
                    )
                    .orEmpty()



            if (
                assignedChildUid !=
                currentUid
            ) {

                error(
                    "You can only submit photos for chores assigned to you."
                )
            }



            /**
            CREATE STORAGE LOCATION
             */

            val storagePath =

                "chore-submissions/" +
                        "$householdId/" +
                        "$choreId/" +
                        "${currentUid}_" +
                        "${System.currentTimeMillis()}.jpg"



            val storageRef =
                storage
                    .reference
                    .child(
                        storagePath
                    )



            /**
            UPLOAD IMAGE
             */

            storageRef
                .putFile(
                    photoUri
                )
                .await()



            /**
            GET DOWNLOAD URL
             */

            val photoUrl =
                storageRef
                    .downloadUrl
                    .await()
                    .toString()



            /**
            SAVE IMAGE INFORMATION
            AND PREPARE AI ANALYSIS
             */

            choreRef
                .update(

                    mapOf(

                        "photoUrl" to
                                photoUrl,

                        "photoStoragePath" to
                                storagePath,

                        "aiStatus" to
                                "ANALYZING",

                        "aiComplete" to
                                null,

                        "aiConfidence" to
                                0.0,

                        "aiAnalysis" to
                                "",

                        "aiIssues" to
                                emptyList<String>(),

                        /**
                        Parent approval screen expects
                        submitted chores to be COMPLETED.
                         */

                        "status" to
                                "COMPLETED",

                        "photoSubmittedAt" to
                                FieldValue.serverTimestamp(),

                        "updatedAt" to
                                FieldValue.serverTimestamp()
                    )
                )
                .await()



            /**
            CALL CLOUD FUNCTION
             */

            val data =
                hashMapOf<String, Any>(

                    "choreId" to
                            choreId
                )



            functions
                .getHttpsCallable(
                    "analyzeChorePhoto"
                )
                .call(
                    data
                )
                .await()
        }
    }



    /**
    =========================================================
    AI INSIGHTS
    =========================================================

    ChorePal calculates all statistics using Firestore.
    Gemini receives only the calculated statistics and
    generates the household, child, and comparison insights.
     */

    suspend fun loadAIInsights(
        period: InsightTimePeriod
    ): Result<AIInsightsReport> {

        return runCatching {



            /**
            =====================================================
            CURRENT USER
            =====================================================
             */

            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val userSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val role =
                userSnap
                    .getString(
                        "role"
                    )
                    .orEmpty()
                    .uppercase()



            if (
                role != "PARENT"
            ) {

                error(
                    "Only parent accounts can access AI Insights."
                )
            }



            val householdId =
                userSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                error(
                    "Missing household id"
                )
            }



            /**
            =====================================================
            LOAD CHILDREN
            =====================================================
             */

            val membersSnap =
                db
                    .collection(
                        "households"
                    )
                    .document(
                        householdId
                    )
                    .collection(
                        "members"
                    )
                    .get()
                    .await()



            val children =
                membersSnap
                    .documents
                    .filter { document ->

                        document
                            .getString(
                                "role"
                            )
                            .orEmpty()
                            .equals(
                                "CHILD",
                                ignoreCase = true
                            )
                    }
                    .map { document ->

                        val firstName =
                            document
                                .getString(
                                    "firstName"
                                )
                                .orEmpty()

                        val lastName =
                            document
                                .getString(
                                    "lastName"
                                )
                                .orEmpty()



                        ChildOption(

                            uid =
                                document
                                    .getString(
                                        "uid"
                                    )
                                    .orEmpty()
                                    .ifBlank {

                                        document.id
                                    },

                            displayName =
                                "$firstName $lastName"
                                    .trim()
                        )
                    }
                    .sortedBy {

                        it.displayName
                            .uppercase()
                    }



            /**
            =====================================================
            LOAD CHORES
            =====================================================
             */

            val choresSnap =
                db
                    .collection(
                        "households"
                    )
                    .document(
                        householdId
                    )
                    .collection(
                        "chores"
                    )
                    .get()
                    .await()



            /**
            =====================================================
            DATE FILTER
            =====================================================
             */

            val now =
                System.currentTimeMillis()



            val cutoff =
                period
                    .days
                    ?.let { days ->

                        now -
                                (
                                        days.toLong() *
                                                24L *
                                                60L *
                                                60L *
                                                1000L
                                        )
                    }



            val filteredDocs =
                choresSnap
                    .documents
                    .filter { document ->

                        if (
                            cutoff == null
                        ) {

                            true

                        } else {

                            val createdMillis =
                                document
                                    .getTimestamp(
                                        "createdAt"
                                    )
                                    ?.toDate()
                                    ?.time
                                    ?: 0L



                            createdMillis >=
                                    cutoff
                        }
                    }



            /**
            =====================================================
            CALCULATE CHILD STATISTICS
            =====================================================
             */

            val childStats =
                children
                    .map { child ->



                        val childChores =
                            filteredDocs
                                .filter { document ->

                                    document
                                        .getString(
                                            "assignedChildUid"
                                        ) ==
                                            child.uid
                                }



                        val assigned =
                            childChores.size



                        val completed =
                            childChores
                                .count { document ->

                                    val status =
                                        document
                                            .getString(
                                                "status"
                                            )
                                            .orEmpty()
                                            .uppercase()



                                    status ==
                                            "COMPLETED" ||
                                            status ==
                                            "APPROVED"
                                }



                        val approved =
                            childChores
                                .count { document ->

                                    document
                                        .getString(
                                            "status"
                                        )
                                        .orEmpty()
                                        .uppercase() ==
                                            "APPROVED"
                                }



                        val rejected =
                            childChores
                                .count { document ->

                                    document
                                        .getString(
                                            "status"
                                        )
                                        .orEmpty()
                                        .uppercase() ==
                                            "REJECTED"
                                }



                        val completionRate =
                            if (
                                assigned > 0
                            ) {

                                (
                                        completed.toDouble() /
                                                assigned.toDouble()
                                        ) *
                                        100.0

                            } else {

                                0.0
                            }



                        val approvalRate =
                            if (
                                completed > 0
                            ) {

                                (
                                        approved.toDouble() /
                                                completed.toDouble()
                                        ) *
                                        100.0

                            } else {

                                0.0
                            }



                        /**
                        Average completion time.

                        Prefer photoSubmittedAt.
                        Fall back to updatedAt.
                         */

                        val completionTimes =
                            childChores
                                .mapNotNull { document ->



                                    val status =
                                        document
                                            .getString(
                                                "status"
                                            )
                                            .orEmpty()
                                            .uppercase()



                                    if (
                                        status !=
                                        "COMPLETED" &&
                                        status !=
                                        "APPROVED"
                                    ) {

                                        return@mapNotNull null
                                    }



                                    val created =
                                        document
                                            .getTimestamp(
                                                "createdAt"
                                            )
                                            ?.toDate()
                                            ?.time
                                            ?: return@mapNotNull null



                                    val finished =
                                        document
                                            .getTimestamp(
                                                "photoSubmittedAt"
                                            )
                                            ?.toDate()
                                            ?.time
                                            ?: document
                                                .getTimestamp(
                                                    "updatedAt"
                                                )
                                                ?.toDate()
                                                ?.time
                                            ?: return@mapNotNull null



                                    if (
                                        finished <
                                        created
                                    ) {

                                        null

                                    } else {

                                        finished -
                                                created
                                    }
                                }



                        val averageHours =
                            if (
                                completionTimes
                                    .isNotEmpty()
                            ) {

                                completionTimes
                                    .average() /
                                        (
                                                1000.0 *
                                                        60.0 *
                                                        60.0
                                                )

                            } else {

                                0.0
                            }



                        ChildPerformanceStats(

                            childUid =
                                child.uid,

                            childName =
                                child.displayName,

                            assigned =
                                assigned,

                            completed =
                                completed,

                            approved =
                                approved,

                            rejected =
                                rejected,

                            completionRate =
                                completionRate,

                            approvalRate =
                                approvalRate,

                            averageCompletionHours =
                                averageHours
                        )
                    }



            /**
            =====================================================
            HOUSEHOLD STATISTICS
            =====================================================
             */

            val householdAssigned =
                childStats
                    .sumOf {

                        it.assigned
                    }



            val householdCompleted =
                childStats
                    .sumOf {

                        it.completed
                    }



            val householdApproved =
                childStats
                    .sumOf {

                        it.approved
                    }



            val householdRejected =
                childStats
                    .sumOf {

                        it.rejected
                    }



            val householdCompletionRate =
                if (
                    householdAssigned > 0
                ) {

                    (
                            householdCompleted.toDouble() /
                                    householdAssigned.toDouble()
                            ) *
                            100.0

                } else {

                    0.0
                }



            val householdStats =
                HouseholdPerformanceStats(

                    assigned =
                        householdAssigned,

                    completed =
                        householdCompleted,

                    approved =
                        householdApproved,

                    rejected =
                        householdRejected,

                    completionRate =
                        householdCompletionRate
                )



            /**
            =====================================================
            PREPARE CHILD DATA FOR AI
            =====================================================
             */

            val childData =
                childStats
                    .map { stats ->

                        hashMapOf<String, Any>(

                            "childUid" to
                                    stats.childUid,

                            "childName" to
                                    stats.childName,

                            "assigned" to
                                    stats.assigned,

                            "completed" to
                                    stats.completed,

                            "approved" to
                                    stats.approved,

                            "rejected" to
                                    stats.rejected,

                            "completionRate" to
                                    stats.completionRate,

                            "approvalRate" to
                                    stats.approvalRate,

                            "averageCompletionHours" to
                                    stats.averageCompletionHours
                        )
                    }



            /**
            =====================================================
            CLOUD FUNCTION REQUEST
            =====================================================
             */

            val data =
                hashMapOf<String, Any>(

                    "period" to
                            period.label,

                    "household" to
                            hashMapOf<String, Any>(

                                "assigned" to
                                        householdAssigned,

                                "completed" to
                                        householdCompleted,

                                "approved" to
                                        householdApproved,

                                "rejected" to
                                        householdRejected,

                                "completionRate" to
                                        householdCompletionRate
                            ),

                    "children" to
                            childData
                )



            /**
            =====================================================
            CALL CLOUD FUNCTION
            =====================================================
             */

            val callableResult =
                functions
                    .getHttpsCallable(
                        "generatePerformanceInsights"
                    )
                    .call(
                        data
                    )
                    .await()



            /**
            =====================================================
            READ RESPONSE
            =====================================================
             */

            @Suppress(
                "UNCHECKED_CAST"
            )

            val resultData =
                callableResult
                    .data as?
                        Map<String, Any?>
                    ?: emptyMap()



            /**
            HOUSEHOLD INSIGHT
             */

            val householdInsight =
                resultData[
                    "householdInsight"
                ]
                    ?.toString()
                    .orEmpty()



            val householdRecommendations =
                (
                        resultData[
                            "householdRecommendations"
                        ] as?
                                List<*>
                        )
                    ?.mapNotNull {

                        it?.toString()
                    }
                    ?: emptyList()



            /**
            CHILD INSIGHTS
             */

            val rawChildInsights =
                resultData[
                    "childInsights"
                ] as?
                        List<*>
                    ?: emptyList<Any>()



            val childInsights =
                rawChildInsights
                    .mapNotNull { item ->



                        val map =
                            item as?
                                    Map<*, *>
                                ?: return@mapNotNull null



                        ChildAIInsight(

                            childUid =
                                map[
                                    "childUid"
                                ]
                                    ?.toString()
                                    .orEmpty(),

                            summary =
                                map[
                                    "summary"
                                ]
                                    ?.toString()
                                    .orEmpty(),

                            strengths =
                                (
                                        map[
                                            "strengths"
                                        ] as?
                                                List<*>
                                        )
                                    ?.mapNotNull {

                                        it?.toString()
                                    }
                                    ?: emptyList(),

                            improvementAreas =
                                (
                                        map[
                                            "improvementAreas"
                                        ] as?
                                                List<*>
                                        )
                                    ?.mapNotNull {

                                        it?.toString()
                                    }
                                    ?: emptyList(),

                            recommendations =
                                (
                                        map[
                                            "recommendations"
                                        ] as?
                                                List<*>
                                        )
                                    ?.mapNotNull {

                                        it?.toString()
                                    }
                                    ?: emptyList()
                        )
                    }



            /**
            COMPARISON
             */

            val comparisonInsight =
                resultData[
                    "comparisonInsight"
                ]
                    ?.toString()
                    .orEmpty()



            val comparisonRecommendations =
                (
                        resultData[
                            "comparisonRecommendations"
                        ] as?
                                List<*>
                        )
                    ?.mapNotNull {

                        it?.toString()
                    }
                    ?: emptyList()



            /**
            =====================================================
            FINAL REPORT
            =====================================================
             */

            AIInsightsReport(

                householdStats =
                    householdStats,

                childStats =
                    childStats,

                householdInsight =
                    householdInsight,

                householdRecommendations =
                    householdRecommendations,

                childInsights =
                    childInsights,

                comparisonInsight =
                    comparisonInsight,

                comparisonRecommendations =
                    comparisonRecommendations
            )
        }
    }



    /**
    =========================================================
    APPROVE CHORE
    =========================================================
     */

    suspend fun approveChore(
        choreId: String
    ): Result<Unit> {

        return runCatching {

            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val userSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val role =
                userSnap
                    .getString("role")
                    .orEmpty()
                    .uppercase()



            if (
                role != "PARENT" &&
                role != "ADULT"
            ) {

                error(
                    "Only parent or adult accounts can approve chores."
                )
            }



            val householdId =
                userSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                error(
                    "Missing household id"
                )
            }



            val choreRef =
                db
                    .collection("households")
                    .document(householdId)
                    .collection("chores")
                    .document(choreId)



            val choreSnap =
                choreRef
                    .get()
                    .await()



            if (
                !choreSnap.exists()
            ) {

                error(
                    "Chore not found"
                )
            }



            choreRef
                .update(

                    mapOf(

                        "status" to
                                "APPROVED",

                        "approvedByUid" to
                                currentUid,

                        "approvedAt" to
                                FieldValue.serverTimestamp(),

                        "updatedAt" to
                                FieldValue.serverTimestamp()
                    )
                )
                .await()
        }
    }



    /**
    =========================================================
    REJECT CHORE
    =========================================================
     */

    suspend fun rejectChore(
        choreId: String
    ): Result<Unit> {

        return runCatching {

            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val userSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val role =
                userSnap
                    .getString("role")
                    .orEmpty()
                    .uppercase()



            if (
                role != "PARENT" &&
                role != "ADULT"
            ) {

                error(
                    "Only parent or adult accounts can reject chores."
                )
            }



            val householdId =
                userSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                error(
                    "Missing household id"
                )
            }



            val choreRef =
                db
                    .collection("households")
                    .document(householdId)
                    .collection("chores")
                    .document(choreId)



            val choreSnap =
                choreRef
                    .get()
                    .await()



            if (
                !choreSnap.exists()
            ) {

                error(
                    "Chore not found"
                )
            }



            choreRef
                .update(

                    mapOf(

                        "status" to
                                "REJECTED",

                        "rejectedByUid" to
                                currentUid,

                        "rejectedAt" to
                                FieldValue.serverTimestamp(),

                        "updatedAt" to
                                FieldValue.serverTimestamp()
                    )
                )
                .await()
        }
    }



    /**
    =========================================================
    DELETE CHORE
    =========================================================
     */

    suspend fun deleteChore(
        choreId: String
    ): Result<Unit> {

        return runCatching {



            val currentUid =
                auth
                    .currentUser
                    ?.uid
                    ?: error(
                        "No logged-in user"
                    )



            val userSnap =
                db
                    .collection("users")
                    .document(currentUid)
                    .get()
                    .await()



            val role =
                userSnap
                    .getString("role")
                    .orEmpty()
                    .uppercase()



            if (
                role != "PARENT" &&
                role != "ADULT"
            ) {

                error(
                    "Only parent or adult accounts can delete chores."
                )
            }



            val householdId =
                userSnap
                    .getString(
                        "householdId"
                    )
                    .orEmpty()



            if (
                householdId.isBlank()
            ) {

                error(
                    "Missing household id"
                )
            }



            db
                .collection("households")
                .document(householdId)
                .collection("chores")
                .document(choreId)
                .delete()
                .await()
        }
    }



    /**
    =========================================================
    CREATE SECONDARY FIREBASE AUTH USER
    =========================================================

    This prevents the parent from being logged out when
    creating a child or adult account.
     */

    private suspend fun createSecondaryAuthUser(

        email: String,
        password: String

    ): String {



        val primaryApp =
            FirebaseApp.getInstance()



        val options:
                FirebaseOptions =
            primaryApp.options



        val secondaryAppName =
            "memberCreatorApp"



        val existingSecondaryApp =
            FirebaseApp
                .getApps(
                    primaryApp.applicationContext
                )
                .firstOrNull {

                    it.name ==
                            secondaryAppName
                }



        val secondaryApp =

            existingSecondaryApp
                ?: FirebaseApp.initializeApp(

                    primaryApp.applicationContext,

                    options,

                    secondaryAppName
                )
                ?: error(
                    "Failed to initialize secondary Firebase app."
                )



        val secondaryAuth =
            FirebaseAuth
                .getInstance(
                    secondaryApp
                )



        return try {



            val authResult =

                secondaryAuth
                    .createUserWithEmailAndPassword(
                        email,
                        password
                    )
                    .await()



            authResult
                .user
                ?.uid
                ?: error(
                    "Missing new member uid"
                )



        } finally {



            secondaryAuth
                .signOut()
        }
    }



    /**
    =========================================================
    HOUSEHOLD MEMBER MAPPER
    =========================================================
     */

    private fun DocumentSnapshot
            .toHouseholdMemberRow():
            HouseholdMemberRow {

        return HouseholdMemberRow(

            uid =
                getString("uid")
                    .orEmpty()
                    .ifBlank {

                        id
                    },

            firstName =
                getString(
                    "firstName"
                )
                    .orEmpty(),

            lastName =
                getString(
                    "lastName"
                )
                    .orEmpty(),

            userType =
                getString(
                    "role"
                )
                    .orEmpty(),

            address =
                getString(
                    "address"
                )
                    .orEmpty(),

            city =
                getString(
                    "city"
                )
                    .orEmpty(),

            state =
                getString(
                    "state"
                )
                    .orEmpty(),

            zip =
                getString(
                    "zip"
                )
                    .orEmpty(),

            email =
                getString(
                    "email"
                )
                    .orEmpty()
        )
    }



    /**
    =========================================================
    CHORE MAPPER
    =========================================================

    Loads normal chore fields AND
    AI photo verification fields.
     */

    private fun DocumentSnapshot
            .toChoreRow():
            ChoreRow {

        return ChoreRow(

            id =
                getString("id")
                    .orEmpty()
                    .ifBlank {

                        id
                    },

            title =
                getString(
                    "title"
                )
                    .orEmpty(),

            description =
                getString(
                    "description"
                )
                    .orEmpty(),

            dueDate =
                getString(
                    "dueDate"
                )
                    .orEmpty(),

            rewardAmount =
                getString(
                    "rewardAmount"
                )
                    .orEmpty(),

            assignedChildUid =
                getString(
                    "assignedChildUid"
                )
                    .orEmpty(),

            assignedChildName =
                getString(
                    "assignedChildName"
                )
                    .orEmpty(),

            createdByUid =
                getString(
                    "createdByUid"
                )
                    .orEmpty(),

            createdByName =
                getString(
                    "createdByName"
                )
                    .orEmpty(),

            createdByRole =
                getString(
                    "createdByRole"
                )
                    .orEmpty(),

            householdId =
                getString(
                    "householdId"
                )
                    .orEmpty(),

            status =
                getString(
                    "status"
                )
                    .orEmpty()
                    .ifBlank {

                        "OPEN"
                    },



            /**
            =================================================
            AI PHOTO VERIFICATION FIELDS
            =================================================
             */

            photoUrl =
                getString(
                    "photoUrl"
                )
                    .orEmpty(),

            photoStoragePath =
                getString(
                    "photoStoragePath"
                )
                    .orEmpty(),

            aiComplete =
                getBoolean(
                    "aiComplete"
                ),

            aiConfidence =
                getDouble(
                    "aiConfidence"
                )
                    ?: 0.0,

            aiAnalysis =
                getString(
                    "aiAnalysis"
                )
                    .orEmpty(),

            aiIssues =
                (
                        get(
                            "aiIssues"
                        )
                                as? List<*>
                        )
                    ?.mapNotNull {

                        it?.toString()
                    }
                    ?: emptyList(),

            aiStatus =
                getString(
                    "aiStatus"
                )
                    .orEmpty()
        )
    }
}

