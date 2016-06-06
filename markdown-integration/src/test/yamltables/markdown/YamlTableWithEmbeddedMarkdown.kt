package yamltables.markdown

import com.google.common.truth.Truth.assertThat
import yamltables.YamlTableParser
import yamltables.YamlTableRenderer
import org.junit.Test
import org.pegdown.PegDownProcessor

class YamlTableWithEmbeddedMarkdownTest {

    @Test
    fun anExampleTableWithMarkdownShouldRenderCorrectly() {
        val yaml = """
cols:
    - Property
    - Trident: &valuecol
        align: center
        mergev: true
    - Gecko: *valuecol
    - WebKit: *valuecol
    - KHTML: *valuecol
    - Presto: *valuecol
    - Prince: *valuecol
data:
    - Property: "@import"
      Trident: 7.0
      Gecko: 1.0
      WebKit: {align: right, content: 85}
      KHTML: &yes "**Yes**"
      Presto: 1.0
      Prince: *yes
    - Property: "/*Comment/"
      Trident: 3.0
      Gecko: 1.0
      WebKit: {align: right, content: 85}
      KHTML: *yes
      Presto: 1.0
      Prince: *yes"""

        val expectedRendering =
                """<table>
<tr>
<th><p>Property</p></th><th><p>Trident</p></th><th><p>Gecko</p></th><th><p>WebKit</p></th>
<th><p>KHTML</p></th><th><p>Presto</p></th><th><p>Prince</p></th>
</tr>
<tr>
<td><p>@import</p></td><td align="center"><p>7.0</p></td><td align="center" rowspan=2><p>1.0</p></td>
<td align="right" rowspan=2><p>85</p></td>
<td align="center" rowspan=2><p><strong>Yes</strong></p></td><td align="center" rowspan=2><p>1.0</p></td>
<td align="center" rowspan=2><p><strong>Yes</strong></p></td></tr><tr><td><p>/*Comment/</p></td>
<td align="center"><p>3.0</p></td>
</tr>
</table>"""
        assertThat(renderYamlWithMarkdown(yaml).replace("\n", "")).isEqualTo(expectedRendering.replace("\n", ""))
    }

    fun renderYamlWithMarkdown(yaml: String): String {
        val markdownProcessor = PegDownProcessor()
        val markdownRenderer = { o: Any? -> markdownProcessor.markdownToHtml(o?.toString() ?: "") }
        return YamlTableRenderer.renderTable(YamlTableParser.parseYamlAsTable(yaml), markdownRenderer)
    }
}
