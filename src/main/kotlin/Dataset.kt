
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarrayOf
import org.jetbrains.kotlinx.multik.api.toNDArray
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array

typealias Vector = D1Array<Double>
typealias LabeledData = List<Pair<Vector, Vector>>

data class Dataset(val input: String) {
    private val data: DataFrame<Any?>

    init {
        val original = DataFrame
            .readCSV(input, delimiter = ';')
            .remove { seq..fullTimeAwayGoals and halfTimeHomeGoals..halfTimeResult  }
            .convert { colsOf<Int>() }
            .with { it.toDouble() }

        data = original.normalized()
        println("Valores após a normalização:")
        data.print()
    }

    fun stratifiedShuffledSplit(trainingRatio: Double = .8): Pair<LabeledData, LabeledData> {
        // Randomizar tudo uma vez pra ser sempre um conjunto diferente
        val shuffled = data.shuffle()

        println("Total de amostras = ${shuffled.rowsCount()}")
        println(shuffled.classDistribution())

        val homeWins = shuffled.filter { it[fullTimeResult] == "H" }
        val draw     = shuffled.filter { it[fullTimeResult] == "D" }
        val awayWins = shuffled.filter { it[fullTimeResult] == "A" }

        val testRatio = 1.0 - trainingRatio

        val training = homeWins.take(trainingRatio)
            .concat(draw.take(trainingRatio))
            .concat(awayWins.take(trainingRatio))
            .shuffle() // Randomizar uma segunda vez pra misturar as classes

        println("Amostras de treinamento: ${training.rowsCount()}")
        println(training.classDistribution())

        val test = homeWins.takeLast(testRatio)
            .concat(draw.takeLast(testRatio))
            .concat(awayWins.takeLast(testRatio))
            .shuffle()

        println("Amostras de teste: ${test.rowsCount()}")
        println(test.classDistribution())

        return Pair(training.labeled(), test.labeled())
    }

    private fun DataFrame<Any?>.labeled(): LabeledData {
        // Converter o resultado para colunas dummy
        val labels = this
            .select { fullTimeResult }
            .map { row ->
                when (row[fullTimeResult]) {
                    "H" -> mk.ndarrayOf(1.0, 0.0, 0.0)
                    "D" -> mk.ndarrayOf(0.0, 1.0, 0.0)
                    "A" -> mk.ndarrayOf(0.0, 0.0, 1.0)
                    else -> mk.ndarrayOf(0.0, 0.0, 0.0)
                }
            }

        // Transformar o dataframe em uma lista de vetores
        val data = this
            .remove { fullTimeResult }
            .map { row -> row.values().map { it as Double } }
            .map { it.toNDArray() }

        return data.zip(labels)
    }

    private fun DataFrame<Any?>.normalized(): DataFrame<Any?> {
        var normalized = this.copy()
        numericColumns.forEach { col ->
            val max = normalized[col].max()
            val min = normalized[col].min()
            normalized = normalized.update(col).with { x -> (x - min) / (max - min) }
        }

        return normalized
    }

    private fun DataFrame<Any?>.take(n: Double): DataFrame<Any?> {
        val cutoff = (this.rowsCount() * n).toInt()
        return this.take(cutoff)
    }

    private fun DataFrame<Any?>.takeLast(n: Double): DataFrame<Any?> {
        val cutoff = (this.rowsCount() * n).toInt()
        return this.takeLast(cutoff)
    }

    private fun <T> DataFrame<T>.classDistribution(): Any {
        print("Proporção de classes: ")
        val total = this.rowsCount().toDouble()
        val homeWins = this.count { it[fullTimeResult] == "H" }.toDouble() * 100.0 / total
        val draws = this.count { it[fullTimeResult] == "D" }.toDouble() * 100.0 / total
        val awayWins = this.count { it[fullTimeResult] == "A" }.toDouble() * 100.0 / total

        return String.format("H = %.2f%%  D = %.2f%%  A = %.2f%%\n", homeWins, draws, awayWins)
    }

}

