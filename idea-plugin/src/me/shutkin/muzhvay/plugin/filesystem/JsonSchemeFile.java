package me.shutkin.muzhvay.plugin.filesystem;

import org.jetbrains.annotations.NotNull;

public class JsonSchemeFile extends HardcodedFile {
  private static final String name = "json-scheme.lipa";

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
          "  root json-model,\n" +
          "\n" +
          "  node json-model <no-values no-attributes children-required> {\n" +
          "    children package class definition\n" +
          "  },\n" +
          "\n" +
          "  node package <single-value no-children>,\n" +
          "  node class <single-value no-children>,\n" +
          "\n" +
          "  node definition <single-value children-required> {\n" +
          "    attributes read,\n" +
          "    children int long float double string str boolean bool array object\n" +
          "  },\n" +
          "\n" +
          "  node int long float double string str boolean bool <single-value no-children>,\n" +
          "  node array object <two-values no-children>\n" +
          "}";
}
