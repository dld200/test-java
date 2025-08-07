package org.example.mobile.xml;

import java.util.ArrayList;
import java.util.List;

public class UIElement {
    public String type;
    public String name;
    public String label;
    public boolean enabled;
    public boolean visible;
    public boolean accessible;
    public float x, y, width, height;
    public UIElement parent;
    public List<UIElement> children = new ArrayList<>();

    public boolean sameBounds(UIElement other) {
        if(!this.name.equals(this.label)){
            return false;
        }
        return this.x == other.x && this.y == other.y
                && this.width == other.width && this.height == other.height;
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
