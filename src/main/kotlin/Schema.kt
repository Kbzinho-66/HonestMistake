import org.jetbrains.kotlinx.dataframe.api.column

val homeTeam by column<String>("HomeTeam")
val seq by column<Int>("Seq")
val awayTeam by column<String>("AwayTeam")

val fullTimeHomeGoals by column<Double>("FTHG")
val fullTimeAwayGoals by column<Double>("FTAG")

val fullTimeResult by column<String>("FTR")

val halfTimeHomeGoals by column<Double>("HTHG")
val halfTimeAwayGoals by column<Double>("HTAG")

val halfTimeResult by column<String>("HTR")

val homeTeamShots by column<Double>("HS")
val awayTeamShots by column<Double>("AS")

val homeTeamShotsOnTarget by column<Double>("HST")
val awayTeamShotsOnTarget by column<Double>("AST")

val homeTeamCorners by column<Double>("HC")
val awayTeamCorners by column<Double>("AC")

val homeTeamFouls by column<Double>("HF")
val awayTeamFouls by column<Double>("AF")

val homeTeamYellowCards by column<Double>("HY")
val awayTeamYellowCards by column<Double>("AY")

val homeTeamRedCards by column<Double>("HR")
val awayTeamRedCards by column<Double>("AR")

val numericColumns = arrayOf(
    homeTeamShots,
    awayTeamShots,
    homeTeamShotsOnTarget,
    awayTeamShotsOnTarget,
    homeTeamCorners,
    awayTeamCorners,
    homeTeamFouls,
    awayTeamFouls,
    homeTeamYellowCards,
    awayTeamYellowCards,
    homeTeamRedCards,
    awayTeamRedCards
)