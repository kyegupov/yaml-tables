package yamltables

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor

object YamlTableParser {

    fun getData(yamlTableSpec: Map<String, Any>): List<RowSpec> {
        val rows = mutableListOf<RowSpec>()
        for (dataRow in yamlTableSpec["data"] as List<Map<String, Any>>) {
            val rowCells = mutableMapOf<String, CellSpec>()
            for ((key, value) in dataRow.entries) {
                rowCells.put(key, parseCell(value))
            }
            rows.add(RowSpec(rowCells))
        }
        return rows.toList()
    }

    fun parseCell(value: Any): CellSpec {
        if (value is Map<*, *>) {
            val cellSpecMap = asYamlMap(value)
            return CellSpec(cellSpecMap["content"]!!, cellSpecMap["align"] as String?)
        } else {
            return CellSpec(value)
        }
    }

    fun getCols(yamlTableSpec: Map<String, Any>): List<ColSpec> {
        val cols = mutableListOf<ColSpec>()
        val colSpecs = yamlTableSpec["cols"] as List<*>
        for (colSpecEntry in colSpecs) {
            when (colSpecEntry) {
                is String -> cols.add(ColSpec(colSpecEntry, colSpecEntry))
                is Map<*, *> -> {
                    val colNameToSpecMap = asYamlMap(colSpecEntry)
                    // Has to have single element
                    assert(colNameToSpecMap.size == 1)
                    val colId = colNameToSpecMap.keys.first()
                    val colSpecValue = colNameToSpecMap.values.first()

                    when (colSpecValue) {
                        is String -> cols.add(ColSpec(colId, colSpecValue))
                        is Map<*, *> -> cols.add(ColSpec(colId,
                                colSpecValue.get("title") as String? ?: colId,
                                colSpecValue.get("align") as String?,
                                colSpecValue.get("mergev") as Boolean? ?: false))
                        else -> error("Unsupported colspec value: " + colSpecValue)
                    }
                }
                else -> error("Unsupported colspec entry: " + colSpecEntry)
            }
        }
        return cols
    }

    @JvmStatic
    fun parseYamlAsTable(yamlString: String): TableSpec {
        val yamlParser = Yaml(SafeConstructor())
        val yamlMap = asYamlMap(yamlParser.load(yamlString))
        return TableSpec(getCols(yamlMap), getData(yamlMap))
    }

    @SuppressWarnings("unchecked")
    fun asYamlMap(o: Any): Map<String, Any> = o as Map<String, Any>
}
