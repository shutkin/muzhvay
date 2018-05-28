# Muzhvay Project

Currently consists of:
- Very basic Lipa parser written in Kotlin
- Lipa language plugin for Intellij Idea
- Kotlin code generation for JSON deserialization

##Lipa language

Lipa is a minimalistic JSON-like format for definition of a tree-structured data. Each node in the Lipa data has a key and 
unlimited set of attributes, values and child nodes. Here is an example:<br>

    root {
      node1 <attribute1 attribute2> value1 value2,
      node2 v21 {child.node value.child, child2 <child-attribute>}
    }

Set of rules can be applied to Lipa files by referencing to a scheme (which is also a Lipa file): what key should root node 
have, what type of children could a specific node contains, how much values can a specific node have, what attributes can 
be applied to a node etc. Reference to a scheme must be defined before root node inside of square brackets in the Lipa file.

#Idea Plugin
Plugin provides syntax highlighting, annotation for format errors and scheme validation errors, autocompletion for nodes 
keys and attributes and referencing to a scheme elements. Autocompletion for scheme names, settings for syntax highlighting 
and text formatting currently is in the developing.<br>
Plugin have two hardcoded schemes: *lipa-scheme* and *json-scheme*. *lipa-scheme* can be used for building custom schemes. 
*json-scheme* can be used for JSON deserializers generation.<br>
Command *"Generate JSON deserializers"* In the *Tools* menu will generate (or rewrite) Kotlin sources for all Lipa files 
in the project that uses scheme *json-scheme*.  

##JSON deserializer generation

Generator creates a Kotlin source code for deserializing JSON data to a strongly typed immutable data objects. Generated 
sources consists of a single Kotlin file with no additional dependencies. In that file there is a public data classes and 
public functions to read (deserialize) data classes from an InputStream.<br><br>
Lipa file
    
    [json-scheme]
    json-model {
      package org.muzhvay.test, class JSONTest,
    
      definition TestObject <read> {
        long id,
        string title,
        array TestChildObject children,
        object TestObject next
      },
      definition TestChildObject {
        int index,
        string name,
        double factor,
        array int data,
        boolean alive
      }
    }

would produce a Kotlin source file JSONTest with following data classes and function:
    
      data class TestObject(val id: Long?, val title: String?, val children: Array<TestChildObject>?, val next: TestObject?)
      data class TestChildObject(val index: Int?, val name: String?, val factor: Double?, val data: IntArray?, val alive: Boolean?)
    
      fun readTestObject(stream: InputStream): TestObject?
