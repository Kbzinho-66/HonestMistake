
import org.jetbrains.kotlinx.multik.api.d1array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.operations.forEachIndexed
import org.jetbrains.kotlinx.multik.ndarray.operations.plusAssign

sealed class Layer(
    private val inputSize: Int,
    val outputSize: Int,
    private val neurons: Array<Neuron>
) {
    private var prevInput: Vector? = null

    open fun process(input: Vector): Vector {
        prevInput = input

        val output = mk.d1array(outputSize) { i ->
            neurons[i].process(input)
        }

        return output
    }

    open fun backpropagate(currentLayerErrorSignal: Vector): Vector {
        val prevLayerErrorSignal = mk.zeros<Double>(inputSize)

        currentLayerErrorSignal.forEachIndexed { i, signal ->
            val sig = neurons[i].backpropagate(signal)
            prevLayerErrorSignal += sig
        }

        return prevLayerErrorSignal
    }

    open fun readjustWeights(learningRate: Double) {
        neurons.forEach { neuron ->
            prevInput?.let { input -> neuron.readjustWeights(input, learningRate) }
        }
    }
}

class InputLayer(
    inputSize: Int,
) : Layer(inputSize, inputSize, neurons = emptyArray()) {
    override fun process(input: Vector): Vector = input
    override fun backpropagate(currentLayerErrorSignal: Vector): Vector = currentLayerErrorSignal
    override fun readjustWeights(learningRate: Double) {}
}

class HiddenLayer(
    inputSize: Int,
    outputSize: Int,
) : Layer(
    inputSize,
    outputSize,
    neurons = Array(outputSize) { Neuron(inputSize, outputSize, ActivationFunction.Sigmoid) })

class OutputLayer(
    inputSize: Int,
    outputSize: Int
) : Layer(
    inputSize,
    outputSize,
    neurons = Array(outputSize) { Neuron(inputSize, outputSize, ActivationFunction.Sigmoid) })