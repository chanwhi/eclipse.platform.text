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
package org.eclipse.jface.text.hyperlink;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;


/**
 * A hyperlink detector tries to find a hyperlink at
 * a given location in a given text viewer.
 * <p>
 * Clients may implement this interface.
 * </p>
 * <p>
 * NOTE: This API is work in progress and may change before the final API freeze. (FIXME)
 * </p>
 * 
 * @since 3.1
 */
public interface IHyperlinkDetector {
	
	/**
	 * Tries to detect hyperlinks for the given region in
	 * the given text viewer and returns them.
	 * <p>
	 * In most of the cases only one hyperlink should be returned.
	 * </p>
	 * @param textViewer the text viewer on which the hover popup should be shown
	 * @param region the text range in the text viewer which is used to detect the hyperlinks
	 * @return the hyperlinks or <code>null</code> if no hyperlink was detected
	 */
	IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region);

}