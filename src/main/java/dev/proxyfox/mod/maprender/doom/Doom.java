package dev.proxyfox.mod.maprender.doom;

import dev.proxyfox.mod.maprender.MapRenderBlockEntity;
import dev.proxyfox.mod.maprender.doom.sector.Line;
import dev.proxyfox.mod.maprender.doom.sector.Point2d;
import dev.proxyfox.mod.maprender.doom.sector.Point3d;
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
	private double fov = Math.PI/3;
	private double fov2 = fov/2;

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

	public void clampPoint(Point2d a, Point2d b, float t) {
		// Get slope and intersect of line
		var m1 = (a.y-b.y)/(a.x-b.x);
		var b1 = a.y-m1*a.x;
		// Get slope of angle
		var m2 = Math.tan(Math.PI/2-t);
		if (Double.isNaN(m2)) m2 = Double.MAX_VALUE;
		// Get intersection
		a.x = (float) (-b1/(m1-m2));
		a.y = m1*a.x+b1;
	}

	public void drawLine(Line l) {
		float asin = (float) Math.sin(activePlayer.rot);
		float acos = (float) Math.cos(activePlayer.rot);

		// Move points
		float xa1 = l.x1 - activePlayer.x;
		float xb1 = l.x2 - activePlayer.x;
		float xa;
		float xb;
		float ya = l.y1 - activePlayer.y;
		float yb = l.y2 - activePlayer.y;
		float za = l.z1 - activePlayer.z;
		float zb = l.z2 - activePlayer.z;

		// Rotate points
		xa = xa1 * acos - ya * asin;
		xb = xb1 * acos - yb * asin;
		ya = ya * acos + xa1 * asin;
		yb = yb * acos + xb1 * asin;

		// Clamp lines to screen area

		if (ya < 1 && yb < 1) return;
		var a1 = Math.atan2(xa, ya);
		var a2 = Math.atan2(xb, yb);
		if (a1 > fov2 && a2 > fov2) return;
		if (a1 < -fov2 && a2 < -fov2) return;

		var pa = new Point2d(xa, ya);
		var pb = new Point2d(xb, yb);

		if (a1 > fov2) {
			clampPoint(pa, pb, (float)fov2);
		}
		if (a1 < -fov2) {
			clampPoint(pa, pb, (float)-fov2);
		}
		if (a2 > fov2) {
			clampPoint(pb, pa, (float)fov2);
		}
		if (a2 < -fov2) {
			clampPoint(pb, pa, (float)-fov2);
		}

		if (pa.y < 1 && pb.y < 1) return;
		if (pa.y < 1) {
			if (a1 > fov2) {
				clampPoint(pa, pb, (float)-fov2);
			}
			if (a1 < -fov2) {
				clampPoint(pa, pb, (float)fov2);
			}
		}
		if (pb.y < 1) {
			if (a2 > fov2) {
				clampPoint(pb, pa, (float)-fov2);
			}
			if (a2 < -fov2) {
				clampPoint(pb, pa, (float)fov2);
			}
		}

		xa = pa.x;
		ya = pa.y;
		xb = pb.x;
		yb = pb.y;

		// Get screen pos
		int sxa = screenPos(xa, ya, width/2);
		int sxb = screenPos(xb, yb, width/2);
		int sya = screenPos(za, ya, height/2);
		int syb = screenPos(za, yb, height/2);
		int syc = screenPos(zb, ya, height/2);
		int syd = screenPos(zb, yb, height/2);

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
				point(px,py, l.color);
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

		drawLine(new Line(20, -50, 0, -20, 40, 5, CanvasColor.RED_NORMAL));

		// endregion

		markDirty();
	}
}
