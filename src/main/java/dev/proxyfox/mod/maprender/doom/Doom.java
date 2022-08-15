package dev.proxyfox.mod.maprender.doom;

import dev.proxyfox.mod.maprender.MapRenderBlockEntity;
import dev.proxyfox.mod.maprender.doom.sector.Line;
import dev.proxyfox.mod.maprender.doom.sector.Point2d;
import dev.proxyfox.mod.maprender.doom.sector.Sector;
import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Doom {
	private final MapRenderBlockEntity entity;
	private final PlayerCanvas canvas;
	private final int width = 128;
	private final int height = 96;
	private final Player activePlayer = new Player();
	private static final float fovMultiplier = 128;
	private int delta = 0;
	private static final int borderWidth = 0;
	private static final double fov = Math.PI/3;
	private static final double fov2 = fov/2;
	public boolean debug;
	private static final Pair<Integer, Integer>[] points = new Pair[] {
			// Row 1
			new Pair(2,0),
			new Pair(3,0),
			new Pair(4,0),
			new Pair(8,0),
			new Pair(16,0),
			new Pair(20,0),
			new Pair(21,0),
			new Pair(22,0),

			// Row 2
			new Pair(1,1),
			new Pair(5,1),
			new Pair(8,1),
			new Pair(16,1),
			new Pair(19,1),
			new Pair(23,1),

			// Row 3
			new Pair(0,2),
			new Pair(6,2),
			new Pair(8,2),
			new Pair(12,2),
			new Pair(16,2),
			new Pair(18,2),
			new Pair(24,2),

			// Row 4
			new Pair(0,3),
			new Pair(6,3),
			new Pair(9,3),
			new Pair(12,3),
			new Pair(15,3),
			new Pair(18,3),
			new Pair(24,3),

			// Row 5
			new Pair(0,4),
			new Pair(6,4),
			new Pair(9,4),
			new Pair(11,4),
			new Pair(13,4),
			new Pair(15,4),
			new Pair(18,4),
			new Pair(24,4),

			// Row 6
			new Pair(0,5),
			new Pair(6,5),
			new Pair(9,5),
			new Pair(11,5),
			new Pair(13,5),
			new Pair(15,5),
			new Pair(18,5),
			new Pair(24,5),

			// Row 7
			new Pair(1,6),
			new Pair(5,6),
			new Pair(10,6),
			new Pair(14,6),
			new Pair(19,6),
			new Pair(23,6),

			// Row 8
			new Pair(2,7),
			new Pair(3,7),
			new Pair(4,7),
			new Pair(10,7),
			new Pair(14,7),
			new Pair(20,7),
			new Pair(21,7),
			new Pair(22,7),
	};

	Sector[] sectors = new Sector[] {
			new Sector(
					new CanvasColor[]{
							CanvasColor.RED_HIGH,
							CanvasColor.ORANGE_HIGH,
							CanvasColor.YELLOW_HIGH,
							CanvasColor.GREEN_HIGH,
							CanvasColor.CYAN_HIGH,
							CanvasColor.BLUE_HIGH,
							CanvasColor.PURPLE_HIGH,
							CanvasColor.WHITE_HIGH,
							CanvasColor.BROWN_NORMAL
					},
					new float[] {
							40,40,20,0,-20,-40,-40,-20,20
					},
					new float[] {
							40,60,60,40,60,60,40,20,20
					},
					CanvasColor.YELLOW_HIGH,
					CanvasColor.BLUE_HIGH,
					-10,
					-5
			),
			new Sector(
					new CanvasColor[] {
							CanvasColor.RED_HIGH,
							CanvasColor.ORANGE_HIGH,
							CanvasColor.YELLOW_HIGH,
							CanvasColor.GREEN_HIGH,
							CanvasColor.CYAN_HIGH,
							CanvasColor.BLUE_HIGH,
							CanvasColor.PURPLE_HIGH,
							CanvasColor.WHITE_HIGH,
							CanvasColor.BROWN_NORMAL

					},
					new float[] {
							-20,0,20,40,60,60,-60,-60,-40
					},
					new float[] {
							60,40,60,60,20,80,80,20,60
					},
					CanvasColor.YELLOW_HIGH,
					CanvasColor.BLUE_HIGH,
					-10,
					5

			)
	};

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
					if (!debug) {
						if (j < height / 2 + 128 - height) {
							canvas.set(i, j, CanvasColor.GREEN_LOWEST);
						} else {
							canvas.set(i, j, CanvasColor.CYAN_HIGH);
						}
					} else {
						canvas.set(i,j, CanvasColor.BLACK_LOWEST);
					}
				}
			}
		}
	}

	public void drawMarquee() {
		int xoff = delta % width;
		int yh = 128 - height;
		for (Pair<Integer, Integer> p : points) {
			int x = p.getLeft()*4;
			int y = p.getRight()*4;
			x += xoff;
			x %= width;
			y = yh-y;
			x = width-x;
			for (int px = x; px < x+4; px++) {
				for (int py = y-4; py < y; py++) {
					canvas.set(px%width, py, CanvasColor.BLACK_LOWEST);
				}
			}
		}
	}

	public void markDirty() {
		canvas.sendUpdates();
	}

	public void update() {
		delta++;
		runGameLogic();
		render();
	}

	public float forward = 0;
	public float sideways = 0;

	public void runGameLogic() {
		activePlayer.turn(sideways / 30);
		activePlayer.moveForward(forward);
	}

	public void clampPoint(Point2d a, Point2d b, float t) {
		// Get slope of angle
		var m2 = Math.tan(Math.PI/2-t);
		// Fix m2 NaN/Infinity
		if (m2 == Double.NEGATIVE_INFINITY) m2 = -Float.MAX_VALUE;
		if (m2 == Double.POSITIVE_INFINITY) m2 = Float.MAX_VALUE;
		if (Double.isNaN(m2)) m2 = Double.MIN_VALUE;

		// If both x are the same, do alternate calculation
		if (a.x == b.x) {
			a.y = (float)m2*a.x;
			return;
		}

		// Get slope of line
		var m1 = (a.y-b.y)/(a.x-b.x);
		// Get y intercept of line
		var b1 = a.y-m1*a.x;
		// Get slope delta
		var m = m1-m2;
		// Prevent divide by zero
		if (m == 0) m = Float.MIN_VALUE;
		// Get intersection
		a.x = (float) (-b1/m);
		a.y = m1*a.x+b1;
	}

	public float dist(float x1, float y1, float x2, float y2) {
		float dx = x2-x1;
		float dy = y2-y1;
		return (float) Math.sqrt(dx*dx+dy*dy);
	}

	public void sortSectors() {
		// Quick and dirty bubble sort
		for (int s1 = 0; s1 < sectors.length-1; s1++) {
			for (int s2 = 0; s2 < sectors.length-s1-1; s2++) {
				if (sectors[s2].dist < sectors[s2+1].dist) {
					Sector old = sectors[s2];
					sectors[s2] = sectors[s2+1];
					sectors[s2+1] = old;
				}
			}
		}
	}

	public void sortLines(Pair<Sector, Line>[] lines) {
		// Quick and dirty bubble sort
		for (int s1 = 0; s1 < lines.length-1; s1++) {
			for (int s2 = 0; s2 < lines.length-s1-1; s2++) {
				if (lines[s2].getRight().dist < lines[s2+1].getRight().dist) {
					var old = lines[s2];
					lines[s2] = lines[s2+1];
					lines[s2+1] = old;
				}
			}
		}
	}

	public void drawSectors() {
		sortSectors();
		ArrayList<Pair<Sector, Line>> linesToDraw = new ArrayList<>();
		for (Sector s : sectors) {
			s.dist = 0;
			s.sortLines();
			s.floorPoints = new HashMap<>();
			for (Line l : s.lines) {
				l.dist = 0;
				drawLine(l, s, true);
				linesToDraw.add(new Pair<>(s, l));
			}
			s.dist /= s.lines.length;
		}
		Pair<Sector, Line>[] lines = linesToDraw.toArray(Pair[]::new);
		sortLines(lines);
		for (var line : lines) {
			drawLine(line.getRight(), line.getLeft(), false);
		}
	}

	public void drawLine(Line l, Sector s, boolean flip) {
		float asin = (float) Math.sin(activePlayer.rot);
		float acos = (float) Math.cos(activePlayer.rot);

		// Move points
		float xa1 = l.x1 - activePlayer.x;
		float xb1 = l.x2 - activePlayer.x;
		float xa;
		float xb;
		float ya = l.y1 - activePlayer.y;
		float yb = l.y2 - activePlayer.y;
		float za = s.z1 - activePlayer.z;
		float zb = s.z2 - activePlayer.z;

		// Rotate points
		xa = xa1 * acos - ya * asin;
		xb = xb1 * acos - yb * asin;
		ya = ya * acos + xa1 * asin;
		yb = yb * acos + xb1 * asin;

		if (debug) {
			point((int) xa/2+width/2, (int) ya/2+height/2, CanvasColor.RED_NORMAL);
			point((int) xb/2+width/2, (int) yb/2+height/2, CanvasColor.RED_NORMAL);
			point(width/2, height/2, CanvasColor.GREEN_NORMAL);
		}

		// Clamp lines to screen area

		// If the line is behind the camera don't bother rendering
		if (ya < 1 && yb < 1) return;

		// Get the angles of the ends of the line
		var a1 = Math.atan2(xa, ya);
		var a2 = Math.atan2(xb, yb);

		// If both are outside the fov don't bother drawing
		if (a1 > fov2 && a2 > fov2) return;
		if (a1 < -fov2 && a2 < -fov2) return;

		// Create writable point to not worry about returning mutliple variables
		var pa = new Point2d(xa, ya);
		var pb = new Point2d(xb, yb);

		// Clamp points outside the fov to inside the fov
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

		// Check if both points are behind the camera again
		if (pa.y < 1 && pb.y < 1) return;

		// If one point is behind the camera, clamp it to the oppisite end of the fov
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

		// Write to the variables
		xa = pa.x;
		ya = pa.y;
		xb = pb.x;
		yb = pb.y;

		s.dist += dist(0,0,(xa+xb)/2,(ya+yb)/2);
		l.dist = dist(0,0,(xa+xb)/2,(ya+yb)/2);

		if (debug) {
			point((int) xa/2+width/2, (int) ya/2+height/2, CanvasColor.BLUE_NORMAL);
			point((int) xb/2+width/2, (int) yb/2+height/2, CanvasColor.BLUE_NORMAL);
			point(width/2, height/2, CanvasColor.GREEN_NORMAL);
			return;
		}

		// Get screen pos
		int sxa = screenPos(xa, ya, width/2);
		int sxb = screenPos(xb, yb, width/2);
		int sya = screenPos(za, ya, height/2);
		int syb = screenPos(za, yb, height/2);
		int syc = screenPos(zb, ya, height/2);
		int syd = screenPos(zb, yb, height/2);

		// Get delta
		int dya = syb - sya;
		int dyb = syd - syc;
		int dx = sxb - sxa;

		// Make sure we're not drawing off the screen
		if (sxa < borderWidth) {
			sxa = borderWidth;
		}
		if (sxa > width - borderWidth) {
			sxa = width - borderWidth;
		}
		if (sxb < borderWidth) {
			sxb = borderWidth;
		}
		if (sxb > width - borderWidth) {
			sxb = width - borderWidth;
		}

		// Prevent divide by zero
		if (dx == 0) {
			dx = 1;
		}

		if (flip) {
			// Draw floor/ceil
			// Loop through x
			for (int px = sxb; px < sxa; px++) {
				int py;
				if (activePlayer.z < s.z1) {
					py = (int)Math.round(dya*(px-sxa+0.5)/dx+sya);
					point(px,py, s.ceil);
				} else if (activePlayer.z > s.z2) {
					py = (int)Math.round(dyb*(px-sxa+0.5)/dx+syc);
					s.floorPoints.put(px,py);
				}
			}
		} else {
			// Draw wall
			// Loop through x
			for (int px = sxa; px < sxb; px++) {
				// Get current y positions
				int pya = (int)Math.round(dya*(px-sxa+0.5)/dx+sya);
				int pyb = (int)Math.round(dyb*(px-sxa+0.5)/dx+syc);

				// Make sure we're not drawing off the screen
				if (pya < borderWidth) {
					pya = borderWidth;
				}
				if (pya > height - borderWidth) {
					pya = height - borderWidth;
				}
				if (pyb < borderWidth) {
					pyb = borderWidth;
				}
				if (pyb > height - borderWidth) {
					pyb = height - 1;
				}

				// Loop through column
				for (int py = pya; py < pyb; py++) {
					point(px,py, l.color);
				}
				// Draw floor and ceiling
				if (activePlayer.z < s.z1) {
					int ceil = s.floorPoints.getOrDefault(px, 0);
					for (int py = ceil; py < pya; py++) {
						point(px,py,s.ceil);
					}
				} else if (activePlayer.z > s.z2) {
					int floor = s.floorPoints.getOrDefault(px, 0);
					for (int py = pyb; py < floor; py++) {
						point(px,py,s.floor);
					}
				}
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

		drawSectors();

		// endregion

		drawMarquee();

		markDirty();
	}
}
