package org.example

fun main() {
    val geno = Geno()

    println(geno.calBothSeg("./src/main/kotlin/data/x.s", "./src/main/kotlin/data/y.s"))
    println(geno.calBothSeg("./src/main/kotlin/data/testfile_a.s", "./src/main/kotlin/data/testfile_b.s"))

    println(geno.calBothFun("./src/main/kotlin/data/x.f", "./src/main/kotlin/data/y.f"))
    println(geno.calBothFun("./src/main/kotlin/data/testfile_a.f", "./src/main/kotlin/data/testfile_b.f"))

    println(geno.calSegAndFun("./src/main/kotlin/data/x.s", "./src/main/kotlin/data/y.f"))
    println(geno.calSegAndFun("./src/main/kotlin/data/testfile_a.s", "./src/main/kotlin/data/testfile_b.f"))
}
