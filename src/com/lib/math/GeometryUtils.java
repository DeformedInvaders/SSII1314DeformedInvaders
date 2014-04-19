/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.lib.math;

import com.lib.utils.Array;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

/** @author Nathan Sweet */
public class GeometryUtils {
	static private final Vector2 tmp1 = new Vector2(), tmp2 = new Vector2(),
			tmp3 = new Vector2();

	/**
	 * Computes the barycentric coordinates v,w for the specified point in the
	 * triangle.
	 * <p>
	 * If barycentric.x >= 0 && barycentric.y >= 0 && barycentric.x +
	 * barycentric.y <= 1 then the point is inside the triangle.
	 * <p>
	 * If vertices a,b,c have values aa,bb,cc then to get an interpolated value
	 * at point p:
	 * 
	 * <pre>
	 * GeometryUtils.barycentric(p, a, b, c, barycentric);
	 * float u = 1.f - barycentric.x - barycentric.y;
	 * float x = u * aa.x + barycentric.x * bb.x + barycentric.y * cc.x;
	 * float y = u * aa.y + barycentric.x * bb.y + barycentric.y * cc.y;
	 * </pre>
	 * 
	 * @return barycentricOut
	 */
	static public Vector2 barycentric(Vector2 p, Vector2 a, Vector2 b,
			Vector2 c, Vector2 barycentricOut) {
		Vector2 v0 = tmp1.set(b).sub(a);
		Vector2 v1 = tmp2.set(c).sub(a);
		Vector2 v2 = tmp3.set(p).sub(a);
		float d00 = v0.dot(v0);
		float d01 = v0.dot(v1);
		float d11 = v1.dot(v1);
		float d20 = v2.dot(v0);
		float d21 = v2.dot(v1);
		float denom = d00 * d11 - d01 * d01;
		barycentricOut.x = (d11 * d20 - d01 * d21) / denom;
		barycentricOut.y = (d00 * d21 - d01 * d20) / denom;
		return barycentricOut;
	}

	/**
	 * Returns the lowest positive root of the quadric equation given by a* x *
	 * x + b * x + c = 0. If no solution is given Float.Nan is returned.
	 * 
	 * @param a
	 *            the first coefficient of the quadric equation
	 * @param b
	 *            the second coefficient of the quadric equation
	 * @param c
	 *            the third coefficient of the quadric equation
	 * @return the lowest positive root or Float.Nan
	 */
	static public float lowestPositiveRoot(float a, float b, float c) {
		float det = b * b - 4 * a * c;
		if (det < 0)
			return Float.NaN;

		float sqrtD = (float) Math.sqrt(det);
		float invA = 1 / (2 * a);
		float r1 = (-b - sqrtD) * invA;
		float r2 = (-b + sqrtD) * invA;

		if (r1 > r2) {
			float tmp = r2;
			r2 = r1;
			r1 = tmp;
		}

		if (r1 > 0)
			return r1;
		if (r2 > 0)
			return r2;
		return Float.NaN;
	}

	public static Vector2 triangleCentroid(float x1, float y1, float x2,
			float y2, float x3, float y3, Vector2 centroid) {
		centroid.x = (x1 + x2 + x3) / 3;
		centroid.y = (y1 + y2 + y3) / 3;
		return centroid;
	}

	public static Vector2 quadrilateralCentroid(float x1, float y1, float x2,
			float y2, float x3, float y3, float x4, float y4, Vector2 centroid) {
		float avgX1 = (x1 + x2 + x3) / 3;
		float avgY1 = (y1 + y2 + y3) / 3;
		float avgX2 = (x1 + x4 + x3) / 3;
		float avgY2 = (y1 + y4 + y3) / 3;
		centroid.x = avgX1 - (avgX1 - avgX2) / 2;
		centroid.y = avgY1 - (avgY1 - avgY2) / 2;
		return centroid;
	}

	/** Returns the centroid for the specified non-self-intersecting polygon. */
	public static Vector2 polygonCentroid(float[] polygon, int offset,
			int count, Vector2 centroid) {
		if (polygon.length < 6)
			throw new IllegalArgumentException(
					"A polygon must have 3 or more coordinate pairs.");
		float x = 0, y = 0;

		float signedArea = 0;
		int i = offset;
		for (int n = offset + count - 2; i < n; i += 2) {
			float x0 = polygon[i];
			float y0 = polygon[i + 1];
			float x1 = polygon[i + 2];
			float y1 = polygon[i + 3];
			float a = x0 * y1 - x1 * y0;
			signedArea += a;
			x += (x0 + x1) * a;
			y += (y0 + y1) * a;
		}

		float x0 = polygon[i];
		float y0 = polygon[i + 1];
		float x1 = polygon[offset];
		float y1 = polygon[offset + 1];
		float a = x0 * y1 - x1 * y0;
		signedArea += a;
		x += (x0 + x1) * a;
		y += (y0 + y1) * a;

		signedArea *= 0.5f;
		centroid.x = x / (6 * signedArea);
		centroid.y = y / (6 * signedArea);
		return centroid;
	}

	public static ShortArray isPolygonSimple(FloatArray vertices, boolean continuo)
	{
		ShortArray listaCortes = new ShortArray();
		FloatArray listaVertices = new FloatArray(vertices);

		if (!continuo)
		{
			listaVertices.add(vertices.get(0));
			listaVertices.add(vertices.get(1));
		}

		int i = 0;
		while (i < listaVertices.size - 4)
		{
			float p1x = listaVertices.get(i);
			float p1y = listaVertices.get(i + 1);
			float p2x = listaVertices.get(i + 2);
			float p2y = listaVertices.get(i + 3);

			int j = i + 4;
			while (j < listaVertices.size - 2)
			{
				float p3x = listaVertices.get(j);
				float p3y = listaVertices.get(j + 1);
				float p4x = listaVertices.get(j + 2);
				float p4y = listaVertices.get(j + 3);

				if (i != 0 || j + 3 != listaVertices.size - 1)
				{
					if (Intersector.intersectSegments(new Vector2(p1x, p1y), new Vector2(p2x, p2y), new Vector2(p3x, p3y), new Vector2(p4x, p4y), null))
					{
						listaCortes.add(i / 2);
						listaCortes.add(i / 2 + 1);
						listaCortes.add(j / 2);
						listaCortes.add((j / 2 + 1) % (vertices.size / 2));
					}
				}

				j = j + 2;
			}

			i = i + 2;
		}

		return listaCortes;
	}

	public static boolean isPointInsideMesh(ShortArray contorno, FloatArray vertices, float x, float y)
	{
		Array<Vector2> polygon = new Array<Vector2>(2 * contorno.size);
		int j = 0;
		while (j < contorno.size)
		{
			int pos = contorno.get(j);
			polygon.add(new Vector2(vertices.get(2 * pos), vertices.get(2 * pos + 1)));
			j = j + 2;
		}

		return Intersector.isPointInPolygon(polygon, new Vector2(x, y));
	}
}