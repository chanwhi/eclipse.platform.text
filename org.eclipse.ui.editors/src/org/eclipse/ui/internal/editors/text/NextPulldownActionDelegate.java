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

package org.eclipse.ui.internal.editors.text;

import org.eclipse.ui.texteditor.AnnotationPreference;

/**
 * The next pulldown action delegate.
 * 
 * @since 3.0
 */
public class NextPulldownActionDelegate extends NextPreviousPulldownActionDelegate {

	/*
	 * @see org.eclipse.ui.internal.texteditor.NextPreviousPulldownActionDelegate#getPreferenceKey(AnnotationPreference)
	 */
	public String getPreferenceKey(AnnotationPreference annotationPreference) {
		return annotationPreference.getIsGoToNextNavigationTargetKey();
	}
}