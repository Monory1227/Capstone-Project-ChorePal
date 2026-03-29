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
    const val UPDATE_CHORE_STATUS = "update_chore_status"
    const val CHORE_APPROVALS = "chore_approvals"
    const val REWARDS = "rewards"
}

@Composable
fun AuthNav() {
    val navController = rememberNavController()
    val repository = remember { FirebaseRepository() }
    val scope = rememberCoroutineScope()

    var loginError by remember { mutableStateOf<String?>(null) }
    var registerError by remember { mutableStateOf<String?>(null) }
    var memberError by remember { mutableStateOf<String?>(null) }
    var editMemberError by remember { mutableStateOf<String?>(null) }
    var choreError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    var currentFirstName by remember { mutableStateOf("") }
    var currentRole by remember { mutableStateOf("") }
    var currentUserUid by remember { mutableStateOf("") }

    var parentSelectedAction by remember { mutableStateOf(ParentAction.HOME) }
    var adultSelectedAction by remember { mutableStateOf(AdultAction.HOME) }
    var childSelectedAction by remember { mutableStateOf(ChildAction.HOME) }

    val householdMembers = remember { mutableStateListOf<HouseholdMemberRow>() }
    val childOptions = remember { mutableStateListOf<ChildOption>() }
    val chores = remember { mutableStateListOf<ChoreRow>() }

    fun logoutAndGoToLogin() {
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
        parentSelectedAction = ParentAction.HOME
        adultSelectedAction = AdultAction.HOME
        childSelectedAction = ChildAction.HOME

        navController.navigate(Routes.LOGIN) {
            popUpTo(0)
        }
    }

    fun refreshHouseholdMembers(afterRefresh: (() -> Unit)? = null) {
        scope.launch {
            val result = repository.loadHouseholdMembers()
            if (result.isSuccess) {
                householdMembers.clear()
                householdMembers.addAll(result.getOrDefault(emptyList()))
                afterRefresh?.invoke()
            } else {
                memberError = result.exceptionOrNull()?.message ?: "Failed to load household members."
            }
        }
    }

    fun refreshChildOptions(afterRefresh: (() -> Unit)? = null) {
        scope.launch {
            val result = repository.loadHouseholdChildren()
            if (result.isSuccess) {
                childOptions.clear()
                childOptions.addAll(result.getOrDefault(emptyList()))
                afterRefresh?.invoke()
            } else {
                choreError = result.exceptionOrNull()?.message ?: "Failed to load children."
            }
        }
    }

    fun refreshChores(afterRefresh: (() -> Unit)? = null) {
        scope.launch {
            val result = repository.loadChores()
            if (result.isSuccess) {
                chores.clear()
                chores.addAll(result.getOrDefault(emptyList()))
                afterRefresh?.invoke()
            } else {
                choreError = result.exceptionOrNull()?.message ?: "Failed to load chores."
            }
        }
    }

    suspend fun routeLoggedInUser() {
        val profileResult = repository.loadCurrentUserProfile()
        if (profileResult.isFailure) {
            loginError = profileResult.exceptionOrNull()?.message ?: "Unable to load profile."
            return
        }

        val profile = profileResult.getOrDefault(emptyMap())
        val role = (profile["role"] as? String).orEmpty().uppercase()
        currentFirstName = (profile["firstName"] as? String).orEmpty()
        currentRole = role
        currentUserUid = (profile["uid"] as? String).orEmpty()

        when (role) {
            "PARENT" -> {
                parentSelectedAction = ParentAction.HOME
                navController.navigate(Routes.PARENT_DASHBOARD) {
                    popUpTo(0)
                }
            }
            "ADULT" -> {
                adultSelectedAction = AdultAction.HOME
                navController.navigate(Routes.ADULT_DASHBOARD) {
                    popUpTo(0)
                }
            }
            "CHILD" -> {
                childSelectedAction = ChildAction.HOME
                navController.navigate(Routes.CHILD_DASHBOARD) {
                    popUpTo(0)
                }
            }
            else -> {
                loginError = "Unknown user role."
            }
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route.orEmpty()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLogin = { email, password ->
                    scope.launch {
                        loginError = null
                        val result = repository.login(email, password)
                        if (result.isSuccess) {
                            routeLoggedInUser()
                        } else {
                            loginError = "Invalid username or password"
                        }
                    }
                },
                onForgotCredentials = {
                    loginError = "Forgot credentials flow is not connected yet."
                },
                onRegister = {
                    loginError = null
                    navController.navigate(Routes.REGISTER)
                },
                errorMessage = loginError
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegister = { firstName, lastName, dob, address, city, state, zip, email, password ->
                    scope.launch {
                        registerError = null
                        val result = repository.registerParent(
                            firstName = firstName,
                            lastName = lastName,
                            dob = dob,
                            address = address,
                            city = city,
                            state = state,
                            zip = zip,
                            email = email,
                            password = password
                        )

                        if (result.isSuccess) {
                            repository.logout()
                            loginError = "Account created successfully. Please sign in."
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            registerError = result.exceptionOrNull()?.message ?: "Registration failed."
                        }
                    }
                },
                onBackToLogin = { navController.popBackStack() },
                onViewTerms = {
                    registerError = "Terms screen is not connected yet."
                },
                errorMessage = registerError
            )
        }

        composable(Routes.PARENT_DASHBOARD) {
            ParentDashboardScreen(
                firstName = currentFirstName,
                selectedAction = parentSelectedAction,
                onActionSelected = { action ->
                    parentSelectedAction = action
                    when (action) {
                        ParentAction.HOME -> navController.navigate(Routes.PARENT_DASHBOARD) {
                            launchSingleTop = true
                        }
                        ParentAction.CREATE_CHORE -> navController.navigate(Routes.CREATE_CHORE)
                        ParentAction.VIEW_CHORES -> navController.navigate(Routes.VIEW_CHORES)
                        ParentAction.EDIT_CHORES -> navController.navigate(Routes.EDIT_CHORES)
                        ParentAction.APPROVALS -> navController.navigate(Routes.CHORE_APPROVALS)
                        ParentAction.REWARDS -> navController.navigate(Routes.REWARDS)
                        ParentAction.HOUSEHOLD -> navController.navigate(Routes.ADD_MEMBER)
                    }
                },
                onLogout = { logoutAndGoToLogin() },
                successMessage = successMessage,
                onClearSuccessMessage = { successMessage = null }
            )
        }

        composable(Routes.ADULT_DASHBOARD) {
            AdultDashboardScreen(
                firstName = currentFirstName,
                selectedAction = adultSelectedAction,
                onActionSelected = { action ->
                    adultSelectedAction = action
                    when (action) {
                        AdultAction.HOME -> navController.navigate(Routes.ADULT_DASHBOARD) {
                            launchSingleTop = true
                        }
                        AdultAction.CREATE_CHORE -> navController.navigate(Routes.CREATE_CHORE)
                        AdultAction.VIEW_CHORES -> navController.navigate(Routes.VIEW_CHORES)
                        AdultAction.EDIT_CHORES -> navController.navigate(Routes.EDIT_CHORES)
                        AdultAction.APPROVALS -> navController.navigate(Routes.CHORE_APPROVALS)
                        AdultAction.REWARDS -> navController.navigate(Routes.REWARDS)
                        AdultAction.PROFILE -> {}
                    }
                },
                onLogout = { logoutAndGoToLogin() }
            )
        }

        composable(Routes.CHILD_DASHBOARD) {
            ChildDashboardScreen(
                firstName = currentFirstName,
                selectedAction = childSelectedAction,
                onActionSelected = { action ->
                    childSelectedAction = action
                    when (action) {
                        ChildAction.HOME -> navController.navigate(Routes.CHILD_DASHBOARD) {
                            launchSingleTop = true
                        }
                        ChildAction.VIEW_CHORES -> navController.navigate(Routes.VIEW_CHORES)
                        ChildAction.UPDATE_STATUS -> navController.navigate(Routes.UPDATE_CHORE_STATUS)
                        ChildAction.REWARDS -> navController.navigate(Routes.REWARDS)
                        ChildAction.PROFILE -> {}
                    }
                },
                onLogout = { logoutAndGoToLogin() }
            )
        }

        composable(Routes.ADD_MEMBER) {
            AddMemberScreen(
                navController = navController,
                onLogout = { logoutAndGoToLogin() }
            )
        }

        composable(Routes.ADD_CHILD_MEMBER) {
            AddChildMemberScreen(
                onSave = { firstName, lastName, dob, address, city, state, zip, email, password ->
                    scope.launch {
                        memberError = null
                        val result = repository.createMemberAccount(
                            firstName = firstName,
                            lastName = lastName,
                            dob = dob,
                            address = address,
                            city = city,
                            state = state,
                            zip = zip,
                            email = email,
                            password = password,
                            role = "CHILD"
                        )
                        if (result.isSuccess) {
                            successMessage = "Child account created successfully."
                            navController.navigate(Routes.PARENT_DASHBOARD) {
                                popUpTo(Routes.PARENT_DASHBOARD) { inclusive = false }
                                launchSingleTop = true
                            }
                        } else {
                            memberError = result.exceptionOrNull()?.message ?: "Failed to add child member."
                        }
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { logoutAndGoToLogin() },
                errorMessage = memberError
            )
        }

        composable(Routes.ADD_ADULT_MEMBER) {
            AddAdultMemberScreen(
                onSave = { firstName, lastName, dob, address, city, state, zip, email, password ->
                    scope.launch {
                        memberError = null
                        val result = repository.createMemberAccount(
                            firstName = firstName,
                            lastName = lastName,
                            dob = dob,
                            address = address,
                            city = city,
                            state = state,
                            zip = zip,
                            email = email,
                            password = password,
                            role = "ADULT"
                        )
                        if (result.isSuccess) {
                            successMessage = "Adult account created successfully."
                            navController.navigate(Routes.PARENT_DASHBOARD) {
                                popUpTo(Routes.PARENT_DASHBOARD) { inclusive = false }
                                launchSingleTop = true
                            }
                        } else {
                            memberError = result.exceptionOrNull()?.message ?: "Failed to add adult member."
                        }
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { logoutAndGoToLogin() },
                errorMessage = memberError
            )
        }

        composable(Routes.VIEW_HOUSEHOLD) {
            ViewHouseholdScreen(
                members = householdMembers,
                selectedAction = ParentAction.HOUSEHOLD,
                onActionSelected = { action ->
                    parentSelectedAction = action
                    when (action) {
                        ParentAction.HOME -> navController.navigate(Routes.PARENT_DASHBOARD)
                        ParentAction.CREATE_CHORE -> navController.navigate(Routes.CREATE_CHORE)
                        ParentAction.VIEW_CHORES -> navController.navigate(Routes.VIEW_CHORES)
                        ParentAction.EDIT_CHORES -> navController.navigate(Routes.EDIT_CHORES)
                        ParentAction.APPROVALS -> navController.navigate(Routes.CHORE_APPROVALS)
                        ParentAction.REWARDS -> navController.navigate(Routes.REWARDS)
                        ParentAction.HOUSEHOLD -> navController.navigate(Routes.ADD_MEMBER)
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { logoutAndGoToLogin() },
                onMemberSelected = { memberUid ->
                    navController.navigate("${Routes.EDIT_MEMBER}/$memberUid")
                }
            )

            LaunchedEffect(currentRoute) {
                refreshHouseholdMembers()
            }
        }

        composable(Routes.CREATE_CHORE) {
            LaunchedEffect(currentRoute) {
                refreshChildOptions()
            }

            CreateChoreScreen(
                childOptions = childOptions,
                onSave = { title, description, dueDate, rewardAmount, assignedChildUid, assignedChildName ->
                    scope.launch {
                        choreError = null
                        val result = repository.createChore(
                            title = title,
                            description = description,
                            dueDate = dueDate,
                            rewardAmount = rewardAmount,
                            assignedChildUid = assignedChildUid,
                            assignedChildName = assignedChildName
                        )
                        if (result.isSuccess) {
                            successMessage = "Chore created successfully."
                            navController.popBackStack()
                        } else {
                            choreError = result.exceptionOrNull()?.message ?: "Failed to create chore."
                        }
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { logoutAndGoToLogin() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(Routes.VIEW_CHORES) {
            LaunchedEffect(currentRoute) {
                refreshChores()
            }

            ViewChoresScreen(
                title = "View Chores",
                chores = chores,
                canEdit = false,
                onBack = { navController.popBackStack() },
                onLogout = { logoutAndGoToLogin() }
            )
        }

        composable(Routes.EDIT_CHORES) {
            LaunchedEffect(currentRoute) {
                refreshChores()
            }

            ViewChoresScreen(
                title = "Edit Chores",
                chores = chores,
                canEdit = currentRole == "PARENT" || currentRole == "ADULT",
                onBack = { navController.popBackStack() },
                onLogout = { logoutAndGoToLogin() },
                onEditChore = { choreId ->
                    navController.navigate("${Routes.EDIT_CHORE}/$choreId")
                }
            )
        }

        composable(Routes.UPDATE_CHORE_STATUS) {
            LaunchedEffect(currentRoute) {
                refreshChores()
            }

            UpdateChoreStatusScreen(
                chores = chores,
                onBack = { navController.popBackStack() },
                onLogout = { logoutAndGoToLogin() },
                onUpdateStatus = { choreId, newStatus ->
                    scope.launch {
                        val result = repository.updateChoreStatus(choreId, newStatus)
                        if (result.isSuccess) {
                            successMessage = "Chore status updated successfully."
                            refreshChores()
                        } else {
                            choreError = result.exceptionOrNull()?.message ?: "Failed to update chore status."
                        }
                    }
                }
            )
        }

        composable(Routes.CHORE_APPROVALS) {
            LaunchedEffect(currentRoute) {
                refreshChores()
            }

            ChoreApprovalsScreen(
                chores = chores,
                onBack = { navController.popBackStack() },
                onLogout = { logoutAndGoToLogin() },
                onApprove = { choreId ->
                    scope.launch {
                        val result = repository.reviewChore(choreId, true)
                        if (result.isSuccess) {
                            successMessage = "Chore approved successfully."
                            refreshChores()
                        } else {
                            choreError = result.exceptionOrNull()?.message ?: "Failed to approve chore."
                        }
                    }
                },
                onReject = { choreId ->
                    scope.launch {
                        val result = repository.reviewChore(choreId, false)
                        if (result.isSuccess) {
                            successMessage = "Chore rejected successfully."
                            refreshChores()
                        } else {
                            choreError = result.exceptionOrNull()?.message ?: "Failed to reject chore."
                        }
                    }
                }
            )
        }

        composable(Routes.REWARDS) {
            LaunchedEffect(currentRoute) {
                refreshChores()
                if (currentRole == "PARENT" || currentRole == "ADULT") {
                    refreshHouseholdMembers()
                }
            }

            RewardsScreen(
                currentRole = currentRole,
                currentUserUid = currentUserUid,
                chores = chores,
                children = householdMembers.filter { it.userType.uppercase() == "CHILD" },
                onBack = { navController.popBackStack() },
                onLogout = { logoutAndGoToLogin() }
            )
        }

        composable(
            route = "${Routes.EDIT_CHORE}/{choreId}",
            arguments = listOf(navArgument("choreId") { type = NavType.StringType })
        ) { backStack ->
            val choreId = backStack.arguments?.getString("choreId").orEmpty()
            var selectedChore by remember(choreId) { mutableStateOf<ChoreRow?>(null) }

            LaunchedEffect(choreId) {
                refreshChildOptions()
                val result = repository.loadChoreById(choreId)
                if (result.isSuccess) {
                    selectedChore = result.getOrNull()
                } else {
                    choreError = result.exceptionOrNull()?.message ?: "Failed to load chore."
                }
            }

            EditChoreScreen(
                chore = selectedChore,
                childOptions = childOptions,
                onSave = { title, description, dueDate, rewardAmount, assignedChildUid, assignedChildName ->
                    scope.launch {
                        val result = repository.updateChore(
                            choreId = choreId,
                            title = title,
                            description = description,
                            dueDate = dueDate,
                            rewardAmount = rewardAmount,
                            assignedChildUid = assignedChildUid,
                            assignedChildName = assignedChildName
                        )
                        if (result.isSuccess) {
                            successMessage = "Chore updated successfully."
                            navController.popBackStack()
                        } else {
                            choreError = result.exceptionOrNull()?.message ?: "Failed to update chore."
                        }
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { logoutAndGoToLogin() }
            )
        }

        composable(
            route = "${Routes.EDIT_MEMBER}/{memberUid}",
            arguments = listOf(navArgument("memberUid") { type = NavType.StringType })
        ) { backStack ->
            val memberUid = backStack.arguments?.getString("memberUid").orEmpty()
            var selectedMember by remember(memberUid) { mutableStateOf<HouseholdMemberRow?>(null) }

            LaunchedEffect(memberUid) {
                editMemberError = null
                val result = repository.loadMemberById(memberUid)
                if (result.isSuccess) {
                    selectedMember = result.getOrNull()
                } else {
                    editMemberError = result.exceptionOrNull()?.message ?: "Failed to load member."
                }
            }

            EditMemberScreen(
                member = selectedMember,
                onSave = { firstName, lastName, address, city, state, zip, email ->
                    scope.launch {
                        editMemberError = null
                        val result = repository.updateMember(
                            memberUid = memberUid,
                            firstName = firstName,
                            lastName = lastName,
                            address = address,
                            city = city,
                            state = state,
                            zip = zip,
                            email = email
                        )
                        if (result.isSuccess) {
                            successMessage = "Member updated successfully."
                            refreshHouseholdMembers {
                                navController.popBackStack()
                            }
                        } else {
                            editMemberError = result.exceptionOrNull()?.message ?: "Failed to update member."
                        }
                    }
                },
                onDelete = {
                    scope.launch {
                        editMemberError = null
                        val result = repository.deleteMember(memberUid)
                        if (result.isSuccess) {
                            successMessage = "Member deleted successfully."
                            refreshHouseholdMembers {
                                navController.popBackStack()
                            }
                        } else {
                            editMemberError = result.exceptionOrNull()?.message ?: "Failed to delete member."
                        }
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { logoutAndGoToLogin() },
                errorMessage = editMemberError
            )
        }
    }
}

