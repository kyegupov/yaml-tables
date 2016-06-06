package yamltables.markdown

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.pegdown.LinkRenderer
import org.pegdown.PegDownProcessor
import org.pegdown.ToHtmlSerializer
import org.pegdown.plugins.PegDownPlugins
import org.pegdown.plugins.ToHtmlSerializerPlugin

class MarkdownWithEmbeddeYamlTableTest {

    @Test
    fun anExampleMarkdownWithTableShouldRenderCorrectly() {
        val markdownWithYaml = """
Markdown is:

* Simple
* Readable
* Efficient

|||
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
      Prince: *yes
...

Isn't it **great**?

"""

        val expectedRendering =
                """<p>Markdown is:</p>
<ul>
  <li>Simple</li>
  <li>Readable</li>
  <li>Efficient</li>
</ul>
<table>
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
</table>
<p>Isn't it <strong>great</strong>?</p>"""
        assertThat(renderMarkdown(markdownWithYaml).replace("\n", "")).isEqualTo(expectedRendering.replace("\n", ""))
    }

    fun renderMarkdown(markdown: String): String {
        val plugins = PegDownPlugins.Builder().withPlugin(YamlTablePegdownParser::class.java).build()
        val markdownProcessor = PegDownProcessor(0, plugins)
        val ast = markdownProcessor.parseMarkdown(markdown.toCharArray())
        val serializePlugins = listOf<ToHtmlSerializerPlugin>(YamlTablePegdownSerializer())

        return ToHtmlSerializer(LinkRenderer(), serializePlugins).toHtml(ast)
    }
}
