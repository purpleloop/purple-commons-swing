module io.github.purpleloop.commons.swing {

    exports io.github.purpleloop.commons.swing;
    exports io.github.purpleloop.commons.swing.color;
    exports io.github.purpleloop.commons.swing.image;
    exports io.github.purpleloop.commons.swing.graph;
    exports io.github.purpleloop.commons.swing.graph.shapes;
    exports io.github.purpleloop.commons.swing.graph.algorithm;
    exports io.github.purpleloop.commons.swing.graph.exception;
    exports io.github.purpleloop.commons.swing.sprites;
    exports io.github.purpleloop.commons.swing.sprites.model;
    exports io.github.purpleloop.commons.swing.sprites.exception;

    requires transitive java.desktop;
    requires transitive io.github.purpleloop.commons;
    requires org.apache.commons.logging;
}