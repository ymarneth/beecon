package sve2.beecon.hive

enum class AggregationInterval {
    MINUTE,
    FIVE_MINUTES,
    HALF_HOUR,
    HOUR,
    DAY,
    WEEK,
    MONTH,
    SIX_MONTHS;

    companion object {
        fun fromString(value: String): AggregationInterval? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}