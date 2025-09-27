package org.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.math.abs

class GenoTest {

    // Helper to write a temp file with given content.
    private fun writeTemp(dir: Path, name: String, content: String): Path {
        val p = dir.resolve(name)
        p.writeText(content.trimIndent().trimEnd() + "\n")
        return p
    }

    @Test
    fun `SEG x SEG basic overlap example`(@TempDir tmp: Path) {
        val g = Geno()

        val a = writeTemp(tmp, "a.s", """
            1 2
            3 6
        """.trimIndent())

        val b = writeTemp(tmp, "b.s", """
            0 1
            1 5
        """.trimIndent())

        val overlap = g.calBothSeg(a.toString(), b.toString())
        assertEquals(3, overlap)  // positions 1,3,4
    }

    @Test
    fun `SEG x SEG touching but no overlap`(@TempDir tmp: Path) {
        val g = Geno()

        val a = writeTemp(tmp, "a.s", "100 200")
        val b = writeTemp(tmp, "b.s", "200 300")

        val overlap = g.calBothSeg(a.toString(), b.toString())
        assertEquals(0, overlap)
    }

    @Test
    fun `SEG x SEG full containment`(@TempDir tmp: Path) {
        val g = Geno()

        val a = writeTemp(tmp, "a.s", "10 50")
        val b = writeTemp(tmp, "b.s", "20 30")

        val overlap = g.calBothSeg(a.toString(), b.toString())
        assertEquals(10, overlap)
    }

    @Test
    fun `FUNC x FUNC correlation matches known sample`(@TempDir tmp: Path) {
        val g = Geno()

        // From your example
        val x = writeTemp(tmp, "x.f", """
            10.0
            11.0
            12.0
            13.0
            14.0
            15.0
            16.0
        """)
        val y = writeTemp(tmp, "y.f", """
            10.5
            11.5
            12.0
            13.0
            13.5
            15.0
            14.0
        """)

        val r = g.calBothFun(x.toString(), y.toString())

        // Allow for floating point tiny differences
        assertTrue(abs(r - 0.9452853306994897) < 1e-9, "r=$r")
    }

    @Test
    fun `FUNC x FUNC raises when one series is constant (zero std)`(@TempDir tmp: Path) {
        val g = Geno()

        val x = writeTemp(tmp, "x.f", """
            1
            1
            1
            1
        """)
        val y = writeTemp(tmp, "y.f", """
            1
            2
            3
            4
        """)

        val ex = assertThrows<IllegalArgumentException> {
            g.calBothFun(x.toString(), y.toString())
        }
        assertTrue(ex.message?.contains("Standard deviation is zero", ignoreCase = true) == true)
    }

    @Test
    fun `FUNC x FUNC raises when lengths differ`(@TempDir tmp: Path) {
        val g = Geno()

        val short = writeTemp(tmp, "x.f", """
            1
            2
            3
        """)
        val long = writeTemp(tmp, "y.f", """
            1
            2
            3
            4
        """)

        assertThrows<Exception> {
            g.calBothFun(short.toString(), long.toString())
        }
    }

    @Test
    fun `SEG x FUNC mean matches known sample`(@TempDir tmp: Path) {
        val g = Geno()

        val seg = writeTemp(tmp, "x.s", """
            1 2
            3 6
        """)
        val funFile = writeTemp(tmp, "y.f", """
            10.5
            11.5
            12.0
            13.0
            13.5
            15.0
            14.0
        """)

        val mean = g.calSegAndFun(seg.toString(), funFile.toString())
        assertTrue(abs(mean - 13.25) < 1e-9, "mean=$mean")
    }

    @Test
    fun `SEG x FUNC raises when no covered values`(@TempDir tmp: Path) {
        val g = Geno()

        val seg = writeTemp(tmp, "x.s", """
            10 11
            20 21
        """)
        val funFile = writeTemp(tmp, "y.f", """
            1
            2
            3
        """)

        val ex = assertThrows<IllegalArgumentException> {
            g.calSegAndFun(seg.toString(), funFile.toString())
        }
        assertTrue(ex.message?.contains("No function values fall within the segments", ignoreCase = true) == true)
    }

    @Test
    fun `SEG file invalid format raises`(@TempDir tmp: Path) {
        val g = Geno()

        // Three columns on a line should be invalid for the current reader.
        val badSeg = writeTemp(tmp, "bad.s", """
            0 1 2
        """)
        val other = writeTemp(tmp, "ok.s", """
            5 6
        """)

        assertThrows<IllegalArgumentException> {
            g.calBothSeg(badSeg.toString(), other.toString())
        }
    }
}
