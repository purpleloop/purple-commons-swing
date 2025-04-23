package io.github.purpleloop.commons.swing.graph.algorithm;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.purpleloop.commons.math.geom.GeomUtils;
import io.github.purpleloop.commons.swing.graph.Graph;
import io.github.purpleloop.commons.swing.graph.Node;

/**
 * Graph auto-organization algorithm.
 * 
 * @param <E> graph content type
 */
public class AutoOrganizer<E> extends GraphOrganizer<E> {

    /** Class logger. */
    private static final Log LOG = LogFactory.getLog(AutoOrganizer.class);

    /** Initial attraction for linked nodes. */
    public static final double INITIAL_ATTRACT_LINK = 0.10;

    /** Initial attraction for not linked nodes. */
    public static final double INITIAL_ATTRACT_NOLINK = -100.0;

    /** Initial attraction for border. */
    public static final double INITIAL_ATTRACT_BORDER = -100.0;

    /** Default minimal distance for links. */
    private static final int DEFAULT_LINK_MINIMAL_DISTANCE = 20;

    /** Attraction for linked nodes. */
    private double attractLink = INITIAL_ATTRACT_LINK;

    /** Attraction for not linked nodes. */
    private double attractNoLink = INITIAL_ATTRACT_NOLINK;

    /** Attraction for border. */
    private double attractBorder = INITIAL_ATTRACT_BORDER;

    /** Minimal distance for links. */
    private double linkMinDist = DEFAULT_LINK_MINIMAL_DISTANCE;

    /** Border repulsion. */
    private boolean repulsionBorderEnabled = false;

    /** @return attraction for linked nodes */
    public double getAttractLink() {
        return attractLink;
    }

    /** @param attractLink attraction for linked nodes */
    public void setAttractLink(double attractLink) {
        this.attractLink = attractLink;
    }

    /** @return attraction for not linked nodes. */
    public double getAttractNoLink() {
        return attractNoLink;
    }

    /** @param attractNoLink Attraction for not linked nodes. */
    public void setAttractNoLink(double attractNoLink) {
        this.attractNoLink = attractNoLink;
    }

    /** @return attraction for border.. */
    public double getAttractBorder() {
        return attractBorder;
    }

    /** @param attractBorder attraction for border. */
    public void setAttractBorder(double attractBorder) {
        this.attractBorder = attractBorder;
    }

    /** @return minimal distance for links. */
    public double getLinkMinDist() {
        return linkMinDist;
    }

    /** @param linkMinDist minimal distance for links. */
    public void setLinkMinDist(double linkMinDist) {
        this.linkMinDist = linkMinDist;
    }

    /** Apply forces. */
    public void applyForces() {

        if (this.graph == null) {
            LOG.error("Graph is not initialized");
            return;
        }

        Node<E> refNode;
        Node<E> targetNode;

        List<Node<E>> nodes = this.graph.getNodes();

        // For each point
        for (int targetIdx = 0; targetIdx < nodes.size(); targetIdx++) {
            targetNode = nodes.get(targetIdx);

            // Consider all others and the attraction they have on the target
            for (int refIdx = 0; refIdx < nodes.size(); refIdx++) {
                if (refIdx != targetIdx) {
                    refNode = nodes.get(refIdx);
                    if (this.graph.areLinked(refNode, targetNode)) {
                        // Nodes are linked
                        attract(targetNode, refNode, attractLink, linkMinDist, true);
                    } else {
                        // Nodes are not linked
                        attract(targetNode, refNode, attractNoLink, 0, false);
                    }
                }
            } // for -- ref
        } // for -- target

        double width = Graph.MAX_WIDTH;
        double height = Graph.MAX_HEIGHT;

        if (repulsionBorderEnabled) {
            // Border repulsion

            for (int x = 0; x < width; x++) {

                for (Node<E> p : this.graph.getNodes()) {
                    attract(p, x, 0, attractBorder, 0, false);
                    attract(p, x, width, attractBorder, 0, false);
                    attract(p, 0, x, attractBorder, 0, false);
                    attract(p, height, x, attractBorder, 0, false);
                } // for -- node

            } // for -- border point
        }

    }

    /**
     * computes the influence of a node on another one.
     * 
     * @param targetNode Target node of the influence
     * @param sourceNode Source node of the influence
     * @param strength attraction / repulsion strength
     * @param minimalDistance minimal distance to apply attraction effect
     * @param link existence of link
     */
    private void attract(Node<E> targetNode, Node<E> sourceNode, double strength,
            double minimalDistance, boolean link) {
        attract(targetNode, sourceNode.xLoc(), sourceNode.yLoc(), strength, minimalDistance, link);
    }

    /**
     * Computes the influence of a source point given by it's coordinates (x,
     * y).
     * 
     * @param targetNode Target node of the influence
     * @param sourceX Source X coordinate
     * @param sourceY Source Y coordinate
     * @param strength attraction / repulsion strength
     * @param minimalDistance minimal distance to apply attraction effect
     * @param link existence of link
     */
    private void attract(Node<E> targetNode, double sourceX, double sourceY, double strength,
            double minimalDistance, boolean link) {

        // We start by computing the distance between source and target
        double sourceTargetDistance = GeomUtils.distance(targetNode.xLoc(), targetNode.yLoc(),
                sourceX, sourceY);

        // Determine the unit vector between source and target
        double ux = (sourceX - targetNode.xLoc()) / sourceTargetDistance;
        double uy = (sourceY - targetNode.yLoc()) / sourceTargetDistance;

        // Determine the distance where attraction is satisfied (ideal)
        double distanceToIdeal = sourceTargetDistance - minimalDistance;

        double force = 0.0;
        if (link) {

            // A link exists - it applies a force according to the distance

            if (distanceToIdeal > 0) {

                // Not enough close - get closer
                force = strength * distanceToIdeal;
            } else if (distanceToIdeal < 0) {

                // Too close - repulsion
                force = -strength;
            }

        } else {
            // No link exists - more like a gravitation force

            if (distanceToIdeal > 0) {
                force = strength / (distanceToIdeal * distanceToIdeal);
            }
        }

        // Moves the target according to the influence of the source
        targetNode.moveRel(force * ux, force * uy);

    }

}
