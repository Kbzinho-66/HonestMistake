
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.forEachIndexed

fun main() {
    val dataset = Dataset("src/main/resources/Dataset_futebol.csv")
    val (trainingData, testData) = dataset.stratifiedShuffledSplit(0.8)

    println("Dados de treinamento:")
    trainingData.take(5).forEach { (input, outcome) ->
        println("Input = $input\nResultado = $outcome\n")
    }

    val model = NeuralNetwork(learningRate = 0.25)
    model.addLayer(InputLayer(12))
    model.addLayer(HiddenLayer(inputSize = 12, outputSize = 15))
    model.addLayer(HiddenLayer(inputSize = 15, outputSize = 15))
    model.addLayer(OutputLayer(inputSize = 15, outputSize = 3))
    model.train(trainingData, 300)

    val predictions = model.predict(testData)
    confusionMatrix(testData, predictions)
}

fun confusionMatrix(df: LabeledData, pred: List<Vector>) {
    val HOME_WINS = 0
    val DRAW = 1
    val AWAY_WINS = 2

    // Pegar a classe esperada
    val expected = df.map { (_, output) ->
        when {
            output[0] == 1.0 -> HOME_WINS
            output[2] == 1.0 -> AWAY_WINS
            else -> DRAW
        }
    }

    // Pegar a classe estimada
    val gotten = pred.map { vec ->
        var maxValue = 0.0
        var maxIndex = -1

        vec.forEachIndexed { index, v ->
            if (v > maxValue) {
                maxValue = v
                maxIndex = index
            }
        }

        maxIndex
    }

    val count = Array(3) { IntArray(3) }

    expected.zip(gotten)
        .forEach { (expected, got) ->
            count[expected][got]++
        }

    println(String.format("|%-15s|%20s|", "", "Classificou como"))
    println(String.format("|%-15s|%6s|%6s|%6s|", "", "Home", "Draw", "Away"))
    count.forEachIndexed { index, arr ->
        val label = when(index) {
            HOME_WINS -> "Home"
            AWAY_WINS -> "Away"
            else      -> "Draw"
        }

        print(String.format("|%-15s|", "Esperava $label"))
        arr.forEach { print(String.format("%6d|", it)) }
        println()
    }

    val accuracy =
        (count[HOME_WINS][HOME_WINS] + count[DRAW][DRAW] + count[AWAY_WINS][AWAY_WINS]) / (count.sumOf{ it.sum() }).toDouble()
    println("Acurácia = $accuracy")

    for (outcome in 0..2) {
        val precision = (count[outcome][outcome]) / (count.sumOf { it[outcome] }).toDouble()
        val recall = (count[outcome][outcome]) / (count[outcome].sum()).toDouble()
        val f1Score = (2 * recall * precision) / (recall + precision)

        val label = when(outcome) {
            HOME_WINS -> "Home"
            AWAY_WINS -> "Away"
            else      -> "Draw"
        }

        println("Classe $label: Precisão = $precision | Recall = $recall | F1-Score = $f1Score")
    }
}