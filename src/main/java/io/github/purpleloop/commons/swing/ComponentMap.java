package io.github.purpleloop.commons.swing;

import java.awt.Component;
import java.util.HashMap;

/**
 * A map of components, useful to keep track of components by their name. This
 * is specially useful when using iterative components.
 */
public class ComponentMap {

    /** The component map. */
    private HashMap<String, Component> components;

    /** Simple constructor. */
    public ComponentMap() {
        components = new HashMap<>();
    }

    /**
     * Adds a new component to the map.
     * 
     * @param name the name used to register the component
     * @param component the component to add
     */
    public void add(String name, Component component) {

        if (components.containsKey(name)) {
            throw new IllegalArgumentException(
                    "A component with the same name '" + name + "' is already registered");
        }
        components.put(name, component);
    }

    /**
     * Removes a component from the map.
     * 
     * @param name the name of the component
     */
    public void remove(String name) {

        if (!components.containsKey(name)) {
            throw new IllegalArgumentException(
                    "There is no component registered with the '" + name + "'.");
        }
        components.remove(name);
    }

    /**
     * A handily method to get a typed component (without a requiring an
     * explicit cast).
     * 
     * @param name the component name to get
     * @return the component
     * @param <T> the type to use as return
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) components.get(name);
    };

}
