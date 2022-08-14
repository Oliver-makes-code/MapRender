package dev.proxyfox.mod.maprender.doom.sector;

import eu.pb4.mapcanvas.api.core.CanvasColor;

public class Line {
	public final float x1;
	public final float x2;
	public final float y1;
	public final float y2;
	public final float z1;
	public final float z2;

	public final CanvasColor color;

	public Line(float x1, float y1, float z1, float x2, float y2, float z2, CanvasColor color) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.z1 = z1;
		this.z2 = z2;
		this.color = color;
	}

	public Line(Point3d a, Point3d b, CanvasColor color) {
		this(a.x, a.y, a.z, b.x, b.y, b.z, color);
	}
}
