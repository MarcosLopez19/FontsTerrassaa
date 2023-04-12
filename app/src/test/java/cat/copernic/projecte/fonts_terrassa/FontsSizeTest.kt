package cat.copernic.projecte.fonts_terrassa

import org.junit.Assert
import org.junit.Test

class FontsSizeTest {

    @Test
    fun testSelectedFonts() {
        val util = ListFragment()
        val tipusFonts = arrayOf(true, true, true, true, true)
        val tipusFonts2 = util.selectAllFonts()
        Assert.assertEquals(tipusFonts, tipusFonts2)
    }
}