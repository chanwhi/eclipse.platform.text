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

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.ui.editors.text.ILocationProvider;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * @since 3.0
 */
public class JavaFileEditorInput implements IEditorInput, ILocationProvider {

	/**
	 * The workbench adapter which simply provides the label.
	 * 
	 * @since 3.1
	 */
	private class WorkbenchAdapter implements IWorkbenchAdapter {
		/*
		 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object o) {
			return null;
		}
	
		/*
		 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
		 */
		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}
	
		/*
		 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
		 */
		public String getLabel(Object o) {
			return ((JavaFileEditorInput)o).getName();
		}
	
		/*
		 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
		 */
		public Object getParent(Object o) {
			return null;
		}
	}

	private File fFile;
	private WorkbenchAdapter fWorkbenchAdapter= new WorkbenchAdapter();

	public JavaFileEditorInput(File file) {
		super();
		fFile= file;
		fWorkbenchAdapter= new WorkbenchAdapter();
	}
	/*
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		return fFile.exists();
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		return fFile.getName();
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return fFile.getAbsolutePath();
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (ILocationProvider.class.equals(adapter))
			return this;
		if (IWorkbenchAdapter.class.equals(adapter))
			return fWorkbenchAdapter;
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/*
	 * @see org.eclipse.ui.editors.text.ILocationProvider#getPath(java.lang.Object)
	 */
	public IPath getPath(Object element) {
		if (element instanceof JavaFileEditorInput) {
			JavaFileEditorInput input= (JavaFileEditorInput) element;
			return new Path(input.fFile.getAbsolutePath());
		}
		return null;
	}
	
	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o == this)
			return true;
		
		if (o instanceof JavaFileEditorInput) {
			JavaFileEditorInput input = (JavaFileEditorInput) o;
			return fFile.equals(input.fFile);		
		}
		
		return false;
	}
	
	/*
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return fFile.hashCode();
	}
}