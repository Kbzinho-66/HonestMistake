import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.operations.times

fun main() {
    val df = DataFrame.read("src/main/resources/Dataset_futebol.csv", delimiter = ';')
    df.print()

    val a = mk.ndarray(mk[1, 2, 3])
    val b = mk.ndarray(mk[3, 0, 1])
    println(a * b)
}