package yamltables

/**
 * Cell specification.
 * @param content cell contents (any YAML type, if string - to be treated as Markdown)
 * @param align cell alignment override
 */
data class CellSpec(
        val content: Any,
        val align: String? = null)

/**
 * Table row specification.
 */
data class RowSpec(
        val cells: Map<String, CellSpec>)

/**
 * Column specification.
 * @param id column id in YAML data
 * @param title human-readable column title
 * @param align column default alignment
 * @param mergeVertical whether to merge cells in this column with the same value
 */
data class ColSpec(
        val id: String,
        val title: String,
        val align: String? = null,
        val mergeVertical: Boolean = false)

/**
 * Table specification.
 */
data class TableSpec(
        val cols: List<ColSpec>,
        val rows: List<RowSpec>)