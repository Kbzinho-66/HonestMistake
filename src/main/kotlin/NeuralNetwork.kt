
import org.jetbrains.kotlinx.multik.ndarray.operations.map
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.sum


data class NeuralNetwork(
    val learningRate: Double = .25,
) {
    private var layers = mutableListOf<Layer>()
    private var inputSize: Int = 0
    private var outputSize: Int = 0

    fun addLayer(layer: Layer) {
        when (layer) {
            is InputLayer -> inputSize = layer.outputSize
            is OutputLayer -> outputSize = layer.outputSize
            else -> {}
        }
        layers.add(layer)
    }

    fun train(data: LabeledData, epochLimit: Int) {
        if (inputSize == 0 || layers.first() !is InputLayer) {
            println("A primeira camada deve ser uma camada de entrada!")
            return
        } else if (outputSize == 0 || layers.last() !is OutputLayer) {
            println("A última camada deve ser uma camada de saída!")
            return
        }

        var epoch = 0
        var patience = 3
        var lastEpochError = Double.MAX_VALUE
        var epochError = 0.0
        while (epoch < epochLimit) {
            epochError = 0.0
            data.forEach { (input, expectedOutput) ->
                var actualOutput = input
                for (layer in layers) {
                    actualOutput = layer.process(actualOutput)
                }

                var errorSignal = expectedOutput - actualOutput
                epochError += errorSignal.map { it * it }.sum()

                for (layer in layers.reversed()) {
                    errorSignal = layer.backpropagate(errorSignal)
                }

                for (layer in layers) {
                    layer.readjustWeights(learningRate)
                }
            }

            epochError /= data.size
            if (lastEpochError - epochError < 1e-8) {
                if (patience == 0) break else patience--
            } else {
                patience = 3
                println("Erro após a época $epoch = $epochError")
                lastEpochError = epochError
                epochError = 0.0
            }
            epoch++
        }

        println("Convergiu depois de $epoch épocas")
    }

    fun predict(data: LabeledData): List<Vector> {
        return data.map { (input, _) ->
            var actualOutput = input
            for (layer in layers) {
                actualOutput = layer.process(actualOutput)
            }
            actualOutput
        }
    }
}