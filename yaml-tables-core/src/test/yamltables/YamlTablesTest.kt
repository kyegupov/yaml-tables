package yamltables

import com.google.common.truth.Truth.assertThat
import yamltables.YamlTableParser
import yamltables.YamlTableRenderer
import org.junit.Test

class YamlTablesTest {

    @Test 
    fun emptyTablespecShouldProduceEmptyTable() {
        val yaml = "{cols:[], data:[]}"
        assertThat(renderSimple(yaml).replace("\n", "")).isEqualTo("<table><tr></tr></table>")
    }

    @Test 
    fun oneCellTablespecShouldProduceOneCellTable() {
        val yaml = """{cols:["foo"], data:["foo": "bar"]}"""
        assertThat(renderSimple(yaml).replace("\n", "")).isEqualTo("<table><tr><th>foo</th></tr><tr><td>bar</td></tr></table>")
    }

    @Test
    fun anExampleSimpleTableShouldRenderCorrectly() {
        val yaml = """
    cols:
        - prop: Property
        - Trident: &valuecol
            align: center
            mergev: true
        - Gecko: *valuecol
        - WebKit: *valuecol
        - KHTML: *valuecol
        - Presto: *valuecol
        - prince:
            title: Prince
            align: center
            mergev: true
    data:
        - prop: "@import"
          Trident: 7.0
          Gecko: 1.0
          WebKit: {align: right, content: 85}
          KHTML: &yes "Yes!"
          Presto: 1.0
          prince: *yes
        - prop: "/*Comment/"
          Trident: 3.0
          Gecko: 1.0
          WebKit: {align: right, content: 85}
          KHTML: *yes
          Presto: 1.0
          prince: *yes"""

        val expectedRendering =
                    """<table>
<tr>
<th>Property</th><th>Trident</th><th>Gecko</th><th>WebKit</th>
<th>KHTML</th><th>Presto</th><th>Prince</th>
</tr>
<tr>
<td>@import</td><td align="center">7.0</td><td align="center" rowspan=2>1.0</td>
<td align="right" rowspan=2>85</td>
<td align="center" rowspan=2>Yes!</td><td align="center" rowspan=2>1.0</td>
<td align="center" rowspan=2>Yes!</td></tr><tr><td>/*Comment/</td>
<td align="center">3.0</td>
</tr>
</table>"""
        assertThat(renderSimple(yaml).replace("\n", "")).isEqualTo(expectedRendering.replace("\n", ""))
    }

    fun renderSimple(yaml: String): String {
        return YamlTableRenderer.renderTable(YamlTableParser.parseYamlAsTable(yaml), YamlTableRenderer.SIMPLE_RENDERER)
    }
}