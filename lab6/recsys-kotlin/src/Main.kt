import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

val LINES_COUNT = 99072113;

data class Entry(val user: Int, val item: Int, val rating: Int)

fun readData(reader:BufferedReader) : Array<Entry> {
    println("reading...")
    var count = 0
    val data : Array<Entry?> = kotlin.arrayOfNulls<Entry>(LINES_COUNT)
    //val data = Array<Entry>(LINES_COUNT, {Entry(0,0,0)})
    var line = reader.readLine()
    while (true) {
        line = reader.readLine()
        if (line == null) {
            break;
        }
        val split = line.split(",".toRegex()).map(String::toInt)
        data[count] = Entry(split[0], split[1], split[2])
        count ++
        if (count % 10000000 == 0)
            println(count)
    }
    return data.map { it!! }.toTypedArray()
}

val qi = hashMapOf<Int, FloatArray>()
val pu = hashMapOf<Int, FloatArray>()
val bu = hashMapOf<Int, Float>()
val bi = hashMapOf<Int, Float>()

var mu = 0.0F

fun predict(user: Int, item: Int) = mu + bu[user]!! + bi[item]!! + (Vect.inner(pu[user]!!, qi[item]!!))

fun error(user: Int, item: Int, answer: Float)
        = answer - (mu + bu[user]!! + bi[item]!! + Vect.inner(pu[user]!!, qi[item]!!))

fun toAns(ans: Float) = Math.min(Math.max(Math.round(ans), 1), 5)

fun gaussian(size : Int) : FloatArray {
    val rnd = Random()
    return (1..size).map { rnd.nextGaussian().toFloat() / size }.toFloatArray()
}

val GAMMA = 0.005F
val LAMBDA = 0.02F
val FEATURES = 40
val ITERATIONS = 10

fun iteration(data: Array<Entry>) {
    var count = 0
    for ((user, item, rating) in data) {
        count += 1
        if (count % 10000000 == 0)
            println(count)
        val err = error(user, item, rating.toFloat())
        bu[user] = bu[user]!! + GAMMA * (err - LAMBDA * bu.getOrDefault(user, 0.0F))
        bi[item] = bi[item]!! + GAMMA * (err - LAMBDA * bi.getOrDefault(item, 0.0F))
        val qi_tmp = qi[item]!!.copyOf()
        val pu_tmp = pu[user]!!.copyOf()
        qi[item] = Vect.add(qi_tmp,
                Vect.multiply(GAMMA, Vect.subtract(Vect.multiply(err, pu_tmp), Vect.multiply(LAMBDA, qi_tmp))))
        pu[user] = Vect.add(pu_tmp,
                Vect.multiply(GAMMA, Vect.subtract(Vect.multiply(err, qi_tmp), Vect.multiply(LAMBDA, pu_tmp))))
    }
}

data class AnsEntry(val id: Int, val prediction: Int)

fun calc_answer(testFileReader: BufferedReader) : List<AnsEntry> {
    var line = testFileReader.readLine()
    val answer = mutableListOf<AnsEntry>()
    while (true) {
        line = testFileReader.readLine()
        if (line == null) {
            break;
        }
        val split = line.split(",".toRegex()).map(String::toInt)
        answer += AnsEntry(split[0], toAns(predict(split[1], split[2])))
    }
    return answer
}

fun main(args: Array<String>) {
    val data = readData(BufferedReader(FileReader("/home/kirill/ssd-pool/train.csv")))
    while (true) {
        println("how many iterations?")
        val iter = readLine()!!.toInt()
        println("start $iter iterations")
        for (i in 1..iter) {
            iteration(data)
            println("iteration: $i")
            val ans = calc_answer(BufferedReader(FileReader("/home/kirill/Dropbox/study/IFMO/4year/ML/IFMO-ML-2016/lab6/test-ids.csv")))
            val fileName = "answer_${FEATURES}_$ITERATIONS.csv"
            val res = File(fileName)
            val sb = StringBuilder().append("Id,Prediction\n")
            for (e in ans) {
                sb.append("${e.id},${e.prediction}\n")
            }
            res.writeText(sb.toString())
            println("$fileName written")
        }
    }
}
