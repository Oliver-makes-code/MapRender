package dev.proxyfox.mod.maprender.doom.sector;

import eu.pb4.mapcanvas.api.core.CanvasColor;

public class Line {
	public final float x1;
	public final float x2;
	public final float y1;
	public final float y2;
	public float dist;

	public final CanvasColor color;

	public Line(float x1, float y1, float x2, float y2, CanvasColor color) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.color = color;
	}
}
