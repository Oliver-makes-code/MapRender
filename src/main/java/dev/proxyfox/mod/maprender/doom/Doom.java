package dev.proxyfox.mod.maprender.doom;

import dev.proxyfox.mod.maprender.MapRenderBlockEntity;
import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;

public class Doom {
	private final MapRenderBlockEntity entity;
	private final PlayerCanvas canvas;
	private final int width = 128;
	private final int height = 96;
	private final Player activePlayer = new Player();
	private float fovMultiplier = 128;
	private int delta = 0;
	private int borderWidth = 2;

	public Doom(MapRenderBlockEntity entity) {
		this.entity = entity;
		canvas = entity.canvas;
	}

	public void clear() {
		for (int i = 0; i < 128; i++) {
			for (int j = 0; j < 128; j++) {
				if (j < 128 - height) {
					canvas.set(i, j, CanvasColor.WHITE_HIGH);
				} else {
					canvas.set(i, j, CanvasColor.BLACK_LOWEST);
				}
			}
		}
	}

	public void markDirty() {
		canvas.sendUpdates();
	}

	public void update() {
		runGameLogic();
		render();
	}

	public void runGameLogic() {
		activePlayer.turn(-0.01f);
	}

	public void clipPoint(Point2d a, Point2d b) {
		// Get angle
		var r = Math.atan2(a.x,a.y);
		// If it's within FOV, return
		if (r <= Math.PI/6 && r >= -Math.PI/6) {
			return;
		}
		// Get slope from line
		var m1 = (a.y-b.y)/(a.x-b.x);
		var b1 = a.y-m1*a.x;
		// Get slope from angle
		double m2;
		if (r > Math.PI/6) {
			m2 = Math.cos(Math.PI/6)/Math.sin(Math.PI/6);
		} else {
			m2 = Math.cos(-Math.PI/6)/Math.sin(-Math.PI/6);
		}
		// Calculate new x and y
		var dx = -b1/(m1-m2);
		var dy = m1*dx+b1;
		// Set new x and y
		a.x = (float) dx;
		a.y = (float) dy;
	}

	public void drawQuad(Point3d a, Point3d b) {
		float asin = (float) Math.sin(activePlayer.rot);
		float acos = (float) Math.cos(activePlayer.rot);

		// Move points
		float xa1 = a.x - activePlayer.x;
		float xa;
		float xb1 = b.x - activePlayer.x;
		float xb;
		float ya = a.y - activePlayer.y;
		float yb = b.y - activePlayer.y;
		float yc;
		float yd;
		float za = a.z - activePlayer.z;
		float zb = a.z - activePlayer.z;
		float zc = b.z - activePlayer.z;
		float zd = b.z - activePlayer.z;

		// Rotate points
		xa = xa1 * acos - ya * asin;
		xb = xb1 * acos - yb * asin;
		ya = ya * acos + xa1 * asin;
		yb = yb * acos + xb1 * asin;
		yc = ya;
		yd = yb;

		// Clip points
		var p2a = new Point2d(xa,ya);
		var p2b = new Point2d(xb,yb);
		clipPoint(p2a, p2b);
		clipPoint(p2b, p2a);

		xa = p2a.x;
		ya = p2a.y;
		xb = p2b.x;
		yb = p2b.y;

		if (ya < 1 && yb < 1) return;

		// Get screen pos
		int sxa = screenPos(xa, ya, width/2);
		int sxb = screenPos(xb, yb, width/2);
		int sya = screenPos(za, ya, height/2);
		int syb = screenPos(zb, yb, height/2);
		int syc = screenPos(zc, yc, height/2);
		int syd = screenPos(zd, yd, height/2);

		// Get smaller values
		int smxa, smxb, smya, smyb, smyc, smyd;
		if (sxa < sxb) {
			smxa = sxa;
			smxb = sxb;
			smya = sya;
			smyb = syb;
			smyc = syc;
			smyd = syd;
		} else {
			smxa = sxb;
			smxb = sxa;
			smya = syb;
			smyb = sya;
			smyc = syd;
			smyd = syc;
		}

		// Get delta
		int dya = smyb - smya;
		int dyb = smyd - smyc;
		int dx = smxb - smxa;

		// Make sure we're not drawing off the screen
		if (smxa < borderWidth) {
			smxa = borderWidth;
		}
		if (smxa > width - borderWidth) {
			smxa = width - borderWidth;
		}
		if (smxb < borderWidth) {
			smxb = borderWidth;
		}
		if (smxb > width - borderWidth) {
			smxb = width - borderWidth;
		}

		// Prevent divide by zero
		if (dx == 0) {
			dx = 1;
		}

		// Loop through x
		for (int px = smxa; px < smxb; px++) {
			// Get current y positions
			int pya = (int)Math.round(dya*(px-smxa+0.5)/dx+smya);
			int pyb = (int)Math.round(dyb*(px-smxa+0.5)/dx+smyc);

			// Get smaller values
			int spya, spyb;
			if (pya < pyb) {
				spya = pya;
				spyb = pyb;
			} else {
				spya = pyb;
				spyb = pya;
			}

			// Make sure we're not drawing off the screen
			if (spya < borderWidth) {
				spya = borderWidth;
			}
			if (spya > height - borderWidth) {
				spya = height - borderWidth;
			}
			if (spyb < borderWidth) {
				spyb = borderWidth;
			}
			if (spyb > height - borderWidth) {
				spyb = height - 1;
			}

			// Loop through column
			for (int py = spya; py < spyb; py++) {
				point(px,py, CanvasColor.RED_NORMAL);
			}
		}
	}

	public void point(int x, int y, CanvasColor color) {
		if (x > width || x < 0) return;
		if (y > height || y < 0) return;
		canvas.set(x,y+128-height, color);
	}

	public int screenPos(float a, float b, float w) {
		return (int)Math.floor(a * fovMultiplier / b + w);
	}

	public void render() {
		clear();

		// region: map render

		delta++;

		drawQuad(new Point3d(40F, 10F, 0F), new Point3d(40F,40F,5F));

		// endregion

		markDirty();
	}
}
