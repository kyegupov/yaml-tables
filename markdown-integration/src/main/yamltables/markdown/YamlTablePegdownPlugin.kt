package yamltables.markdown

import org.pegdown.PegDownProcessor
import org.pegdown.Printer
import org.pegdown.ast.AbstractNode
import org.pegdown.ast.Node
import org.pegdown.ast.Visitor
import org.pegdown.plugins.ToHtmlSerializerPlugin
import yamltables.TableSpec
import yamltables.YamlTableRenderer

class YamlTableNode(val table: TableSpec) : AbstractNode() {

    // TODO: actually parse YAML table into a tree of table nodes

    override fun accept(visitor: Visitor) {
        visitor.visit(this as Node)
    }

    override fun getChildren(): List<Node>? {
        return null
    }
}

class YamlTablePegdownSerializer : ToHtmlSerializerPlugin {

    override fun visit(node: Node, visitor: Visitor, printer: Printer): Boolean {
        if (node is YamlTableNode) {
            val markdownProcessor = PegDownProcessor()
            val markdownRenderer = { o: Any? -> markdownProcessor.markdownToHtml(o?.toString() ?: "") }

            printer.println()
            printer.print(YamlTableRenderer.renderTable(node.table, markdownRenderer))

            return true
        }
        return false
    }
}

// This had to be rewritten in Java - Pegdown uses Parboiled, which manipulates JVM opcodes and cannot handle Kotlin
//open class YamlTablePegdownParser : Parser(Extensions.ALL, 1000L, Parser.DefaultParseRunnerProvider), BlockPluginParser {
//    private val START_MARKER = "|||"
//    private val YAML_END_MARKER = "..."
//
//    override fun blockPluginRules(): Array<Rule> {
//        return arrayOf(yamlTable())
//    }
//
//    open fun yamlTable(): Rule {
//
//        // stack ends up having YAML table definition in it as a string
//
//        return NodeSequence(
//                yamlTableStartMarker(),
//                body(),
//                yamlEndMarker(),
//                push(YamlTableNode(
//                        YamlTableParser.parseYamlAsTable(pop() as String))))
//    }
//
//    open fun yamlTableStartMarker(): Rule {
//        return Sequence(
//                START_MARKER,
//                Newline())
//    }
//
//    open fun yamlEndMarker(): Rule {
//        return Sequence(
//                YAML_END_MARKER,
//                Newline())
//    }
//
//
//    /*
//     * extracts the body of the component into a raw string
//     */
//    open fun body(): Rule {
//        val rawBody = StringBuilderVar()
//
//        return Sequence(
//                OneOrMore(
//                        TestNot(yamlEndMarker()),
//                        BaseParser.ANY,
//                        rawBody.append(matchedChar())),
//                push(rawBody.string.trim { it <= ' ' }))
//    }
//}