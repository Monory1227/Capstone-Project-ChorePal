package com.example.chorepalcowboysg6

data class ChildPerformanceStats(
    val childUid: String = "",
    val childName: String = "",
    val assigned: Int = 0,
    val completed: Int = 0,
    val approved: Int = 0,
    val rejected: Int = 0,
    val completionRate: Double = 0.0,
    val approvalRate: Double = 0.0,
    val averageCompletionHours: Double = 0.0
)

data class HouseholdPerformanceStats(
    val assigned: Int = 0,
    val completed: Int = 0,
    val approved: Int = 0,
    val rejected: Int = 0,
    val completionRate: Double = 0.0
)

data class ChildAIInsight(
    val childUid: String = "",
    val summary: String = "",
    val strengths: List<String> = emptyList(),
    val improvementAreas: List<String> = emptyList(),
    val recommendations: List<String> = emptyList()
)

data class AIInsightsReport(
    val householdStats: HouseholdPerformanceStats =
        HouseholdPerformanceStats(),

    val childStats: List<ChildPerformanceStats> =
        emptyList(),

    val householdInsight: String = "",

    val householdRecommendations: List<String> =
        emptyList(),

    val childInsights: List<ChildAIInsight> =
        emptyList(),

    val comparisonInsight: String = "",

    val comparisonRecommendations: List<String> =
        emptyList()
)

enum class InsightTimePeriod(
    val label: String,
    val days: Int?
) {
    LAST_7_DAYS("Last 7 Days", 7),
    LAST_30_DAYS("Last 30 Days", 30),
    LAST_90_DAYS("Last 90 Days", 90),
    ALL_TIME("All Time", null)
}

