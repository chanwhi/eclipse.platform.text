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

import java.util.ResourceBundle;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;

import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.IChangeRulerColumn;
import org.eclipse.jface.text.source.ILineDiffer;
import org.eclipse.jface.text.source.IVerticalRulerInfo;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

/**
 * Abstract superclass of actions that restore / revert parts of a document displayed in the action's
 * editor to the state described by the {@link ILineDiffer ILineDiffer} associated with the document's 
 * {@link IAnnotationModel IAnnotationModel}.
 * 
 * @since 3.0
 */
public abstract class QuickDiffRestoreAction extends TextEditorAction {
	
	/**
	 * Creates a new instance.
	 * 
	 * @param bundle the resource bundle
	 * @param prefix a prefix to be prepended to the various resource keys
	 * @param editor the editor this action belongs to
	 */
	QuickDiffRestoreAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}
	
	/**
	 * Called by this action's run method inside a pair of calls to <code>IRewriteTarget.beginCompoundChange</code>
	 * and <code>IRewriteTarget.endCompoundChange</code>().
	 *
	 * @see IRewriteTarget
	 */
	protected abstract void runCompoundChange();
	
	/*
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		ITextEditor editor= getTextEditor();
		if (editor == null || !validateEditorInputState())
			return;
		IRewriteTarget target= (IRewriteTarget)editor.getAdapter(IRewriteTarget.class);
		if (target != null)
			target.beginCompoundChange();
		runCompoundChange();
		if (target != null) 
			target.endCompoundChange();
		
	}
	
	/*
	 * @see org.eclipse.ui.texteditor.TextEditorAction#update()
	 */
	public void update() {
		super.update();
		if (isEnabled())
			setEnabled(canModifyEditor());
	}

	/**
	 * Returns the selection of the editor this action belongs to.
	 * 
	 * @return the editor's selection, or <code>null</code>
	 */
	protected ITextSelection getSelection() {
		if (getTextEditor() == null)
			return null;
		ISelectionProvider sp= getTextEditor().getSelectionProvider();
		if (sp == null)
			return null;
		ISelection s= sp.getSelection();
		if (s instanceof ITextSelection)
			return (ITextSelection)s;
		return null;
	}

	/**
	 * Returns the annotation model of the document displayed in this action's editor, if it
	 * implements the {@link IAnnotationModelExtension IAnnotationModelExtension} interface.
	 * 
	 * @return the displayed document's annotation model if it is an <code>IAnnotationModelExtension</code>, or <code>null</code>
	 */
	private IAnnotationModelExtension getModel() {
		if (getTextEditor() == null)
			return null;
		IDocumentProvider provider= getTextEditor().getDocumentProvider();
		IEditorInput editorInput= getTextEditor().getEditorInput();
		IAnnotationModel m= provider.getAnnotationModel(editorInput);
		if (m instanceof IAnnotationModelExtension)
			return (IAnnotationModelExtension)m;
		return null;
	}

	/**
	 * Returns the diff model associated with the annotation model of the document currently displayed
	 * in this action's editor, if any.
	 * 
	 * @return the diff model associated with the displayed document, or <code>null</code>
	 */
	protected ILineDiffer getDiffer() {
		IAnnotationModelExtension extension= getModel();
		if (extension != null)
			return (ILineDiffer)extension.getAnnotationModel(IChangeRulerColumn.QUICK_DIFF_MODEL_ID);
		return null;
	}

	/**
	 * Returns a <code>IVerticalRulerInfo</code> if this action's editor adapts to one.
	 * 
	 * @return the <code>IVerticalRulerInfo</code> for the editor's vertical ruler, or <code>null</code>
	 */
	protected IVerticalRulerInfo getRuler() {
		if (getTextEditor() != null)
			return (IVerticalRulerInfo)getTextEditor().getAdapter(IVerticalRulerInfo.class);
		return null;
	}

	/**
	 * Sets the status line error message to <code>string</code>.
	 * 
	 * @param string the message to be displayed as error.
	 */
	protected void setStatus(String string) {
		if (getTextEditor() != null) {
			IEditorStatusLine statusLine= (IEditorStatusLine) getTextEditor().getAdapter(IEditorStatusLine.class);
			if (statusLine != null) {
				statusLine.setMessage(true, string, null);
			}
		}
	}
}