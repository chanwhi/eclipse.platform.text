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
package org.eclipse.jface.text;


/**
 * Interface for storing and managing text.
 * <p>
 * Provides access to the stored text and allows to manipulate it. Clients may
 * implement this interface or use {@link org.eclipse.jface.text.GapTextStore} and
 * {@link org.eclipse.jface.text.SequentialRewriteTextStore}.
 */
public interface ITextStore {
	
	/**
	 * Returns the character at the specified offset.
	 *
	 * @param offset the offset in this text store
	 * @return the character at this offset
	 */
	char get(int offset);
	
	/**
	 * Returns the text of the specified character range.
	 *
	 * @param offset the offset of the range
	 * @param length the length of the range
	 * @return the text of the range
	 */
	String get(int offset, int length);
	
	/**
	 * Returns number of characters stored in this text store.
	 *
	 * @return the number of characters stored in this text store
	 */
	int getLength();
	
	/**
	 * Replaces the specified character range with the given text.
	 * <code>replace(getLength(), 0, "some text")</code> is a valid
	 * call and appends text to the end of the text store.
	 *
	 * @param offset the offset of the range to be replaced
	 * @param length the number of characters to be replaced
	 * @param text the substitution text
	 */
	void replace(int offset, int length, String text);
	
	/**
	 * Replace the content of the text store with the given text.
	 * Convenience method for <code>replace(0, getLength(), text</code>.
	 *
	 * @param text the new content of the text store
	 */
	void set(String text);
}