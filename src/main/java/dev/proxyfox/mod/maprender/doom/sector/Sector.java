package dev.proxyfox.mod.maprender.doom.sector;

import eu.pb4.mapcanvas.api.core.CanvasColor;

import java.util.HashMap;
import java.util.Map;

public class Sector {
	public Line[] lines;
	public final CanvasColor floor;
	public final CanvasColor ceil;
	public final float z1;
	public final float z2;
	public float dist;

	public Map<Integer, Integer> floorPoints = new HashMap<>();

	public Sector(CanvasColor[] colors, float[] x, float[] y, CanvasColor floor, CanvasColor ceil, float z1, float z2) {
		if (colors.length != x.length || colors.length != y.length) {
			throw new IllegalArgumentException("All arrays must be the same size");
		}
		lines = new Line[x.length];
		for (int i = 0; i < x.length; i++) {
			float x1 = x[i];
			float y1 = y[i];
			CanvasColor color = colors[i];
			float x2, y2;
			if (i == x.length-1) {
				x2 = x[0];
				y2 = y[0];
			} else {
				x2 = x[i+1];
				y2 = y[i+1];
			}
			lines[i] = new Line(x1,y1,x2,y2,color);
		}
		this.floor = floor;
		this.ceil = ceil;
		this.z1 = z1;
		this.z2 = z2;
	}

	public void sortLines() {
		// Quick and dirty bubble sort
		for (int s1 = 0; s1 < lines.length-1; s1++) {
			for (int s2 = 0; s2 < lines.length-s1-1; s2++) {
				if (lines[s2].dist < lines[s2+1].dist) {
					Line old = lines[s2];
					lines[s2] = lines[s2+1];
					lines[s2+1] = old;
				}
			}
		}
	}
}
