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
package org.eclipse.ui.internal.texteditor.quickdiff;


import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.source.ILineDiffInfo;
import org.eclipse.jface.text.source.ILineDiffer;
import org.eclipse.jface.text.source.IVerticalRulerInfo;

import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Action that will revert a line in the currently displayed document to the state in the
 * reference document.
 * 
 * @since 3.0
 */
public class RevertLineAction extends QuickDiffRestoreAction {
	/** Resource key prefix. */
	private static final String PREFIX= "RevertLineAction."; //$NON-NLS-1$
	/** Resource key for added lines - they will be deleted. */
	private static final String DELETE_KEY= PREFIX + "delete.label"; //$NON-NLS-1$
	/** Resource key for changed lines - they will be reverted. */
	private static final String REVERT_KEY= PREFIX + "label"; //$NON-NLS-1$

	/** The line to be restored. Set in <code>update()</code>. */
	private int fLine;

	/**
	 * Creates a new instance.
	 * 
	 * @param editor the editor this action belongs to
	 */
	public RevertLineAction(ITextEditor editor) {
		super(QuickDiffMessages.getResourceBundle(), PREFIX, editor);
	}

	/*
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		super.update();
		
		if (!isEnabled())
			return;
		
		setEnabled(false);
		IVerticalRulerInfo ruler= getRuler();
		if (ruler == null)
			return;
		fLine= ruler.getLineOfLastMouseButtonActivity();
		ILineDiffer differ= getDiffer();
		if (differ == null)
			return;
		ILineDiffInfo info= differ.getLineInfo(fLine);
		if (info != null && info.getChangeType() != ILineDiffInfo.UNCHANGED) {
			if (info.getChangeType() == ILineDiffInfo.ADDED)
				setText(QuickDiffMessages.getString(DELETE_KEY));
			else
				setText(QuickDiffMessages.getString(REVERT_KEY));
			setEnabled(true);
		}
	}

	/*
	 * @see org.eclipse.ui.internal.editors.quickdiff.QuickDiffRestoreAction#runCompoundChange()
	 */
	public void runCompoundChange() {
		if (!isEnabled())
			return;
		ILineDiffer differ= getDiffer();
		if (differ != null) {
			try {
				differ.revertLine(fLine);
			} catch (BadLocationException e) {
				setStatus(e.getMessage());
			}
		}
	}
}