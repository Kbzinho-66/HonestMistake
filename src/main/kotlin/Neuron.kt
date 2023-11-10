
import org.jetbrains.kotlinx.multik.api.d1array
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.random.Random

data class Neuron(
    val inputSize: Int,
    val outputSize: Int,
    val activationFunction: ActivationFunction,
) {
    private var weights: Vector = mk.d1array(inputSize) { Random.nextDouble(.0, .001) }
    private var output: Double = 0.0
    private var error: Double = 0.0

    /**
     * "Processa" um vetor coluna de entrada. Ou seja, soma o produto de cada entrada por seu peso,
     * aplica a função de ativação e retorna seu valor de saída.
     */
    fun process(input: Vector): Double =
        activationFunction.apply( input dot weights ).also { output = it }

    /**
     * Calcula o seu erro e retorna o quanto cada neurônio da camada anterior influenciou nesse erro.
     */
    fun backpropagate(signal: Double): Vector {
        error = output * ( 1 - output ) * signal
        return error * weights
    }

    fun readjustWeights(input: Vector, learningRate: Double) {
        weights += learningRate * input * error
    }
}