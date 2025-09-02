package org.example.common.model;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class UIElement {
    public String name;
    public String label;
    public String type;
    public boolean enabled;
    public boolean visible;
    public boolean accessible;
    public float x, y, width, height;
    public transient UIElement parent;
    public List<UIElement> children = new ArrayList<>();

    public boolean eliminate(UIElement parent) {
        if (this.enabled && this.visible && this.accessible) {
            return false;
        }

//        if (!this.label.equals(parent.label) || !this.name.equals(parent.name)) {
//            return false;
//        }

        if (!"XCUIElementTypeOther".equals(this.type)) {
            return false;
        }

        if (this.name.length() > 100) {
            return true;
        }

        if (this.x == parent.x && this.y == parent.y && abs(this.width - parent.width) < 5 && abs(this.height - parent.height) < 5) {
            return true;
        }

        if (this.name.isEmpty() && this.label.isEmpty()) {
            return true;
        }
        return false;
    }

    public void addChild(UIElement child) {
        child.parent = this;
        this.children.add(child);
    }

    @Override
    public String toString() {
        return String.format("%s [name=%s, label=%s, x=%.1f y=%.1f w=%.1f h=%.1f]",
                type, name, label, x, y, width, height);
    }
}
