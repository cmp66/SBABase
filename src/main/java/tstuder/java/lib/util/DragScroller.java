package tstuder.java.lib.util;

/*
 * This DragScroller class helps implement mouse-based scrolling of graphical content
 * by means of simulating arrow key events.
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

import java.awt.*;
import java.awt.event.*;

/**
 * A utility  class to handle automatic horizontal and/or vertical scrolling
 * of some class implementing the KeyListener interface 
 * as the mouse is dragged outside a specified rectangle.
 * <p>Dragging in the listener class is achieved by simulating appropriate key
 * events.
 *
 * <p><b>Revision History</b></p>
 * <pre>
 * &nbsp; ts, 1999-06-01, v1.0,    First version.
 * </pre>
 * 
 * @author	Thomas Studer (ts)
 */
public class DragScroller {

	private Component		source;
	
	private KeyListener	listener;
	
	private Rectangle 	componentArea;

	private int 			modifiers;

	/**
	 * -1 --> drag left, 0 --> no dragging, 1 --> drag right
	 */
	private int				dragHorizontally;

	/**
	 * -1 --> drag up, 0 --> no dragging, 1 --> drag down
	 */
	private int				dragVertically;

/**
 * Construct a DragScroller
 *
 * @param componentArea The rectangle dragging outside of which triggers 
 * scrolling.
 * @param source The source object of the generated key events.
 * Usually the same as 'listener'.
 * @param listener DragScroller's client class expecting key events simulating
 * automatic dragging. 
 */
public DragScroller( Rectangle componentArea, Component source, KeyListener listener ) {

	this.componentArea = componentArea;
	this.source = source;
	this.listener = listener;
}
/**
 * This method is usually called in response to a MouseDragged event received by
 * the draggable client of this class. It starts dragging of the client if
 * necessary.
 *
 * @param mouseX The new mouse X coordinate.
 * @param mouseY The new mouse Y coordinate.
 * @param modifiers The modifiers used in the generated key events.
 * @return <code>true</code> if DragScroller is currently dragging, i.e. the mouse
 * lies outside the relevant component area, <code>false</code> otherwise.
 */
@SuppressWarnings("unused")
public boolean drag( int mouseX, int mouseY, int modifiers ) {

	int 		distance = 1;
	double 	xDelta 	= 0.0;
	double 	yDelta 	= 0.0;

	this.modifiers = modifiers;
	
	if (mouseX > componentArea.x + componentArea.width) {

		dragHorizontally = 1;
		xDelta = mouseX - (componentArea.x + componentArea.width);

	} else if (mouseX < componentArea.x) {

		dragHorizontally = -1;
		xDelta = componentArea.x - mouseX;

	} else dragHorizontally = 0;

	
	if (mouseY > componentArea.y + componentArea.height) {

		dragVertically = 1;
		yDelta = mouseY - (componentArea.y + componentArea.height);

	} else if (mouseY < componentArea.y) {

		dragVertically = -1;
		yDelta = componentArea.y - mouseY;

	} else dragVertically = 0;

	
	if (dragHorizontally != 0 || dragVertically != 0) {

		//distance = (int) Math.sqrt( xDelta * xDelta + yDelta * yDelta );

		postKeyEvent();
		return true;

	} else {

		return false;
	}
}
/**
 * The Thread's run() method.
 */
@SuppressWarnings("deprecation")
public void postKeyEvent() {

    
    
	int keyCode = KeyEvent.VK_UNDEFINED;
	
	if (dragHorizontally > 0) {

		keyCode = KeyEvent.VK_RIGHT;

	} else if (dragHorizontally < 0) {

		keyCode = KeyEvent.VK_LEFT;
	}

	if (keyCode != KeyEvent.VK_UNDEFINED) {
	    
		listener.keyPressed( new KeyEvent( source, KeyEvent.KEY_PRESSED, 
			(new java.util.Date()).getTime(), modifiers, keyCode ));
	}

	keyCode = KeyEvent.VK_UNDEFINED;
	
	if (dragVertically > 0) {

		keyCode = KeyEvent.VK_DOWN;

	} else if (dragVertically < 0) {

		keyCode = KeyEvent.VK_UP;
	}

	if (keyCode != KeyEvent.VK_UNDEFINED) {

		listener.keyPressed( new KeyEvent( source, KeyEvent.KEY_PRESSED, 
			(new java.util.Date()).getTime(), modifiers, keyCode ));
	}
}
}
