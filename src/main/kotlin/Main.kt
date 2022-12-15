import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.math.cos
import kotlin.math.exp

fun main() {
    val n = 100
    val interval = listOf(0.0, 5.0)
    val grid = makeGrid(interval, n)

    val g = { x: Double ->
        exp(x) * cos(x)
    }

    val y0 = 0.0
    val values = fourthDegreeScheme(interval, n, y0, g, grid)

    val treeValues = TreeMap(values)
    createGraphics(xs = ArrayList(treeValues.keys), ys = ArrayList(treeValues.values))
}

fun fourthDegreeScheme(
    interval: List<Double>,
    n: Int,
    y0: Double,
    g: (Double) -> Double,
    grid: List<Double>
): Map<Double, Double> {
    val values = ConcurrentHashMap<Double, Double>()
    val h = (interval.last() - interval.first()) / n

    values[grid.first()] = y0
    var yi1 = y0
    var yi2 = y0
    for (i in 1..n) {
        val x = grid[i]

        val y = if (i == 1) {
            (8 * g(x) + 5 * g(x - h) + g(x + h)) * h / 12 + yi1
        } else {
            (g(x + h) + 4 * g(x) + g(x - h)) * h / 3 + yi2
        }

        values[x] = y
        yi2 = yi1
        yi1 = y
    }

    return values
}

fun secondDegreeScheme(
    interval: List<Double>,
    n: Int,
    y0: Double,
    g: (Double) -> Double,
    grid: List<Double>
): Map<Double, Double> {
    val values = ConcurrentHashMap<Double, Double>()
    val h = (interval.last() - interval.first()) / n

    var yi1 = y0
    var yi2 = y0
    for (i in 1..n) {
        val x = grid[i]
        val y = if (i == 1) {
            (8 * g(x) + 5 * g(x - h) + g(x + h)) * h / 12 + yi1
        } else {
            (g(x - h) + g(x + h)) * h + yi2
        }
        values[x] = y

        yi2 = yi1
        yi1 = y
    }

    return values
}

fun makeGrid(interval: List<Double>, intervalsCnt: Int): List<Double> {
    val points = ArrayList<Double>()

    val startPoint = interval.first()
    val length = interval.last() - interval.first()
    var cnt = 1
    points.add(interval.first())

    do {
        val nextPoint = startPoint + length * cnt / (intervalsCnt)
        points.add(nextPoint)
        cnt += 1
    } while (nextPoint != interval.last())

    return points
}

fun createGraphics(xs: List<Double>, ys: List<Double>) {
    val templateReader = DataInputStream(FileInputStream("src/main/resources/template.html"))
    val html = File("src/main/resources/graphics.html")
    html.createNewFile()

    val htmlWriter = DataOutputStream(FileOutputStream(html))
    val htmlStr = String(templateReader.readAllBytes())
    val newStr = htmlStr.replace("%x", xs.toString()).replace("%y", ys.toString())
    htmlWriter.write(newStr.toByteArray())
}

// показать порядок аппроксимации
// правило рунге