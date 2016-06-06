package yamltables.markdown;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.support.StringBuilderVar;
import org.pegdown.Parser;
import org.pegdown.plugins.BlockPluginParser;
import yamltables.YamlTableParser;

// This has to be written in Java - Pegdown uses Parboiled, which manipulates JVM opcodes and cannot handle Kotlin
public class YamlTablePegdownParser extends Parser implements BlockPluginParser {
    private final String START_MARKER = "|||";
    private final String YAML_END_MARKER = "...";

    public YamlTablePegdownParser() {
        super(ALL, 1000l, DefaultParseRunnerProvider);
    }

    @Override 
    public Rule[] blockPluginRules() {
        return new Rule[] { yamlTable() };
    }

    public Rule yamlTable() {

        // stack ends up having YAML table definition in it as a string

        return NodeSequence(
                yamlTableStartMarker(),
                body(),
                yamlEndMarker(),
                push(new YamlTableNode(
                        YamlTableParser.parseYamlAsTable((String) pop()))));
    }

    public Rule yamlTableStartMarker() {
        return Sequence(
                START_MARKER,
                Newline());
    }

    public Rule yamlEndMarker() {
        return Sequence(
                YAML_END_MARKER,
                Newline());
    }


    /*
     * extracts the body of the component into a raw string
     */
    public Rule body() {
        StringBuilderVar rawBody = new StringBuilderVar();

        return Sequence(
                OneOrMore(
                        TestNot(yamlEndMarker()),
                        BaseParser.ANY,
                        rawBody.append(matchedChar())),
                push(rawBody.getString().trim()));
    }
}
