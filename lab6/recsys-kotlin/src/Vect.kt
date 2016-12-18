object Vect {
    fun inner(a: FloatArray, b: FloatArray): Float {
        if (a.size != b.size)
            throw IllegalArgumentException("a.size != b.size")
        return a.zip(b).map { it.first * it.second }.sum()
    }

    fun multiply(num: Float, vec: FloatArray) = vec.map { it * num }.toFloatArray()

    fun subtract(a: FloatArray, b: FloatArray): FloatArray {
        if (a.size != b.size)
            throw IllegalArgumentException("a.size != b.size")
        return a.zip(b).map { it.first - it.second }.toFloatArray()
    }

    fun add(a: FloatArray, b: FloatArray): FloatArray {
        if (a.size != b.size)
            throw IllegalArgumentException("a.size != b.size")
        return a.zip(b).map { it.first + it.second }.toFloatArray()
    }
}