package dev.proxyfox.mod.maprender.doom;

public class Player {
	public float x = 0;
	public float y = -40;
	public float z = 0;
	public float rot = 0;

	public void moveForward(float amount) {
		float dx = (float)Math.sin(rot) * amount;
		float dy = (float)Math.cos(rot) * amount;
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
