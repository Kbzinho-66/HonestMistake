
import kotlin.math.exp
import kotlin.math.max

enum class ActivationFunction {
    Identity {
        override fun apply(x: Double): Double = x
    },
    ReLu {
        override fun apply(x: Double): Double = max(0.0, x)
    },
    Sigmoid {
        override fun apply(x: Double): Double = 1 / (1 + exp(-x))
    },
    TanH {
        override fun apply(x: Double): Double = (exp(x) - exp(-x)) / (exp(x) + exp(-x))
    };

    abstract fun apply(x: Double): Double
}
