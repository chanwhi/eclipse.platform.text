/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.text.templates;

import org.eclipse.jface.text.Assert;

/**
 * A template consisting of a name and a pattern.
 * <p>
 * Clients may instantiate this class. May become final in the future.
 * </p>
 * @since 3.0
 */
public class Template {
	/* XXX this class should be final or implement Cloneable, or both. */

	/** The name of this template */
	private String fName;
	/** A description of this template */
	private String fDescription;
	/** The name of the context type of this template */
	private String fContextTypeId;
	/** The template pattern. */
	private String fPattern;

	/**
	 * Creates an empty template.
	 */
	public Template() {
		this("", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	
	/**
	 * Creates a copy of a template.
	 * 
	 * @param template the template to copy
	 */
	public Template(Template template) {
		this(template.getName(), template.getDescription(), template.getContextTypeId(), template.getPattern());	
	}

	/**
	 * Creates a template.
	 * 
	 * @param name the name of the template
	 * @param description the description of the template
	 * @param contextTypeId the id of the context type in which the template can be applied
	 * @param pattern the template pattern
	 */		
	public Template(String name, String description, String contextTypeId, String pattern) {
		setDescription(description);
		setName(name);
		setContextTypeId(contextTypeId);
		setPattern(pattern);
	}
	
	/*
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return fName.hashCode() ^ fPattern.hashCode() ^ fContextTypeId.hashCode();
	}

	/**
	 * Sets the description of the template.
	 * 
	 * @param description the new description
	 */
	public void setDescription(String description) {
		Assert.isNotNull(description);
		fDescription= description;
	}
	
	/**
	 * Returns the description of the template.
	 * 
	 * @return the description of the template
	 */
	public String getDescription() {
		return fDescription;
	}
	
	/**
	 * Sets the name of the context type in which the template can be applied.
	 * 
	 * @param contextTypeId the new context type name
	 */
	public void setContextTypeId(String contextTypeId) {
		Assert.isNotNull(contextTypeId);
		fContextTypeId= contextTypeId;
	}
	
	/**
	 * Returns the id of the context type in which the template can be applied.
	 * 
	 * @return the id of the context type in which the template can be applied
	 */
	public String getContextTypeId() {
		return fContextTypeId;
	}

	/**
	 * Sets the name of the template.
	 * 
	 * @param name the name of the template
	 */
	public void setName(String name) {
		fName= name;
	}
			
	/**
	 * Returns the name of the template.
	 * 
	 * @return the name of the template
	 */
	public String getName() {
		return fName;
	}

	/**
	 * Sets the pattern of the template.
	 * 
	 * @param pattern the new pattern of the template
	 */
	public void setPattern(String pattern) {
		fPattern= pattern;
	}
		
	/**
	 * Returns the template pattern.
	 * 
	 * @return the template pattern
	 */
	public String getPattern() {
		return fPattern;
	}
	
	/**
	 * Returns <code>true</code> if template is enabled and matches the context,
	 * <code>false</code> otherwise.
	 * 
	 * @param prefix the prefix (e.g. inside a document) to match
	 * @param contextTypeName the context type name to match
	 * @return <code>true</code> if template is enabled and matches the context,
	 * <code>false</code> otherwise
	 */
	public boolean matches(String prefix, String contextTypeName) {
		return fContextTypeId.equals(contextTypeName);
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Template))
			return false;
			
		Template t= (Template) o;
		if (t == this)
			return true;		

		return t.fName.equals(fName)
				&& t.fPattern.equals(fPattern)
				&& t.fContextTypeId.equals(fContextTypeId) 
				&& t.fDescription.equals(fDescription);
	}
	
}
