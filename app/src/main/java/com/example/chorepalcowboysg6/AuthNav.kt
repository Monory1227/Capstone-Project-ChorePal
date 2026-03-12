package com.example.chorepalcowboysg6

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val PARENT_DASHBOARD = "parent_dashboard"
    const val ADD_MEMBER = "add_member"
    const val VIEW_HOUSEHOLD = "view_household"

    const val ADD_CHILD_MEMBER = "add_child_member"
    const val ADD_ADULT_MEMBER = "add_adult_member"

    const val CREATE_CHORE = "create_chore"
    const val VIEW_CHORES = "view_chores"
    const val EDIT_CHORES = "edit_chores"
    const val CHORE_APPROVALS = "chore_approvals"
    const val REWARDS = "rewards"
}

@Composable
fun AuthNav() {
    val navController = rememberNavController()
    val repo = remember { FirebaseRepository() }
    val scope = rememberCoroutineScope()

    var firstName by rememberSaveable { mutableStateOf("") }
    var currentParentAction by rememberSaveable { mutableStateOf(ParentAction.HOME) }
    var successMessage by rememberSaveable { mutableStateOf<String?>(null) }

    fun routeFor(action: ParentAction): String = when (action) {
        ParentAction.HOME -> Routes.PARENT_DASHBOARD
        ParentAction.CREATE_CHORE -> Routes.CREATE_CHORE
        ParentAction.VIEW_CHORES -> Routes.VIEW_CHORES
        ParentAction.EDIT_CHORES -> Routes.EDIT_CHORES
        ParentAction.APPROVALS -> Routes.CHORE_APPROVALS
        ParentAction.REWARDS -> Routes.REWARDS
        ParentAction.HOUSEHOLD -> Routes.ADD_MEMBER
    }

    fun logout() {
        repo.logout()
        firstName = ""
        currentParentAction = ParentAction.HOME
        successMessage = null

        navController.navigate(Routes.LOGIN) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun goToDashboardWithSuccess(message: String = "User successfully created ✓!") {
        currentParentAction = ParentAction.HOME
        successMessage = message

        navController.navigate(Routes.PARENT_DASHBOARD) {
            popUpTo(Routes.PARENT_DASHBOARD) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLogin = { email, password ->
                    scope.launch {
                        val result = repo.login(email, password)
                        result.onSuccess {
                            val profileResult = repo.loadCurrentUserProfile()
                            profileResult.onSuccess { profile ->
                                firstName = profile["firstName"]?.toString().orEmpty().ifBlank { "User" }
                                currentParentAction = ParentAction.HOME
                                successMessage = null

                                navController.navigate(Routes.PARENT_DASHBOARD) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }.onFailure {
                                println("Profile load failed: ${it.message}")
                            }
                        }.onFailure {
                            println("Login failed: ${it.message}")
                        }
                    }
                },
                onForgotCredentials = { },
                onRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegister = { first, last, username, dob, address, city, state, zip, email, password ->
                    scope.launch {
                        val result = repo.registerParent(
                            firstName = first,
                            lastName = last,
                            username = username,
                            dob = dob,
                            address = address,
                            city = city,
                            state = state,
                            zip = zip,
                            email = email,
                            password = password
                        )

                        result.onSuccess {
                            firstName = first.trim().ifBlank { "Parent" }
                            currentParentAction = ParentAction.HOME
                            successMessage = "Parent account created ✓!"

                            navController.navigate(Routes.PARENT_DASHBOARD) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                                launchSingleTop = true
                            }
                        }.onFailure {
                            println("Register failed: ${it.message}")
                        }
                    }
                },
                onBackToLogin = { navController.popBackStack() },
                onViewTerms = { }
            )
        }

        composable(Routes.PARENT_DASHBOARD) {
            ParentDashboardScreen(
                firstName = firstName,
                selectedAction = currentParentAction,
                onActionSelected = { action ->
                    currentParentAction = action
                    navController.navigate(routeFor(action)) {
                        launchSingleTop = true
                    }
                },
                onLogout = { logout() },
                successMessage = successMessage,
                onClearSuccessMessage = { successMessage = null }
            )
        }

        composable(Routes.ADD_MEMBER) {
            AddMemberScreen(
                navController = navController,
                onLogout = { logout() }
            )
        }

        composable(Routes.VIEW_HOUSEHOLD) {
            ViewHouseholdScreen(
                members = emptyList<HouseholdMemberRow>(),
                selectedAction = ParentAction.HOUSEHOLD,
                onActionSelected = { action ->
                    currentParentAction = action
                    navController.navigate(routeFor(action)) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { logout() }
            )
        }

        composable(Routes.ADD_CHILD_MEMBER) {
            AddChildMemberScreen(
                onSave = { first, last, username, dob, address, city, state, zip, email, password ->
                    scope.launch {
                        val finalEmail =
                            if (email.isBlank()) "${username.trim().lowercase()}@chorepal.local"
                            else email.trim()

                        val result = repo.createMemberAccount(
                            firstName = first,
                            lastName = last,
                            username = username,
                            dob = dob,
                            address = address,
                            city = city,
                            state = state,
                            zip = zip,
                            email = finalEmail,
                            password = password,
                            role = "CHILD"
                        )

                        result.onSuccess {
                            goToDashboardWithSuccess("Child account created ✓!")
                        }.onFailure {
                            println("Create child failed: ${it.message}")
                        }
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { logout() }
            )
        }

        composable(Routes.ADD_ADULT_MEMBER) {
            AddAdultMemberScreen(
                onSave = { first, last, username, dob, address, city, state, zip, email, password ->
                    scope.launch {
                        val result = repo.createMemberAccount(
                            firstName = first,
                            lastName = last,
                            username = username,
                            dob = dob,
                            address = address,
                            city = city,
                            state = state,
                            zip = zip,
                            email = email,
                            password = password,
                            role = "ADULT"
                        )

                        result.onSuccess {
                            goToDashboardWithSuccess("Adult account created ✓!")
                        }.onFailure {
                            println("Create adult failed: ${it.message}")
                        }
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { logout() }
            )
        }

        composable(Routes.CREATE_CHORE) {
            CreateChoreScreen(
                onSave = { _, _, _, _, _ ->
                    navController.navigate(Routes.PARENT_DASHBOARD) {
                        popUpTo(Routes.PARENT_DASHBOARD) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { logout() },
                onCancel = {
                    currentParentAction = ParentAction.HOME
                    navController.navigate(Routes.PARENT_DASHBOARD) {
                        popUpTo(Routes.PARENT_DASHBOARD) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.VIEW_CHORES) { PlaceholderScreen("View Chores (Coming Soon)") }
        composable(Routes.EDIT_CHORES) { PlaceholderScreen("Edit Chores (Coming Soon)") }
        composable(Routes.CHORE_APPROVALS) { PlaceholderScreen("Chore Approvals (Coming Soon)") }
        composable(Routes.REWARDS) { PlaceholderScreen("Rewards (Coming Soon)") }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title)
    }
}