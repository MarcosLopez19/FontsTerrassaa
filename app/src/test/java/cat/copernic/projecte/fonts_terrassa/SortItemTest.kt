package cat.copernic.projecte.fonts_terrassa

import org.junit.Assert
import org.junit.Test

class SortItemTest {

    @Test
    fun testGetSortItem() {
        val util = ListFragment()
        val notExcpectedValue = -1
        val itemValue = util.getItemSelected()
        Assert.assertNotEquals(notExcpectedValue, itemValue)
    }
}

