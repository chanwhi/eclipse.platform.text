/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.text.projection;


import org.eclipse.jface.text.Position;


/**
 * Internal class. Do not use. Only public for testing purposes.
 * <p>
 * A segment is the image of a master document fragment in a projection
 * document.
 * 
 * @since 3.0
 */
public class Segment extends Position {
	
	/** The corresponding fragment for this segment. */
	public Fragment fragment;
	/** A flag indicating that the segment updater should stretch this segment when a change happens at its boundaries. */
	public boolean isMarkedForStretch;
	/** A flag indicating that the segment updater should shift this segment when a change happends at its boundaries. */
	public boolean isMarkedForShift;
	
	/**
	 * Creates a new segment covering the given range.
	 * 
	 * @param offset the offset of the segment
	 * @param length the length of the segment
	 */
	public Segment(int offset, int length) {
		super(offset, length);
	}
	
	/**
	 * Sets the stretching flag.
	 */
	public void markForStretch() {
		isMarkedForStretch= true;
	}
	
	/**
	 * Returns <code>true</code> if the stretching flag is set, <code>false</code> otherwise.
	 * @return <code>true</code> if the stretching flag is set, <code>false</code> otherwise
	 */
	public boolean isMarkedForStretch() {
		return isMarkedForStretch;
	}
	
	/**
	 * Sets the shifting flag.
	 */
	public void markForShift() {
		isMarkedForShift= true;
	}
	
	/**
	 * Returns <code>true</code> if the shifting flag is set, <code>false</code> otherwise.
	 * @return <code>true</code> if the shifting flag is set, <code>false</code> otherwise
	 */
	public boolean isMarkedForShift() {
		return isMarkedForShift;
	}
	
	/**
	 * Clears the shifting and the streching flag.
	 */
	public void clearMark() {
		isMarkedForStretch= false;
		isMarkedForShift= false;
	}
}