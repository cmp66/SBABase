package tstuder.java.lib.graphics;

/*
 * This class contains various graphics and color utility methods.
 *
 * Copyright (C) 1999 Thomas Studer
 * mailto:tstuder@datacomm.ch
 * http://www.datacomm.ch/tstuder
 *
 * This class is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This class is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this class; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.awt.Color;

/**
 * General graphics and color utility functions.
 *
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-04-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class GraphicsUtil {

	private static final Color 	WHITE_BRIGHTER = new Color(200, 200, 200);
	private static final Color 	WHITE_DARKER = new Color(140, 140, 140);
	private static final Color 	BLACK_BRIGHTER = new Color(125, 125, 125);
	private static final Color 	BLACK_DARKER = new Color(75, 75, 75);

	private static final double 	FACTOR = 0.5;
/**
 * Lighten up a color.
 *
 * @return A new, brighter variant of the Color passed in the 'color' parameter.
 * @param color The color to lighten up.
 */
public static Color brighter( Color color ) {

	if (color.equals(Color.white)) 
		return WHITE_BRIGHTER;
	else if (color.equals(Color.black)) 
		return BLACK_BRIGHTER;
	else {
		// Brighten up dark colors very
		// quickly; lighten up bright colors only a bit.
		int red = color.getRed();
		red += (int) ((255 - red) * FACTOR);
		int blue = color.getBlue();
		blue += (int) ((255 - blue) * FACTOR);
		int green = color.getGreen();
		green += (int) ((255 - green) * FACTOR);

		return new Color(Math.min(red, 255),
						 Math.min(green, 255),
						 Math.min(blue, 255));
	}
}
/**
 * Darken a color.
 *
 * @return A new, darker variant of the Color passed in the 'color' parameter.
 * @param color The Color to darken.
 */
public static Color darker( Color color ) {

	if (color.equals(Color.white)) 
		return WHITE_DARKER;
	else if (color.equals(Color.black)) 
		return BLACK_DARKER;
	else 
		return color.darker();
}
}
