package yamltables

import com.google.common.html.HtmlEscapers
import yamltables.ColSpec
import yamltables.RowSpec
import yamltables.TableSpec

/**
 * An Any to render table specifications as HTML.
 */
object YamlTableRenderer {

    val SIMPLE_RENDERER = {o : Any? -> HtmlEscapers.htmlEscaper().escape((o ?: "").toString())}

    /**
     * Renders table as HTML.
     * @param tableSpec table description as YAML map
     * @param cellRenderer cell contents renderer, should output HTML
     * @return
     */
    fun renderTable(tableSpec: TableSpec, cellRenderer: (Any?) -> String): String {
        val cols = tableSpec.cols
        val data = tableSpec.rows

        val outHeads = StringBuilder()
        val outData = StringBuilder()

        outHeads.append("<tr>")
        for (colSpec in cols) {
            outHeads.append("<th>${cellRenderer(colSpec.title)}</th>\n")
        }
        outHeads.append("</tr>\n")


        val cellsVerticallyMergedTillRowNumber = mutableMapOf<ColSpec, Int>()

        for ((rowIndex, row) in data.withIndex()) {
            outData.append("<tr>")
            for (colSpec in cols) {
                val cellShouldBeSkipped = (cellsVerticallyMergedTillRowNumber[colSpec] ?: -1) > rowIndex
                if (!cellShouldBeSkipped) {
                    var firstRowWithDifferentValue = rowIndex + 1
                    while (firstRowWithDifferentValue < data.size
                            && data[firstRowWithDifferentValue].cells[colSpec.id] == row.cells[colSpec.id]) {
                        firstRowWithDifferentValue += 1
                    }
                    cellsVerticallyMergedTillRowNumber.put(colSpec, firstRowWithDifferentValue)
                    val rowSpan = firstRowWithDifferentValue - rowIndex
                    outData.append(renderCell(row, colSpec, rowSpan, cellRenderer))
                }
            }

            outData.append("</tr>\n")
        }

        return "<table>${outHeads.toString()}${outData.toString()}</table>"
    }

    fun renderCell(row: RowSpec, colSpec: ColSpec, rowSpan: Int, renderer: (Any?) -> String): String {
        if (row.cells.contains(colSpec.id)) {
            val cellSpec = row.cells[colSpec.id]!!

            val attributes = StringBuilder()
            val align = cellSpec.align ?: colSpec.align
            if (align != null) {
                attributes.append(" align=\"" + align + "\"") // see SI-6476
            }
            if (rowSpan != 1) {
                attributes.append(" rowspan=$rowSpan")
            }
            return "<td$attributes>${renderer(cellSpec.content)}</td>\n"
        } else {
            return "<td></td>\n"
        }
    }

}