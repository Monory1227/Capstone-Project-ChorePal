package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


private const val HOUSEHOLD_OPTION =
    "__HOUSEHOLD__"

private const val COMPARISON_OPTION =
    "__COMPARISON__"


@Composable
fun AIInsightsScreen(
    repository: FirebaseRepository,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {

    val scope =
        rememberCoroutineScope()

    var report by remember {
        mutableStateOf<AIInsightsReport?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var insightExpanded by remember {
        mutableStateOf(false)
    }

    val selectedOptions =
        remember {
            mutableStateListOf<String>()
        }


    /*
    =========================================================
    LOAD AI INSIGHTS
    =========================================================

    Time Period selector has been removed.

    AI Insights now always uses ALL TIME.
    */

    fun loadInsights() {

        scope.launch {

            isLoading = true
            errorMessage = null

            repository
                .loadAIInsights(
                    InsightTimePeriod.ALL_TIME
                )
                .onSuccess {

                    report = it

                    /*
                    First load:
                    show Household + all children + Comparison.
                    */

                    if (
                        selectedOptions.isEmpty()
                    ) {

                        selectedOptions.add(
                            HOUSEHOLD_OPTION
                        )

                        it.childStats.forEach { child ->

                            selectedOptions.add(
                                child.childUid
                            )
                        }

                        if (
                            it.childStats.size > 1
                        ) {

                            selectedOptions.add(
                                COMPARISON_OPTION
                            )
                        }
                    }
                }
                .onFailure {

                    errorMessage =
                        it.message
                            ?: "Unable to load AI Insights."
                }

            isLoading = false
        }
    }


    /*
    Load once when screen opens.
    */

    LaunchedEffect(Unit) {

        loadInsights()
    }


    Scaffold { padding ->

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(
                        horizontal = 16.dp
                    )
        ) {

            /*
            =====================================================
            HEADER
            =====================================================
            */

            Spacer(
                Modifier.height(8.dp)
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
            ) {

                /*
                BACK BUTTON
                */

                IconButton(
                    onClick = onBack,
                    modifier =
                        Modifier.align(
                            Alignment.CenterStart
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons
                                .AutoMirrored
                                .Filled
                                .ArrowBack,

                        contentDescription =
                            "Back",

                        modifier =
                            Modifier.size(
                                34.dp
                            )
                    )
                }


                /*
                CHOREPAL LOGO
                */

                Image(
                    painter =
                        painterResource(
                            id =
                                R.drawable
                                    .chorepal_logo
                        ),

                    contentDescription =
                        "ChorePal Logo",

                    modifier =
                        Modifier
                            .align(
                                Alignment.Center
                            )
                            .width(
                                280.dp
                            )
                            .height(
                                110.dp
                            ),

                    contentScale =
                        ContentScale.Fit
                )


                /*
                LOGOUT BUTTON
                */

                IconButton(
                    onClick = onLogout,
                    modifier =
                        Modifier.align(
                            Alignment.CenterEnd
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons
                                .AutoMirrored
                                .Filled
                                .ExitToApp,

                        contentDescription =
                            "Logout",

                        modifier =
                            Modifier.size(
                                34.dp
                            )
                    )
                }
            }


            /*
            =====================================================
            TITLE
            =====================================================
            */

            Text(
                text =
                    "AI Insights",

                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,

                fontWeight =
                    FontWeight.Bold,

                modifier =
                    Modifier.align(
                        Alignment.CenterHorizontally
                    )
            )

            Spacer(
                Modifier.height(16.dp)
            )


            /*
            =====================================================
            VIEW INSIGHTS FOR
            =====================================================
            */

            Text(
                text =
                    "View Insights For",

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(6.dp)
            )


            /*
            =====================================================
            MULTI-SELECT DROPDOWN
            =====================================================
            */

            Box {

                OutlinedButton(
                    onClick = {

                        insightExpanded =
                            true
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(

                        if (
                            selectedOptions.isEmpty()
                        ) {

                            "Select insights"

                        } else {

                            "${selectedOptions.size} selected"
                        }
                    )
                }


                DropdownMenu(
                    expanded =
                        insightExpanded,

                    onDismissRequest = {

                        insightExpanded =
                            false
                    }
                ) {


                    /*
                    HOUSEHOLD
                    */

                    MultiSelectInsightItem(
                        label =
                            "Household",

                        checked =
                            HOUSEHOLD_OPTION in
                                    selectedOptions,

                        onCheckedChange = {

                            toggleSelection(
                                selectedOptions,
                                HOUSEHOLD_OPTION
                            )
                        }
                    )


                    /*
                    CHILDREN
                    */

                    report
                        ?.childStats
                        ?.forEach { child ->

                            MultiSelectInsightItem(
                                label =
                                    child.childName,

                                checked =
                                    child.childUid in
                                            selectedOptions,

                                onCheckedChange = {

                                    toggleSelection(
                                        selectedOptions,
                                        child.childUid
                                    )
                                }
                            )
                        }


                    /*
                    COMPARISON
                    */

                    if (
                        (
                                report
                                    ?.childStats
                                    ?.size
                                    ?: 0
                                ) > 1
                    ) {

                        MultiSelectInsightItem(
                            label =
                                "Comparison",

                            checked =
                                COMPARISON_OPTION in
                                        selectedOptions,

                            onCheckedChange = {

                                toggleSelection(
                                    selectedOptions,
                                    COMPARISON_OPTION
                                )
                            }
                        )
                    }
                }
            }


            Spacer(
                Modifier.height(16.dp)
            )


            /*
            =====================================================
            CONTENT
            =====================================================
            */

            when {

                /*
                LOADING
                */

                isLoading -> {

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }


                /*
                ERROR
                */

                errorMessage != null -> {

                    Column {

                        Text(
                            text =
                                "AI Insights could not be loaded.",

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                errorMessage
                                    .orEmpty()
                        )

                        Spacer(
                            Modifier.height(12.dp)
                        )

                        Button(
                            onClick = {

                                loadInsights()
                            }
                        ) {

                            Text(
                                "Try Again"
                            )
                        }
                    }
                }


                /*
                REPORT
                */

                else -> {

                    val currentReport =
                        report

                    if (
                        currentReport != null
                    ) {

                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .verticalScroll(
                                        rememberScrollState()
                                    )
                        ) {


                            /*
                            ================================
                            HOUSEHOLD
                            ================================
                            */

                            if (
                                HOUSEHOLD_OPTION in
                                selectedOptions
                            ) {

                                HouseholdInsightCard(
                                    report =
                                        currentReport
                                )

                                Spacer(
                                    Modifier.height(
                                        16.dp
                                    )
                                )
                            }


                            /*
                            ================================
                            CHILD REPORTS
                            ================================
                            */

                            currentReport
                                .childStats
                                .filter { child ->

                                    child.childUid in
                                            selectedOptions
                                }
                                .forEach { child ->

                                    val aiInsight =
                                        currentReport
                                            .childInsights
                                            .firstOrNull {

                                                it.childUid ==
                                                        child.childUid
                                            }

                                    ChildInsightCard(
                                        stats =
                                            child,

                                        aiInsight =
                                            aiInsight
                                    )

                                    Spacer(
                                        Modifier.height(
                                            16.dp
                                        )
                                    )
                                }


                            /*
                            ================================
                            COMPARISON
                            ================================
                            */

                            if (
                                COMPARISON_OPTION in
                                selectedOptions &&
                                currentReport
                                    .childStats
                                    .size > 1
                            ) {

                                ComparisonInsightCard(
                                    report =
                                        currentReport
                                )

                                Spacer(
                                    Modifier.height(
                                        24.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


/*
=========================================================
TOGGLE MULTI SELECT
=========================================================
*/

private fun toggleSelection(
    selections: MutableList<String>,
    value: String
) {

    if (
        value in selections
    ) {

        selections.remove(
            value
        )

    } else {

        selections.add(
            value
        )
    }
}


/*
=========================================================
MULTI SELECT MENU ITEM
=========================================================
*/

@Composable
private fun MultiSelectInsightItem(
    label: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {

    DropdownMenuItem(

        text = {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Checkbox(
                    checked =
                        checked,

                    onCheckedChange = {

                        onCheckedChange()
                    }
                )

                Text(
                    text =
                        label
                )
            }
        },

        onClick = {

            onCheckedChange()
        }
    )
}


/*
=========================================================
HOUSEHOLD CARD
=========================================================
*/

@Composable
private fun HouseholdInsightCard(
    report: AIInsightsReport
) {

    val stats =
        report.householdStats

    InsightCard(
        title =
            "Household"
    ) {

        StatLine(
            "Chores Assigned",
            stats.assigned.toString()
        )

        StatLine(
            "Chores Completed",
            stats.completed.toString()
        )

        StatLine(
            "Chores Approved",
            stats.approved.toString()
        )

        StatLine(
            "Completion Rate",
            "${stats.completionRate.roundToInt()}%"
        )

        Spacer(
            Modifier.height(12.dp)
        )

        SectionTitle(
            "AI Household Insight"
        )

        Text(
            report
                .householdInsight
                .ifBlank {

                    "No AI household insight available."
                }
        )

        if (
            report
                .householdRecommendations
                .isNotEmpty()
        ) {

            Spacer(
                Modifier.height(12.dp)
            )

            SectionTitle(
                "Recommendations"
            )

            RecommendationList(
                report
                    .householdRecommendations
            )
        }
    }
}


/*
=========================================================
CHILD CARD
=========================================================
*/

@Composable
private fun ChildInsightCard(
    stats: ChildPerformanceStats,
    aiInsight: ChildAIInsight?
) {

    InsightCard(
        title =
            stats.childName
    ) {

        StatLine(
            "Assigned",
            stats.assigned.toString()
        )

        StatLine(
            "Completed",
            stats.completed.toString()
        )

        StatLine(
            "Approved",
            stats.approved.toString()
        )

        StatLine(
            "Rejected",
            stats.rejected.toString()
        )

        StatLine(
            "Completion Rate",
            "${stats.completionRate.roundToInt()}%"
        )

        StatLine(
            "Approval Rate",
            "${stats.approvalRate.roundToInt()}%"
        )

        StatLine(
            "Average Completion Time",
            formatHours(
                stats.averageCompletionHours
            )
        )


        Spacer(
            Modifier.height(12.dp)
        )


        /*
        AI SUMMARY
        */

        SectionTitle(
            "AI Insight"
        )

        Text(
            aiInsight
                ?.summary
                ?.ifBlank {

                    "No AI insight available."
                }
                ?: "No AI insight available."
        )


        /*
        STRENGTHS
        */

        if (
            !aiInsight
                ?.strengths
                .isNullOrEmpty()
        ) {

            Spacer(
                Modifier.height(12.dp)
            )

            SectionTitle(
                "Strengths"
            )

            RecommendationList(
                aiInsight
                    ?.strengths
                    ?: emptyList()
            )
        }


        /*
        IMPROVEMENT
        */

        if (
            !aiInsight
                ?.improvementAreas
                .isNullOrEmpty()
        ) {

            Spacer(
                Modifier.height(12.dp)
            )

            SectionTitle(
                "Improvement Opportunities"
            )

            RecommendationList(
                aiInsight
                    ?.improvementAreas
                    ?: emptyList()
            )
        }


        /*
        RECOMMENDATIONS
        */

        if (
            !aiInsight
                ?.recommendations
                .isNullOrEmpty()
        ) {

            Spacer(
                Modifier.height(12.dp)
            )

            SectionTitle(
                "Suggested Rewards & Motivation"
            )

            RecommendationList(
                aiInsight
                    ?.recommendations
                    ?: emptyList()
            )
        }
    }
}


/*
=========================================================
COMPARISON CARD
=========================================================
*/

@Composable
private fun ComparisonInsightCard(
    report: AIInsightsReport
) {

    InsightCard(
        title =
            "Performance Comparison"
    ) {

        report
            .childStats
            .sortedByDescending {

                it.completionRate
            }
            .forEach { child ->

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 4.dp
                            )
                ) {

                    Text(
                        text =
                            child.childName,

                        modifier =
                            Modifier.weight(1f)
                    )

                    Text(
                        text =
                            "${child.completionRate.roundToInt()}%",

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }


        Spacer(
            Modifier.height(12.dp)
        )


        SectionTitle(
            "AI Comparison"
        )


        Text(
            report
                .comparisonInsight
                .ifBlank {

                    "No comparison insight available."
                }
        )


        if (
            report
                .comparisonRecommendations
                .isNotEmpty()
        ) {

            Spacer(
                Modifier.height(12.dp)
            )

            SectionTitle(
                "Recommendations"
            )

            RecommendationList(
                report
                    .comparisonRecommendations
            )
        }
    }
}


/*
=========================================================
GENERIC INSIGHT CARD
=========================================================
*/

@Composable
private fun InsightCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                18.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        18.dp
                    )
        ) {

            Text(
                text =
                    title,

                style =
                    MaterialTheme
                        .typography
                        .titleLarge,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.height(12.dp)
            )

            content()
        }
    }
}


/*
=========================================================
STAT LINE
=========================================================
*/

@Composable
private fun StatLine(
    label: String,
    value: String
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 3.dp
                )
    ) {

        Text(
            text =
                label,

            modifier =
                Modifier.weight(1f)
        )

        Text(
            text =
                value,

            fontWeight =
                FontWeight.Bold
        )
    }
}


/*
=========================================================
SECTION TITLE
=========================================================
*/

@Composable
private fun SectionTitle(
    text: String
) {

    Text(
        text =
            text,

        fontWeight =
            FontWeight.Bold
    )

    Spacer(
        Modifier.height(5.dp)
    )
}


/*
=========================================================
RECOMMENDATION LIST
=========================================================
*/

@Composable
private fun RecommendationList(
    values: List<String>
) {

    values.forEach { value ->

        Text(
            text =
                "• $value",

            modifier =
                Modifier.padding(
                    vertical = 2.dp
                )
        )
    }
}


/*
=========================================================
FORMAT COMPLETION TIME
=========================================================
*/

private fun formatHours(
    hours: Double
): String {

    if (
        hours <= 0.0
    ) {

        return "N/A"
    }

    return if (
        hours < 24.0
    ) {

        "${hours.roundToInt()} hrs"

    } else {

        val days =
            hours / 24.0

        String.format(
            "%.1f days",
            days
        )
    }
}

