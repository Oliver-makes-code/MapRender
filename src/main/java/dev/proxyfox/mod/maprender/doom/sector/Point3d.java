package dev.proxyfox.mod.maprender.doom.sector;

public class Point3d extends Point2d {
	public float z;
	public Point3d() {
		super();
	}
	public Point3d(float x, float y, float z) {
		super(x,y);
		this.z = z;
	}
}
