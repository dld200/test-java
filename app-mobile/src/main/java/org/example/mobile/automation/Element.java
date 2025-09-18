package org.example.mobile.automation;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

@Data
public class Element {
    public String name;

    public String label;

    public String type;

    public boolean enabled;

    public boolean visible;

    public boolean accessible;

    public int x, y, width, height;

    public transient Element parent;

    public List<Element> children = new ArrayList<>();

    public Element(){
    }

    public Element(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean eliminate(Element parent) {
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

    public void addChild(Element child) {
        child.parent = this;
        this.children.add(child);
    }

    @Override
    public String toString() {
        return String.format("%s [name=%s, label=%s, x=%.1f y=%.1f w=%.1f h=%.1f]",
                type, name, label, x, y, width, height);
    }
}
