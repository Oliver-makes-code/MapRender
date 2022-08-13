package dev.proxyfox.mod.maprender.doom;

public class Player {
	public float x = 0;
	public float y = 0;
	public float z = 2.5F;
	public float rot = 0;

	public void moveForward(float amount) {
		float dx = (float)Math.cos(rot) * amount;
		float dy = (float)Math.sin(rot) * amount;
		x += dx;
		y += dy;
	}

	public void turn(float amount) {
		rot += amount;
		if (rot < 0) {
			rot += (float) (2*Math.PI);
		} else if (rot >= 2*Math.PI) {
			rot -= (float) (2*Math.PI);
		}
		rot %= (float) (2*Math.PI);
	}
}
