package org.example

import java.io.File

/**
 * Geno class provides methods to calculate overlaps and correlations between genomic segments and functions.
 */
class Geno {
    private data class Seg(val start: Int, val end: Int)

    // Reads a .s file and returns a list of Seg objects.
    private fun readSegFile(filePath: String): List<Seg> {
        val lines = File(filePath).readLines()

        return lines.map { line ->
            val parts = line.split(Regex("\\s+"))
            if (parts.size != 2) {
                throw IllegalArgumentException("Invalid line format: $line")
            }
            val start = parts[0].toIntOrNull() ?: throw IllegalArgumentException("Invalid start position: ${parts[0]}")
            val end = parts[1].toIntOrNull() ?: throw IllegalArgumentException("Invalid end position: ${parts[1]}")

            require(start < end) { "Start position must be less than end position: $line" }
            Seg(start, end)
        }
    }

    // Reads a .f file and returns a list of Double values.
    private fun readFunFile(filePath: String): List<Double> {
        val lines = File(filePath).readLines()

        return lines.map { line ->
            val value = line.trim().toDoubleOrNull() ?: throw IllegalArgumentException("Invalid value: $line")
            value
        }
    }

    // Calculates the total overlap length between two segment files.
    fun calBothSeg(file1: String, file2: String): Long {
        val segs1 = readSegFile(file1)
        val segs2 = readSegFile(file2)

        var overlapCnt = 0L
        var i = 0
        var j = 0

        while (i < segs1.size && j < segs2.size) {
            val s = maxOf(segs1[i].start, segs2[j].start)
            val e = minOf(segs1[i].end, segs2[j].end)

            if (s < e) {
                overlapCnt += (e - s)
            }
            if (segs1[i].end < segs2[j].end) {
                i++
            } else {
                j++
            }
        }

        return overlapCnt
    }

    // Calculates the Pearson correlation coefficient between two function files.
    fun calBothFun(file1: String, file2: String): Double {
        fun List<Double>.toMean(): Double {
            // Size will always be > 0
            return this.sum() / this.size
        }

        fun List<Double>.toSqrtSquareDiff(mean: Double): Double {
            val squareDiffs = this.map { (it - mean) * (it - mean) }
            return Math.sqrt(squareDiffs.sum())
        }

        val funs1 = readFunFile(file1)
        val funs2 = readFunFile(file2)
        require(funs1.size == funs2.size) { "Function files must have the same size." }

        val mean1 = funs1.toMean()
        val mean2 = funs2.toMean()

        val sqrtSquareDiff1 = funs1.toSqrtSquareDiff(mean1)
        val sqrtSquareDiff2 = funs2.toSqrtSquareDiff(mean2)

        if (sqrtSquareDiff1 == 0.0 || sqrtSquareDiff2 == 0.0) {
            throw IllegalArgumentException("Standard deviation is zero, cannot calculate correlation.")
        }

        var total = 0.0
        // Both lists have the same size.
        for (i in funs1.indices) {
            total += (funs1[i] - mean1) * (funs2[i] - mean2)
        }

        return total / (sqrtSquareDiff1 * sqrtSquareDiff2)
    }

    // Calculates the average function value within the segments defined in the segment file.
    fun calSegAndFun(file1: String, file2: String): Double {
        val segs = readSegFile(file1)
        val funs = readFunFile(file2)

        var sum = 0.0
        var count = 0

        var segIndex = 0

        for (i in funs.indices) {
            while (segIndex < segs.size && i >= segs[segIndex].end) {
                segIndex++
            }

            if (segIndex < segs.size && i >= segs[segIndex].start && i < segs[segIndex].end) {
                sum += funs[i]
                count++
            }
        }

        if (count == 0) {
            throw IllegalArgumentException("No function values fall within the segments.")
        }

        return sum / count
    }
}
