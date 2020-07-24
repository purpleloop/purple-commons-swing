module purpleloop.commons.swing {

    exports purpleloop.commons.swing;
    exports purpleloop.commons.swing.image;
    exports purpleloop.commons.swing.sprites;

    requires transitive java.desktop;
    requires transitive purpleloop.commons;
    requires commons.logging;
}