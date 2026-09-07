package com.example.chorepalcowboysg6

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EmailNotificationService {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /*
     -------------------------------------------------------
     BASIC EMAIL QUEUE
     -------------------------------------------------------

     Firebase Trigger Email watches the "mail" collection.

     When this document is added:

     mail
       └── generatedDocumentId
            ├── to
            └── message
                 ├── subject
                 ├── text
                 └── html

     Firebase's email extension sends the actual email.
     */

    private suspend fun queueEmail(
        recipients: List<String>,
        subject: String,
        text: String,
        html: String
    ) {

        val cleanRecipients =
            recipients
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .distinct()

        if (cleanRecipients.isEmpty()) {
            return
        }

        val emailDocument = hashMapOf(
            "to" to cleanRecipients,

            "message" to hashMapOf(
                "subject" to subject,
                "text" to text,
                "html" to html
            ),

            "createdAt" to FieldValue.serverTimestamp()
        )

        db.collection("mail")
            .add(emailDocument)
            .await()
    }


    /*
     -------------------------------------------------------
     GET USER
     -------------------------------------------------------
     */

    private suspend fun loadUser(
        uid: String
    ): Map<String, Any?> {

        if (uid.isBlank()) {
            return emptyMap()
        }

        val snapshot =
            db.collection("users")
                .document(uid)
                .get()
                .await()

        return snapshot.data ?: emptyMap()
    }


    /*
     -------------------------------------------------------
     GET CHORE
     -------------------------------------------------------
     */

    private suspend fun loadChore(
        choreId: String
    ): Map<String, Any?> {

        if (choreId.isBlank()) {
            return emptyMap()
        }

        val currentUid =
            auth.currentUser?.uid
                ?: error("No logged-in user.")

        val userSnapshot =
            db.collection("users")
                .document(currentUid)
                .get()
                .await()

        val householdId =
            userSnapshot
                .getString("householdId")
                ?: error("Missing householdId.")

        val snapshot =
            db.collection("households")
                .document(householdId)
                .collection("chores")
                .document(choreId)
                .get()
                .await()

        if (!snapshot.exists()) {
            error("Chore not found: $choreId")
        }

        return snapshot.data
            ?: emptyMap()
    }



    /*
     -------------------------------------------------------
     GET CHILD EMAIL
     -------------------------------------------------------
     */

    private suspend fun getChildEmail(
        childUid: String
    ): String {

        val child =
            loadUser(childUid)

        return child["email"]
            ?.toString()
            .orEmpty()
            .trim()
    }


    /*
     -------------------------------------------------------
     GET CHILD NAME
     -------------------------------------------------------
     */

    private suspend fun getChildName(
        childUid: String
    ): String {

        val child =
            loadUser(childUid)

        return child["firstName"]
            ?.toString()
            .orEmpty()
            .ifBlank { "there" }
    }


    /*
     -------------------------------------------------------
     GET PARENT EMAILS FOR HOUSEHOLD
     -------------------------------------------------------

     This intentionally returns ALL PARENT accounts in the
     household.

     If there is only one parent, only that parent receives it.
     */

    private suspend fun getParentEmailsForHousehold(
        householdId: String
    ): List<String> {

        if (householdId.isBlank()) {
            return emptyList()
        }

        val snapshot =
            db.collection("users")
                .whereEqualTo(
                    "householdId",
                    householdId
                )
                .whereEqualTo(
                    "role",
                    "PARENT"
                )
                .get()
                .await()

        return snapshot.documents
            .mapNotNull { document ->
                document.getString("email")
            }
            .map {
                it.trim()
            }
            .filter {
                it.isNotBlank()
            }
            .distinct()
    }


    /*
     =======================================================
     1. NEW CHORE EMAIL
     =======================================================

     Parent creates chore
                ↓
     Child receives email
     */

    suspend fun notifyChildChoreCreated(
        childUid: String,
        choreTitle: String,
        dueDate: String,
        rewardAmount: String
    ): Result<Unit> {

        return runCatching {

            val email =
                getChildEmail(childUid)

            if (email.isBlank()) {
                error(
                    "Child does not have an email address."
                )
            }

            val childName =
                getChildName(childUid)

            queueEmail(
                recipients = listOf(email),

                subject =
                    "New Chore Assigned: $choreTitle",

                text = """
                    Hi $childName,

                    You have a new chore in ChorePal.

                    Chore: $choreTitle
                    Due Date: $dueDate
                    Reward: $rewardAmount

                    Open ChorePal to view your chore.

                    - ChorePal
                """.trimIndent(),

                html = """
                    <h2>New Chore Assigned!</h2>

                    <p>Hi $childName,</p>

                    <p>
                        You have a new chore waiting for you
                        in <b>ChorePal</b>.
                    </p>

                    <p>
                        <b>Chore:</b> $choreTitle
                        <br>
                        <b>Due Date:</b> $dueDate
                        <br>
                        <b>Reward:</b> $rewardAmount
                    </p>

                    <p>
                        Open ChorePal to view your chore.
                    </p>

                    <p>
                        - ChorePal
                    </p>
                """.trimIndent()
            )
        }
    }


    /*
     =======================================================
     2. CHORE APPROVED EMAIL
     =======================================================

     Parent approves chore
                ↓
     Child receives email
     */

    suspend fun notifyChildChoreApproved(
        choreId: String
    ): Result<Unit> {

        return runCatching {

            val chore =
                loadChore(choreId)

            val childUid =
                chore["assignedChildUid"]
                    ?.toString()
                    .orEmpty()

            val choreTitle =
                chore["title"]
                    ?.toString()
                    .orEmpty()
                    .ifBlank {
                        "Your chore"
                    }

            val rewardAmount =
                chore["rewardAmount"]
                    ?.toString()
                    .orEmpty()

            val email =
                getChildEmail(childUid)

            if (email.isBlank()) {
                error(
                    "Assigned child does not have an email."
                )
            }

            val childName =
                getChildName(childUid)

            queueEmail(
                recipients = listOf(email),

                subject =
                    "Chore Approved: $choreTitle",

                text = """
                    Hi $childName,

                    Great job!

                    Your chore "$choreTitle" was approved.

                    Reward: $rewardAmount

                    Keep up the great work!

                    - ChorePal
                """.trimIndent(),

                html = """
                    <h2>Great Job! ⭐</h2>

                    <p>Hi $childName,</p>

                    <p>
                        Your chore
                        <b>$choreTitle</b>
                        was approved!
                    </p>

                    <p>
                        <b>Reward:</b>
                        $rewardAmount
                    </p>

                    <p>
                        Keep up the great work!
                    </p>

                    <p>
                        - ChorePal
                    </p>
                """.trimIndent()
            )
        }
    }


    /*
     =======================================================
     3. CHORE REJECTED EMAIL
     =======================================================

     Parent rejects chore
                ↓
     Child receives email
     */

    suspend fun notifyChildChoreRejected(
        choreId: String
    ): Result<Unit> {

        return runCatching {

            val chore =
                loadChore(choreId)

            if (chore.isEmpty()) {
                error(
                    "Chore not found for ID: $choreId"
                )
            }

            val childUid =
                chore["assignedChildUid"]
                    ?.toString()
                    ?.trim()
                    .orEmpty()

            if (childUid.isBlank()) {
                error(
                    "Chore does not contain assignedChildUid."
                )
            }

            val choreTitle =
                chore["title"]
                    ?.toString()
                    .orEmpty()
                    .ifBlank {
                        "Your chore"
                    }

            val child =
                loadUser(childUid)

            if (child.isEmpty()) {
                error(
                    "Child user document not found for UID: $childUid"
                )
            }

            val email =
                child["email"]
                    ?.toString()
                    ?.trim()
                    .orEmpty()

            if (email.isBlank()) {
                error(
                    "Assigned child does not have an email."
                )
            }

            val childName =
                child["firstName"]
                    ?.toString()
                    .orEmpty()
                    .ifBlank {
                        "there"
                    }

            queueEmail(
                recipients = listOf(email),

                subject =
                    "Chore Needs Attention: $choreTitle",

                text = """
                Hi $childName,

                Your chore "$choreTitle" was not approved yet.

                Please review the chore and complete it again.

                When you finish, update the chore status in ChorePal.

                - ChorePal
            """.trimIndent(),

                html = """
                <h2>Your Chore Needs Another Try</h2>

                <p>Hi $childName,</p>

                <p>
                    Your chore
                    <b>$choreTitle</b>
                    was not approved yet.
                </p>

                <p>
                    Please review the chore and
                    complete it again.
                </p>

                <p>
                    When you finish, update the
                    chore status in ChorePal.
                </p>

                <p>- ChorePal</p>
            """.trimIndent()
            )
        }
    }



    /*
     =======================================================
     4. PARENT NOTIFICATION WHEN CHILD COMPLETES CHORE
     =======================================================

     IMPORTANT:

     This function sends an email EVERY TIME the child changes
     the chore to COMPLETED.

     There is intentionally NO check such as:

         if (previousStatus != COMPLETED)

     Therefore:

       Child completes chore
             ↓
       Parent notified

       Parent rejects chore
             ↓
       Child completes it again
             ↓
       Parent notified AGAIN

     This matches your requirement.
     */

    suspend fun notifyParentChoreCompleted(
        choreId: String
    ): Result<Unit> {

        return runCatching {

            val chore =
                loadChore(choreId)

            val childUid =
                chore["assignedChildUid"]
                    ?.toString()
                    .orEmpty()

            val choreTitle =
                chore["title"]
                    ?.toString()
                    .orEmpty()
                    .ifBlank {
                        "Chore"
                    }

            val child =
                loadUser(childUid)

            val childName =
                child["firstName"]
                    ?.toString()
                    .orEmpty()
                    .ifBlank {
                        chore["assignedChildName"]
                            ?.toString()
                            .orEmpty()
                            .ifBlank {
                                "Your child"
                            }
                    }

            /*
             Prefer the child's householdId.
             */

            var householdId =
                child["householdId"]
                    ?.toString()
                    .orEmpty()

            /*
             Fallback:
             use currently logged-in child's profile.
             */

            if (householdId.isBlank()) {

                val currentUid =
                    auth.currentUser
                        ?.uid
                        .orEmpty()

                val currentUser =
                    loadUser(currentUid)

                householdId =
                    currentUser["householdId"]
                        ?.toString()
                        .orEmpty()
            }

            if (householdId.isBlank()) {
                error(
                    "Unable to determine household."
                )
            }

            val parentEmails =
                getParentEmailsForHousehold(
                    householdId
                )

            if (parentEmails.isEmpty()) {
                error(
                    "No parent email found for household."
                )
            }

            queueEmail(
                recipients = parentEmails,

                subject =
                    "$childName completed a chore",

                text = """
                    Hello,

                    $childName has marked the following chore as completed:

                    $choreTitle

                    Please open ChorePal to review the chore.

                    - ChorePal
                """.trimIndent(),

                html = """
                    <h2>Chore Completed</h2>

                    <p>
                        <b>$childName</b>
                        has marked the following chore
                        as completed:
                    </p>

                    <p>
                        <b>$choreTitle</b>
                    </p>

                    <p>
                        Please open ChorePal to review
                        the chore.
                    </p>

                    <p>
                        - ChorePal
                    </p>
                """.trimIndent()
            )
        }
    }
}


