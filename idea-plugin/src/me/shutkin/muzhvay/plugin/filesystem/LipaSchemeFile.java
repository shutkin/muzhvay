package me.shutkin.muzhvay.plugin.filesystem;

import org.jetbrains.annotations.NotNull;

public class LipaSchemeFile extends HardcodedFile {
  private static final String name = "lipa-scheme.lipa";

  @NotNull
  @Override
  public String getName() {
    return name;
  }

  @Override
  protected String getContent() {
    return content;
  }

  private static final String content = "[lipa-scheme]\n" +
          "scheme {\n" +
          "  root scheme,\n" +
          "\n" +
          "  node scheme <no-values no-attributes children-required> {\n" +
          "    children root node\n" +
          "  },\n" +
          "\n" +
          "  node root <single-value no-children no-attributes>,\n" +
          "\n" +
          "  node node <values-required> {\n" +
          "    attributes no-values single-value two-values values-required no-attributes children-required no-children,\n" +
          "    children attributes values-pattern children\n" +
          "  },\n" +
          "  node node.attributes <no-attributes no-children>,\n" +
          "  node node.values-pattern <single-value no-attributes no-children>,\n" +
          "  node node.children <no-attributes no-children>\n" +
          "}\n";
}
