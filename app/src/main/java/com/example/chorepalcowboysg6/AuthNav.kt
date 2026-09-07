package com.example.chorepalcowboysg6

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch



object Routes {

    const val USER_TYPE = "user_type"

    const val LOGIN = "login"
    const val REGISTER = "register"

    const val PARENT_DASHBOARD = "parent_dashboard"
    const val ADULT_DASHBOARD = "adult_dashboard"
    const val CHILD_DASHBOARD = "child_dashboard"

    const val ADD_MEMBER = "add_member"
    const val ADD_CHILD_MEMBER = "add_child_member"
    const val ADD_ADULT_MEMBER = "add_adult_member"

    const val VIEW_HOUSEHOLD = "view_household"
    const val EDIT_MEMBER = "edit_member"

    const val CREATE_CHORE = "create_chore"
    const val VIEW_CHORES = "view_chores"
    const val EDIT_CHORES = "edit_chores"
    const val EDIT_CHORE = "edit_chore"

    const val UPDATE_CHORE_STATUS =
        "update_chore_status"

    const val CHORE_APPROVALS =
        "chore_approvals"

    const val REWARDS =
        "rewards"

    /*
    =========================================================
    AI INSIGHTS ROUTE
    =========================================================
    */
    const val AI_INSIGHTS =
        "ai_insights"

    /*
    =========================================================
    NEW AI PHOTO VERIFICATION ROUTE
    =========================================================
    */
    const val PHOTO_SUBMISSION =
        "photo_submission"
}



@Composable
fun AuthNav() {

    val navController =
        rememberNavController()

    val repository =
        remember {
            FirebaseRepository()
        }

    /*
    =========================================================
    EMAIL NOTIFICATION SERVICE
    =========================================================
    */

    val emailNotifications =
        remember {
            EmailNotificationService()
        }

    val scope =
        rememberCoroutineScope()



    /*
    =========================================================
    ERROR / SUCCESS STATE
    =========================================================
    */

    var loginError by remember {
        mutableStateOf<String?>(null)
    }

    var registerError by remember {
        mutableStateOf<String?>(null)
    }

    var memberError by remember {
        mutableStateOf<String?>(null)
    }

    var editMemberError by remember {
        mutableStateOf<String?>(null)
    }

    var choreError by remember {
        mutableStateOf<String?>(null)
    }

    var successMessage by remember {
        mutableStateOf<String?>(null)
    }



    /*
    =========================================================
    CURRENT USER
    =========================================================
    */

    var currentFirstName by remember {
        mutableStateOf("")
    }

    var currentRole by remember {
        mutableStateOf("")
    }

    var currentUserUid by remember {
        mutableStateOf("")
    }



    /*
    Determines whether login screen
    uses kid background.
    */

    var kidsMode by remember {
        mutableStateOf(false)
    }



    /*
    =========================================================
    DASHBOARD STATE
    =========================================================
    */

    var parentSelectedAction by remember {
        mutableStateOf(
            ParentAction.HOME
        )
    }

    var adultSelectedAction by remember {
        mutableStateOf(
            AdultAction.HOME
        )
    }

    var childSelectedAction by remember {
        mutableStateOf(
            ChildAction.HOME
        )
    }



    /*
    =========================================================
    DATA
    =========================================================
    */

    val householdMembers =
        remember {
            mutableStateListOf<HouseholdMemberRow>()
        }

    val childOptions =
        remember {
            mutableStateListOf<ChildOption>()
        }

    val chores =
        remember {
            mutableStateListOf<ChoreRow>()
        }



    /*
    =========================================================
    LOGOUT
    =========================================================
    */

    fun logoutAndGoToUserType() {

        repository.logout()

        loginError = null
        registerError = null
        memberError = null
        editMemberError = null
        choreError = null
        successMessage = null

        householdMembers.clear()
        childOptions.clear()
        chores.clear()

        currentFirstName = ""
        currentRole = ""
        currentUserUid = ""

        parentSelectedAction =
            ParentAction.HOME

        adultSelectedAction =
            AdultAction.HOME

        childSelectedAction =
            ChildAction.HOME

        kidsMode = false

        navController.navigate(
            Routes.USER_TYPE
        ) {
            popUpTo(0)
        }
    }



    /*
    =========================================================
    REFRESH HOUSEHOLD
    =========================================================
    */

    fun refreshHouseholdMembers(
        afterRefresh: (() -> Unit)? = null
    ) {

        scope.launch {

            val result =
                repository
                    .loadHouseholdMembers()

            if (result.isSuccess) {

                householdMembers.clear()

                householdMembers.addAll(
                    result.getOrDefault(
                        emptyList()
                    )
                )

                afterRefresh?.invoke()

            } else {

                memberError =
                    result
                        .exceptionOrNull()
                        ?.message
                        ?: "Failed to load household members."
            }
        }
    }



    /*
    =========================================================
    REFRESH CHILD OPTIONS
    =========================================================
    */

    fun refreshChildOptions(
        afterRefresh: (() -> Unit)? = null
    ) {

        scope.launch {

            val result =
                repository
                    .loadHouseholdChildren()

            if (result.isSuccess) {

                childOptions.clear()

                childOptions.addAll(
                    result.getOrDefault(
                        emptyList()
                    )
                )

                afterRefresh?.invoke()

            } else {

                choreError =
                    result
                        .exceptionOrNull()
                        ?.message
                        ?: "Failed to load children."
            }
        }
    }



    /*
    =========================================================
    REFRESH CHORES
    =========================================================
    */

    fun refreshChores(
        afterRefresh: (() -> Unit)? = null
    ) {

        scope.launch {

            val result =
                repository.loadChores()

            if (result.isSuccess) {

                chores.clear()

                chores.addAll(
                    result.getOrDefault(
                        emptyList()
                    )
                )

                afterRefresh?.invoke()

            } else {

                choreError =
                    result
                        .exceptionOrNull()
                        ?.message
                        ?: "Failed to load chores."
            }
        }
    }



    /*
    =========================================================
    ROUTE USER AFTER LOGIN
    =========================================================
    */

    suspend fun routeLoggedInUser() {

        val profileResult =
            repository
                .loadCurrentUserProfile()

        if (profileResult.isFailure) {

            loginError =
                profileResult
                    .exceptionOrNull()
                    ?.message
                    ?: "Unable to load profile."

            return
        }

        val profile =
            profileResult.getOrDefault(
                emptyMap()
            )

        val role =
            (profile["role"] as? String)
                .orEmpty()
                .uppercase()

        currentFirstName =
            (profile["firstName"] as? String)
                .orEmpty()

        currentRole =
            role

        currentUserUid =
            (profile["uid"] as? String)
                .orEmpty()



        when (role) {

            "PARENT" -> {

                parentSelectedAction =
                    ParentAction.HOME

                navController.navigate(
                    Routes.PARENT_DASHBOARD
                ) {
                    popUpTo(0)
                }
            }



            "ADULT" -> {

                adultSelectedAction =
                    AdultAction.HOME

                navController.navigate(
                    Routes.ADULT_DASHBOARD
                ) {
                    popUpTo(0)
                }
            }



            "CHILD" -> {

                childSelectedAction =
                    ChildAction.HOME

                navController.navigate(
                    Routes.CHILD_DASHBOARD
                ) {
                    popUpTo(0)
                }
            }



            else -> {

                loginError =
                    "Unknown user role."
            }
        }
    }



    /*
    =========================================================
    CURRENT ROUTE
    =========================================================
    */

    val backStackEntry by
    navController
        .currentBackStackEntryAsState()

    val currentRoute =
        backStackEntry
            ?.destination
            ?.route
            .orEmpty()



    /*
    =========================================================
    NAVIGATION
    =========================================================
    */

    NavHost(
        navController = navController,
        startDestination = Routes.USER_TYPE
    ) {



        /*
        =====================================================
        USER TYPE
        =====================================================
        */

        composable(
            Routes.USER_TYPE
        ) {

            UserTypeSelectionScreen(

                onKidSelected = {

                    kidsMode = true
                    loginError = null

                    navController.navigate(
                        Routes.LOGIN
                    )
                },

                onParentSelected = {

                    kidsMode = false
                    loginError = null

                    navController.navigate(
                        Routes.LOGIN
                    )
                }
            )
        }



        /*
        =====================================================
        LOGIN
        =====================================================
        */

        composable(
            Routes.LOGIN
        ) {

            LoginScreen(

                isKidsMode =
                    kidsMode,

                onLogin = {
                        email,
                        password ->

                    scope.launch {

                        loginError = null

                        val result =
                            repository.login(
                                email,
                                password
                            )

                        if (result.isSuccess) {

                            routeLoggedInUser()

                        } else {

                            loginError =
                                "Invalid username or password"
                        }
                    }
                },

                onForgotCredentials = {

                    loginError =
                        "Forgot credentials flow is not connected yet."
                },

                onRegister = {

                    kidsMode = false
                    loginError = null

                    navController.navigate(
                        Routes.REGISTER
                    )
                },

                onBack = {

                    loginError = null
                    kidsMode = false

                    navController
                        .popBackStack()
                },

                errorMessage =
                    loginError
            )
        }



        /*
        =====================================================
        REGISTER PARENT
        =====================================================
        */

        composable(
            Routes.REGISTER
        ) {

            RegisterScreen(

                onRegister = {
                        firstName,
                        lastName,
                        dob,
                        address,
                        city,
                        state,
                        zip,
                        email,
                        password ->

                    scope.launch {

                        registerError = null

                        val result =
                            repository
                                .registerParent(

                                    firstName =
                                        firstName,

                                    lastName =
                                        lastName,

                                    dob =
                                        dob,

                                    address =
                                        address,

                                    city =
                                        city,

                                    state =
                                        state,

                                    zip =
                                        zip,

                                    email =
                                        email,

                                    password =
                                        password
                                )

                        if (result.isSuccess) {

                            repository.logout()

                            loginError =
                                "Account created successfully. Please sign in."

                            navController.navigate(
                                Routes.LOGIN
                            ) {

                                popUpTo(
                                    Routes.LOGIN
                                ) {
                                    inclusive = true
                                }

                                launchSingleTop =
                                    true
                            }

                        } else {

                            registerError =
                                result
                                    .exceptionOrNull()
                                    ?.message
                                    ?: "Registration failed."
                        }
                    }
                },

                onBackToLogin = {

                    navController
                        .popBackStack()
                },

                onViewTerms = {

                    registerError =
                        "Terms screen is not connected yet."
                },

                errorMessage =
                    registerError
            )
        }



        /*
        =====================================================
        PARENT DASHBOARD
        =====================================================
        */

        composable(
            Routes.PARENT_DASHBOARD
        ) {

            ParentDashboardScreen(

                firstName =
                    currentFirstName,

                selectedAction =
                    parentSelectedAction,

                onActionSelected = { action ->

                    parentSelectedAction =
                        action

                    when (action) {

                        ParentAction.HOME -> {

                            navController.navigate(
                                Routes.PARENT_DASHBOARD
                            ) {
                                launchSingleTop =
                                    true
                            }
                        }

                        ParentAction.CREATE_CHORE -> {

                            navController.navigate(
                                Routes.CREATE_CHORE
                            )
                        }

                        ParentAction.VIEW_CHORES -> {

                            navController.navigate(
                                Routes.VIEW_CHORES
                            )
                        }

                        ParentAction.EDIT_CHORES -> {

                            navController.navigate(
                                Routes.EDIT_CHORES
                            )
                        }

                        ParentAction.APPROVALS -> {

                            navController.navigate(
                                Routes.CHORE_APPROVALS
                            )
                        }

                        ParentAction.REWARDS -> {

                            navController.navigate(
                                Routes.REWARDS
                            )
                        }

                        ParentAction.AI_INSIGHTS -> {

                            navController.navigate(
                                Routes.AI_INSIGHTS
                            )
                        }

                        ParentAction.HOUSEHOLD -> {

                            navController.navigate(
                                Routes.ADD_MEMBER
                            )
                        }
                    }
                },

                onLogout = {

                    logoutAndGoToUserType()
                },

                successMessage =
                    successMessage,

                onClearSuccessMessage = {

                    successMessage = null
                }
            )
        }



        /*
        =====================================================
        ADULT DASHBOARD
        =====================================================
        */

        composable(
            Routes.ADULT_DASHBOARD
        ) {

            AdultDashboardScreen(

                firstName =
                    currentFirstName,

                selectedAction =
                    adultSelectedAction,

                onActionSelected = { action ->

                    adultSelectedAction =
                        action

                    when (action) {

                        AdultAction.HOME -> {

                            navController.navigate(
                                Routes.ADULT_DASHBOARD
                            ) {
                                launchSingleTop =
                                    true
                            }
                        }

                        AdultAction.CREATE_CHORE -> {

                            navController.navigate(
                                Routes.CREATE_CHORE
                            )
                        }

                        AdultAction.VIEW_CHORES -> {

                            navController.navigate(
                                Routes.VIEW_CHORES
                            )
                        }

                        AdultAction.EDIT_CHORES -> {

                            navController.navigate(
                                Routes.EDIT_CHORES
                            )
                        }

                        AdultAction.APPROVALS -> {

                            navController.navigate(
                                Routes.CHORE_APPROVALS
                            )
                        }

                        AdultAction.REWARDS -> {

                            navController.navigate(
                                Routes.REWARDS
                            )
                        }

                        AdultAction.PROFILE -> {
                        }
                    }
                },

                onLogout = {

                    logoutAndGoToUserType()
                }
            )
        }



        /*
        =====================================================
        CHILD DASHBOARD
        =====================================================
        */

        composable(
            Routes.CHILD_DASHBOARD
        ) {

            ChildDashboardScreen(

                firstName =
                    currentFirstName,

                selectedAction =
                    childSelectedAction,

                onActionSelected = { action ->

                    childSelectedAction =
                        action

                    when (action) {

                        ChildAction.HOME -> {

                            navController.navigate(
                                Routes.CHILD_DASHBOARD
                            ) {
                                launchSingleTop =
                                    true
                            }
                        }

                        ChildAction.VIEW_CHORES -> {

                            navController.navigate(
                                Routes.VIEW_CHORES
                            )
                        }

                        ChildAction.UPDATE_STATUS -> {

                            navController.navigate(
                                Routes.UPDATE_CHORE_STATUS
                            )
                        }

                        ChildAction.REWARDS -> {

                            navController.navigate(
                                Routes.REWARDS
                            )
                        }

                        ChildAction.PROFILE -> {
                        }
                    }
                },

                onLogout = {

                    logoutAndGoToUserType()
                }
            )
        }



        /*
        =====================================================
        ADD MEMBER
        =====================================================
        */

        composable(
            Routes.ADD_MEMBER
        ) {

            AddMemberScreen(

                navController =
                    navController,

                onLogout = {

                    logoutAndGoToUserType()
                }
            )
        }



        /*
        =====================================================
        ADD CHILD
        =====================================================
        */

        composable(
            Routes.ADD_CHILD_MEMBER
        ) {

            AddChildMemberScreen(

                onSave = {
                        firstName,
                        lastName,
                        dob,
                        address,
                        city,
                        state,
                        zip,
                        email,
                        password ->

                    scope.launch {

                        memberError = null

                        val result =
                            repository
                                .createMemberAccount(

                                    firstName =
                                        firstName,

                                    lastName =
                                        lastName,

                                    dob =
                                        dob,

                                    address =
                                        address,

                                    city =
                                        city,

                                    state =
                                        state,

                                    zip =
                                        zip,

                                    email =
                                        email,

                                    password =
                                        password,

                                    role =
                                        "CHILD"
                                )

                        if (result.isSuccess) {

                            successMessage =
                                "Child account created successfully."

                            navController.navigate(
                                Routes.PARENT_DASHBOARD
                            ) {

                                popUpTo(
                                    Routes.PARENT_DASHBOARD
                                ) {
                                    inclusive = false
                                }

                                launchSingleTop =
                                    true
                            }

                        } else {

                            memberError =
                                result
                                    .exceptionOrNull()
                                    ?.message
                                    ?: "Failed to add child member."
                        }
                    }
                },

                onBack = {

                    navController
                        .popBackStack()
                },

                onLogout = {

                    logoutAndGoToUserType()
                },

                errorMessage =
                    memberError
            )
        }



        /*
        =====================================================
        ADD ADULT
        =====================================================
        */

        composable(
            Routes.ADD_ADULT_MEMBER
        ) {

            AddAdultMemberScreen(

                onSave = {
                        firstName,
                        lastName,
                        dob,
                        address,
                        city,
                        state,
                        zip,
                        email,
                        password ->

                    scope.launch {

                        memberError = null

                        val result =
                            repository
                                .createMemberAccount(

                                    firstName =
                                        firstName,

                                    lastName =
                                        lastName,

                                    dob =
                                        dob,

                                    address =
                                        address,

                                    city =
                                        city,

                                    state =
                                        state,

                                    zip =
                                        zip,

                                    email =
                                        email,

                                    password =
                                        password,

                                    role =
                                        "ADULT"
                                )

                        if (result.isSuccess) {

                            successMessage =
                                "Adult account created successfully."

                            navController.navigate(
                                Routes.PARENT_DASHBOARD
                            ) {

                                popUpTo(
                                    Routes.PARENT_DASHBOARD
                                ) {
                                    inclusive = false
                                }

                                launchSingleTop =
                                    true
                            }

                        } else {

                            memberError =
                                result
                                    .exceptionOrNull()
                                    ?.message
                                    ?: "Failed to add adult member."
                        }
                    }
                },

                onBack = {

                    navController
                        .popBackStack()
                },

                onLogout = {

                    logoutAndGoToUserType()
                },

                errorMessage =
                    memberError
            )
        }



        /*
        =====================================================
        VIEW HOUSEHOLD
        =====================================================
        */

        composable(
            Routes.VIEW_HOUSEHOLD
        ) {

            ViewHouseholdScreen(

                members =
                    householdMembers,

                selectedAction =
                    ParentAction.HOUSEHOLD,

                onActionSelected = { action ->

                    parentSelectedAction =
                        action

                    when (action) {

                        ParentAction.HOME ->

                            navController.navigate(
                                Routes.PARENT_DASHBOARD
                            )

                        ParentAction.CREATE_CHORE ->

                            navController.navigate(
                                Routes.CREATE_CHORE
                            )

                        ParentAction.VIEW_CHORES ->

                            navController.navigate(
                                Routes.VIEW_CHORES
                            )

                        ParentAction.EDIT_CHORES ->

                            navController.navigate(
                                Routes.EDIT_CHORES
                            )

                        ParentAction.APPROVALS ->

                            navController.navigate(
                                Routes.CHORE_APPROVALS
                            )

                        ParentAction.REWARDS ->

                            navController.navigate(
                                Routes.REWARDS
                            )

                        ParentAction.AI_INSIGHTS ->

                            navController.navigate(
                                Routes.AI_INSIGHTS
                            )

                        ParentAction.HOUSEHOLD ->

                            navController.navigate(
                                Routes.ADD_MEMBER
                            )
                    }
                },

                onBack = {

                    navController
                        .popBackStack()
                },

                onLogout = {

                    logoutAndGoToUserType()
                },

                onMemberSelected = { memberUid ->

                    navController.navigate(
                        "${Routes.EDIT_MEMBER}/$memberUid"
                    )
                }
            )



            LaunchedEffect(
                currentRoute
            ) {

                refreshHouseholdMembers()
            }
        }



        /*
        =====================================================
        CREATE CHORE
        =====================================================

        Existing email behavior preserved.
        */

        composable(
            Routes.CREATE_CHORE
        ) {

            LaunchedEffect(
                currentRoute
            ) {

                refreshChildOptions()
            }



            CreateChoreScreen(

                childOptions =
                    childOptions,

                onSave = {
                        title,
                        description,
                        dueDate,
                        rewardAmount,
                        assignedChildUid,
                        assignedChildName ->

                    scope.launch {

                        choreError = null

                        val result =
                            repository
                                .createChore(

                                    title =
                                        title,

                                    description =
                                        description,

                                    dueDate =
                                        dueDate,

                                    rewardAmount =
                                        rewardAmount,

                                    assignedChildUid =
                                        assignedChildUid,

                                    assignedChildName =
                                        assignedChildName
                                )



                        if (result.isSuccess) {

                            /*
                            EMAIL CHILD
                            */

                            val emailResult =
                                emailNotifications
                                    .notifyChildChoreCreated(

                                        childUid =
                                            assignedChildUid,

                                        choreTitle =
                                            title,

                                        dueDate =
                                            dueDate,

                                        rewardAmount =
                                            rewardAmount
                                    )



                            if (
                                emailResult.isFailure
                            ) {

                                println(
                                    "Chore created but child email failed: " +
                                            emailResult
                                                .exceptionOrNull()
                                                ?.message
                                )
                            }



                            successMessage =
                                "Chore created successfully."

                            navController
                                .popBackStack()

                        } else {

                            choreError =
                                result
                                    .exceptionOrNull()
                                    ?.message
                                    ?: "Failed to create chore."
                        }
                    }
                },

                onBack = {

                    navController
                        .popBackStack()
                },

                onLogout = {

                    logoutAndGoToUserType()
                },

                onCancel = {

                    navController
                        .popBackStack()
                }
            )
        }



        /*
        =====================================================
        VIEW CHORES
        =====================================================
        */

        composable(
            Routes.VIEW_CHORES
        ) {

            LaunchedEffect(
                currentRoute
            ) {

                refreshChores()
            }



            ViewChoresScreen(

                title =
                    "View Chores",

                chores =
                    chores,

                canEdit =
                    false,

                onBack = {

                    navController
                        .popBackStack()
                },

                onLogout = {

                    logoutAndGoToUserType()
                },

                isKidsMode =
                    currentRole.uppercase() ==
                            "CHILD"
            )
        }



        /*
        =====================================================
        EDIT CHORES
        =====================================================
        */

        composable(
            Routes.EDIT_CHORES
        ) {

            LaunchedEffect(
                currentRoute
            ) {

                refreshChores()
            }



            ViewChoresScreen(

                title =
                    "Edit Chores",

                chores =
                    chores,

                canEdit =
                    currentRole == "PARENT" ||
                            currentRole == "ADULT",

                onBack = {

                    navController
                        .popBackStack()
                },

                onLogout = {

                    logoutAndGoToUserType()
                },

                onEditChore = { choreId ->

                    navController.navigate(
                        "${Routes.EDIT_CHORE}/$choreId"
                    )
                },

                isKidsMode =
                    false
            )
        }



        /*
        =====================================================
        UPDATE CHORE STATUS
        =====================================================

        NEW AI FLOW:

        IN PROGRESS
             ↓
        Normal status update

        COMPLETED
             ↓
        Photo Submission Screen
             ↓
        Upload Photo
             ↓
        Firebase Storage
             ↓
        AI Analysis
             ↓
        COMPLETED
             ↓
        Parent Email
        */

        composable(
            Routes.UPDATE_CHORE_STATUS
        ) {

            LaunchedEffect(
                currentRoute
            ) {

                refreshChores()
            }



            UpdateChoreStatusScreen(

                chores =
                    chores,

                onBack = {

                    navController
                        .popBackStack()
                },

                onLogout = {

                    logoutAndGoToUserType()
                },

                onUpdateStatus = {
                        choreId,
                        newStatus ->



                    /*
                    =========================================
                    CHILD CHOOSES COMPLETED
                    =========================================

                    DO NOT mark completed yet.

                    Send child to photo upload screen.
                    */

                    if (
                        newStatus
                            .uppercase() ==
                        "COMPLETED"
                    ) {

                        choreError = null

                        navController.navigate(
                            "${Routes.PHOTO_SUBMISSION}/$choreId"
                        )

                    } else {

                        /*
                        =====================================
                        IN PROGRESS
                        =====================================
                        */

                        scope.launch {

                            choreError = null

                            val result =
                                repository
                                    .updateChoreStatus(
                                        choreId,
                                        newStatus
                                    )



                            if (
                                result.isSuccess
                            ) {

                                successMessage =
                                    "Chore status updated successfully."

                                refreshChores()

                            } else {

                                choreError =
                                    result
                                        .exceptionOrNull()
                                        ?.message
                                        ?: "Failed to update chore status."
                            }
                        }
                    }
                },

                isKidsMode =
                    currentRole
                        .uppercase() ==
                            "CHILD"
            )
        }



        /*
        =====================================================
        NEW AI PHOTO SUBMISSION SCREEN
        =====================================================

        Child uploads completed chore photo.

        FirebaseRepository:
            1. Uploads photo
            2. Stores photo URL
            3. Calls analyzeChorePhoto
            4. AI analyzes image
            5. AI result stored on chore
            6. Chore becomes COMPLETED

        Parent is then emailed.
        */

        composable(

            route =
                "${Routes.PHOTO_SUBMISSION}/{choreId}",

            arguments =
                listOf(

                    navArgument(
                        "choreId"
                    ) {

                        type =
                            NavType.StringType
                    }
                )

        ) { backStack ->



            val choreId =
                backStack
                    .arguments
                    ?.getString(
                        "choreId"
                    )
                    .orEmpty()



            var selectedChore by
            remember(
                choreId
            ) {

                mutableStateOf<ChoreRow?>(
                    null
                )
            }



            var isSubmitting by
            remember {

                mutableStateOf(
                    false
                )
            }



            /*
            LOAD CHORE INFORMATION
            */

            LaunchedEffect(
                choreId
            ) {

                choreError = null

                val result =
                    repository
                        .loadChoreById(
                            choreId
                        )



                if (
                    result.isSuccess
                ) {

                    selectedChore =
                        result
                            .getOrNull()

                } else {

                    choreError =
                        result
                            .exceptionOrNull()
                            ?.message
                            ?: "Failed to load chore."
                }
            }



            PhotoSubmissionScreen(

                chore =
                    selectedChore,

                isSubmitting =
                    isSubmitting,

                errorMessage =
                    choreError,

                onBack = {

                    navController
                        .popBackStack()
                },

                onLogout = {

                    logoutAndGoToUserType()
                },

                onSubmitPhoto = { photoUri ->

                    scope.launch {

                        choreError = null
                        isSubmitting = true

                        val result =
                            repository
                                .submitChorePhoto(
                                    choreId =
                                        choreId,

                                    photoUri =
                                        photoUri
                                )



                        if (
                            result.isSuccess
                        ) {

                            /*
                            RELOAD CHORE AFTER AI ANALYSIS
                            */

                            val refreshed =
                                repository
                                    .loadChoreById(
                                        choreId
                                    )

                            val completedChore =
                                refreshed.getOrNull()



                            /*
                            EMAIL PARENT
                            */

                            if (
                                completedChore != null
                            ) {

                                val emailResult =
                                    emailNotifications
                                        .notifyParentChoreCompleted(

                                            choreId = choreId
                                        )



                                if (
                                    emailResult.isFailure
                                ) {

                                    println(
                                        "Photo submitted but parent email failed: " +
                                                emailResult
                                                    .exceptionOrNull()
                                                    ?.message
                                    )
                                }
                            }



                            successMessage =
                                "Chore photo submitted successfully."

                            refreshChores()

                            navController.navigate(
                                Routes.CHILD_DASHBOARD
                            ) {

                                popUpTo(
                                    Routes.CHILD_DASHBOARD
                                ) {
                                    inclusive = false
                                }

                                launchSingleTop =
                                    true
                            }

                        } else {

                            choreError =
                                result
                                    .exceptionOrNull()
                                    ?.message
                                    ?: "Failed to submit chore photo."
                        }

                        isSubmitting = false
                    }
                }
            )
        }



        /*
        =====================================================
        CHORE APPROVALS
        =====================================================
        */

        composable(
            Routes.CHORE_APPROVALS
        ) {

            LaunchedEffect(
                currentRoute
            ) {

                refreshChores()
            }



            ChoreApprovalsScreen(

                chores =
                    chores,

                onBack = {

                    navController
                        .popBackStack()
                },

                onLogout = {

                    logoutAndGoToUserType()
                },

                onApprove = { choreId ->

                    scope.launch {

                        choreError = null

                        val result =
                            repository
                                .approveChore(
                                    choreId
                                )



                        if (
                            result.isSuccess
                        ) {

                            val chore =
                                chores
                                    .firstOrNull {
                                        it.id ==
                                                choreId
                                    }



                            if (
                                chore != null
                            ) {

                                val emailResult =
                                    emailNotifications
                                        .notifyChildChoreApproved(

                                            choreId = choreId
                                        )



                                if (
                                    emailResult.isFailure
                                ) {

                                    println(
                                        "APPROVAL EMAIL ERROR: " +
                                                emailResult
                                                    .exceptionOrNull()
                                                    ?.message
                                    )
                                }
                            }



                            successMessage =
                                "Chore approved successfully."

                            refreshChores()

                        } else {

                            choreError =
                                result
                                    .exceptionOrNull()
                                    ?.message
                                    ?: "Failed to approve chore."
                        }
                    }
                },

                onReject = { choreId ->

                    scope.launch {

                        choreError = null

                        val chore =
                            chores
                                .firstOrNull {
                                    it.id ==
                                            choreId
                                }



                        val result =
                            repository
                                .rejectChore(
                                    choreId
                                )



                        if (
                            result.isSuccess
                        ) {

                            if (
                                chore != null
                            ) {

                                val emailResult =
                                    emailNotifications
                                        .notifyChildChoreRejected(
                                            choreId = choreId
                                        )



                                if (
                                    emailResult.isFailure
                                ) {

                                    val errorMessage =
                                        emailResult
                                            .exceptionOrNull()
                                            ?.message
                                            ?: "Unknown email error."



                                    println(
                                        "REJECTION EMAIL ERROR: $errorMessage"
                                    )



                                    choreError =
                                        "Chore rejected, but email failed: $errorMessage"
                                }
                            }



                            refreshChores()

                        } else {

                            choreError =
                                result
                                    .exceptionOrNull()
                                    ?.message
                                    ?: "Failed to reject chore."
                        }
                    }
                }
            )
        }



        /*
        =====================================================
        REWARDS
        =====================================================
        */

        composable(
            Routes.REWARDS
        ) {

            LaunchedEffect(
                currentRoute
            ) {

                refreshChores()



                if (
                    currentRole ==
                    "PARENT" ||
                    currentRole ==
                    "ADULT"
                ) {

                    refreshHouseholdMembers()
                }
            }



            RewardsScreen(

                currentRole =
                    currentRole,

                currentUserUid =
                    currentUserUid,

                chores =
                    chores,

                children =
                    householdMembers.filter {

                        it.userType
                            .uppercase() ==
                                "CHILD"
                    },

                onBack = {

                    navController
                        .popBackStack()
                },

                onLogout = {

                    logoutAndGoToUserType()
                },

                isKidsMode =
                    currentRole
                        .uppercase() ==
                            "CHILD"
            )
        }



        /*
        =====================================================
        AI INSIGHTS
        =====================================================
        */

        composable(
            Routes.AI_INSIGHTS
        ) {

            AIInsightsScreen(

                repository =
                    repository,

                onBack = {

                    navController
                        .popBackStack()
                },

                onLogout = {

                    logoutAndGoToUserType()
                }
            )
        }



        /*
        =====================================================
        EDIT SINGLE CHORE
        =====================================================
        */

        composable(

            route =
                "${Routes.EDIT_CHORE}/{choreId}",

            arguments =
                listOf(

                    navArgument(
                        "choreId"
                    ) {

                        type =
                            NavType.StringType
                    }
                )

        ) { backStack ->



            val choreId =
                backStack
                    .arguments
                    ?.getString(
                        "choreId"
                    )
                    .orEmpty()



            var selectedChore by
            remember(
                choreId
            ) {

                mutableStateOf<ChoreRow?>(
                    null
                )
            }



            LaunchedEffect(
                choreId
            ) {

                refreshChildOptions()



                val result =
                    repository
                        .loadChoreById(
                            choreId
                        )



                if (
                    result.isSuccess
                ) {

                    selectedChore =
                        result.getOrNull()

                } else {

                    choreError =
                        result
                            .exceptionOrNull()
                            ?.message
                            ?: "Failed to load chore."
                }
            }



            EditChoreScreen(

                chore =
                    selectedChore,

                childOptions =
                    childOptions,

                onSave = {
                        title,
                        description,
                        dueDate,
                        rewardAmount,
                        assignedChildUid,
                        assignedChildName ->



                    scope.launch {

                        val result =
                            repository
                                .updateChore(

                                    choreId =
                                        choreId,

                                    title =
                                        title,

                                    description =
                                        description,

                                    dueDate =
                                        dueDate,

                                    rewardAmount =
                                        rewardAmount,

                                    assignedChildUid =
                                        assignedChildUid,

                                    assignedChildName =
                                        assignedChildName
                                )



                        if (
                            result.isSuccess
                        ) {

                            successMessage =
                                "Chore updated successfully."

                            navController
                                .popBackStack()

                        } else {

                            choreError =
                                result
                                    .exceptionOrNull()
                                    ?.message
                                    ?: "Failed to update chore."
                        }
                    }
                },

                onBack = {

                    navController
                        .popBackStack()
                },

                onLogout = {

                    logoutAndGoToUserType()
                }
            )
        }



        /*
        =====================================================
        EDIT MEMBER
        =====================================================
        */

        composable(

            route =
                "${Routes.EDIT_MEMBER}/{memberUid}",

            arguments =
                listOf(

                    navArgument(
                        "memberUid"
                    ) {

                        type =
                            NavType.StringType
                    }
                )

        ) { backStack ->



            val memberUid =
                backStack
                    .arguments
                    ?.getString(
                        "memberUid"
                    )
                    .orEmpty()



            var selectedMember by
            remember(
                memberUid
            ) {

                mutableStateOf<HouseholdMemberRow?>(
                    null
                )
            }



            LaunchedEffect(
                memberUid
            ) {

                editMemberError =
                    null



                val result =
                    repository
                        .loadMemberById(
                            memberUid
                        )



                if (
                    result.isSuccess
                ) {

                    selectedMember =
                        result
                            .getOrNull()

                } else {

                    editMemberError =
                        result
                            .exceptionOrNull()
                            ?.message
                            ?: "Failed to load member."
                }
            }



            EditMemberScreen(

                member =
                    selectedMember,



                onSave = {
                        firstName,
                        lastName,
                        address,
                        city,
                        state,
                        zip,
                        email ->



                    scope.launch {

                        editMemberError =
                            null



                        val result =
                            repository
                                .updateMember(

                                    memberUid =
                                        memberUid,

                                    firstName =
                                        firstName,

                                    lastName =
                                        lastName,

                                    address =
                                        address,

                                    city =
                                        city,

                                    state =
                                        state,

                                    zip =
                                        zip,

                                    email =
                                        email
                                )



                        if (
                            result.isSuccess
                        ) {

                            successMessage =
                                "Household member updated successfully."

                            refreshHouseholdMembers()

                            navController
                                .popBackStack()

                        } else {

                            editMemberError =
                                result
                                    .exceptionOrNull()
                                    ?.message
                                    ?: "Failed to update household member."
                        }
                    }
                },



                onDelete = {

                    scope.launch {

                        editMemberError =
                            null



                        val result =
                            repository
                                .deleteMember(
                                    memberUid
                                )



                        if (
                            result.isSuccess
                        ) {

                            successMessage =
                                "Household member deleted successfully."

                            refreshHouseholdMembers()

                            navController
                                .popBackStack()

                        } else {

                            editMemberError =
                                result
                                    .exceptionOrNull()
                                    ?.message
                                    ?: "Failed to delete household member."
                        }
                    }
                },



                onBack = {

                    navController
                        .popBackStack()
                },



                onLogout = {

                    logoutAndGoToUserType()
                },



                errorMessage =
                    editMemberError
            )
        }
    }
}

