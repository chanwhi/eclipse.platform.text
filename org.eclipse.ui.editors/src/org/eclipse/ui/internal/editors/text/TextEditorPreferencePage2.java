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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.PreferencePage;

import org.eclipse.jface.text.Assert;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.editors.text.ITextEditorHelpContextIds;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceDialog;
import org.eclipse.ui.internal.editors.text.TextEditorPreferencePage2.EnumeratedDomain.EnumValue;

/**
 * The preference page for setting the editor options.
 * <p>
 * This class is internal and not intended to be used by clients.</p>
 * 
 * @since 2.1
 */
public class TextEditorPreferencePage2 extends PreferencePage implements IWorkbenchPreferencePage {
	
	private abstract class Initializer {

		protected final Preference fPreference;
		
		protected Initializer(Preference preference) {
			fPreference= preference;
		}
		
		public abstract void initialize();
	}
	

	public final class InitializerFactory {
		private class TextInitializer extends Initializer {
			private final Text fText;

			public TextInitializer(Preference preference, Text control) {
				super(preference);
				fText= control;
			}
			public void initialize() {
				String value= fOverlayStore.getString(fPreference.getKey());
				fText.setText(value);
			}
		}

		private class CheckboxInitializer extends Initializer {
			private final Button fControl;

			public CheckboxInitializer(Preference preference, Button control) {
				super(preference);
				fControl= control;
			}
			public void initialize() {
				boolean value= fOverlayStore.getBoolean(fPreference.getKey());
				fControl.setSelection(value);
			}
		}

		private class ComboInitializer extends Initializer {
			private final Combo fControl;
			private final EnumeratedDomain fDomain;

			public ComboInitializer(Preference preference, Combo control, EnumeratedDomain domain) {
				super(preference);
				fControl= control;
				fDomain= domain;
			}
			public void initialize() {
				int value= fOverlayStore.getInt(fPreference.getKey());
				EnumValue enumValue= fDomain.getValueByInteger(value);
				if (enumValue != null) {
					int index= fDomain.getIndex(enumValue);
					if (index >= 0)
						fControl.select(index);
				}
			}
		}

		public Initializer create(Preference preference, Text control) {
			return new TextInitializer(preference, control);
		}

		public Initializer create(Preference preference, Button control) {
			return new CheckboxInitializer(preference, control);
		}
		
		public Initializer create(Preference preference, Combo control, EnumeratedDomain domain) {
			return new ComboInitializer(preference, control, domain);
		}
	}
	
	
	abstract static class Domain {
		public abstract IStatus validate(Object value);
		protected int parseInteger(Object val) throws NumberFormatException {
			if (val instanceof Integer) {
				return ((Integer) val).intValue();
			}
			if (val instanceof String) {
				return Integer.parseInt((String) val);
			}
			throw new NumberFormatException("Value must be an integer");
		}
	}
	
	static class IntegerDomain extends Domain {
		// TODO convert to Spinner as soon as it becomes available
		private final int fMax;
		private final int fMin;
		public IntegerDomain(int min, int max) {
			Assert.isLegal(max >= min);
			fMax= max;
			fMin= min;
		}

		public IStatus validate(Object val) {
			StatusInfo status= new StatusInfo();
			try {
				int integer= parseInteger(val);
				if (!rangeCheck(integer))
					status.setError(TextEditorMessages.getFormattedString("TextEditorPreferencePage.invalid_input", String.valueOf(integer))); //$NON-NLS-1$
			} catch (NumberFormatException e) {
					status.setError(TextEditorMessages.getFormattedString("TextEditorPreferencePage.invalid_input", String.valueOf(val))); //$NON-NLS-1$
			}
			return status;
		}
		
		protected boolean rangeCheck(int i) {
			return (i >= fMin && i <= fMax);
		}

	}
	
	static class EnumeratedDomain extends Domain {
		public final static class EnumValue {
			private final int fValue;
			private final String fName;
			public EnumValue(int value) {
				this(value, null);
			}
			public EnumValue(int value, String name) {
				fValue= value;
				fName= name;
			}
			public String getLabel() {
				return fName == null ? String.valueOf(fValue) : fName;
			}
			public int getIntValue() {
				return fValue;
			}
			public final int hashCode() {
				return getIntValue();
			}
			public boolean equals(Object obj) {
				if (obj instanceof EnumValue) {
					return ((EnumValue) obj).getIntValue() == fValue;
				}
				return false;
			}
		}
		
		private final java.util.List fItems= new ArrayList();
		private final Set fValueSet= new HashSet();
		
		public void addValue(EnumValue val) {
			if (fValueSet.contains(val))
				fItems.remove(val);
			fItems.add(val);
			fValueSet.add(val);
		}

		public int getIndex(EnumValue enumValue) {
			int i= 0;
			for (Iterator it= fItems.iterator(); it.hasNext();) {
				EnumValue ev= (EnumValue) it.next();
				if (ev.equals(enumValue))
					return i; 
				i++;
			}
			return -1;
		}
		
		public EnumValue getValueByIndex (int index) {
			if (index >= 0 && fItems.size() > index)
				return (EnumValue) fItems.get(index);
			return null;
		}

		public EnumValue getValueByInteger(int intValue) {
			for (Iterator it= fItems.iterator(); it.hasNext();) {
				EnumValue e= (EnumValue) it.next();
				if (e.getIntValue() == intValue)
					return e;
			}
			return null;
		}

		public void addValue(int val) {
			addValue(new EnumValue(val));
		}
		
		public void addRange(int from, int to) {
			while (from <= to)
				addValue(from++);
		}

		public Control createControl(Composite parent, final Preference pref, final Preferences prefs) {
			Composite composite= new Composite(parent, SWT.NONE);
			GridLayout layout= new GridLayout(2, false);
			layout.horizontalSpacing= 5;
			layout.marginHeight= 0;
			layout.marginWidth= 0;
			composite.setLayout(layout);
			
			Label labelControl= new Label(composite, SWT.NONE);
			labelControl.setText(pref.getName());
			GridData gd= new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
			labelControl.setLayoutData(gd);
			
			final Combo combo= new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
			gd= new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
			combo.setLayoutData(gd);
			combo.setToolTipText(pref.getDescription());
			for (Iterator it= fItems.iterator(); it.hasNext();) {
				EnumValue value= (EnumValue) it.next();
				combo.add(value.getLabel());
			}
			int val= prefs.getInt(pref.getKey());
			int selection;
			if (val >= 0 && val < fItems.size())
				selection= val;
			else
				selection= 0;
			combo.select(selection);
			
			combo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int index= combo.getSelectionIndex();
					EnumValue value= (EnumValue) fItems.get(index);
					prefs.setValue(pref.getKey(), value.getIntValue());
				}
			});
			
			return composite;
		}

		public IStatus validate(Object value) {
			StatusInfo status= new StatusInfo();
			try {
				EnumValue e= parseEnumValue(value);
				if (!fValueSet.contains(e))
					status.setError("Value must be in between " + getValueByIndex(0).getLabel() + " and " + getValueByIndex(fItems.size() - 1).getLabel());
			} catch (NumberFormatException e) {
				status.setError(TextEditorMessages.getFormattedString("TextEditorPreferencePage.invalid_input", String.valueOf(value))); //$NON-NLS-1$
			}
			
			return status;
		}
		
		private EnumValue parseEnumValue(Object value) {
			if (value instanceof EnumValue)
				return (EnumValue) value;
			int integer= parseInteger(value);
			return getValueByInteger(integer);
		}
	}
	
	static class BooleanDomain extends Domain {
		public Control createControl(Composite parent, final Preference pref, final Preferences prefs) {
			final Button checkbox= new Button(parent, SWT.CHECK);
			checkbox.setText(pref.getName());
			checkbox.setToolTipText(pref.getDescription());
			checkbox.setSelection(prefs.getBoolean(pref.getKey()));
			
			checkbox.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					boolean val= checkbox.getSelection();
					prefs.setValue(pref.getKey(), val);
				}
			});
			return checkbox;
		}

		public IStatus validate(Object value) {
			StatusInfo status= new StatusInfo();
			try {
				parseBoolean(value);
			} catch (NumberFormatException e) {
				status.setError(TextEditorMessages.getFormattedString("TextEditorPreferencePage.invalid_input", String.valueOf(value))); //$NON-NLS-1$)
			}
			
			return status;
		}

		private boolean parseBoolean(Object value) throws NumberFormatException {
			if (value instanceof Boolean)
				return ((Boolean) value).booleanValue();
			
			if (value instanceof String) {
				if (Boolean.TRUE.toString().equalsIgnoreCase((String) value))
					return true;
				if (Boolean.FALSE.toString().equalsIgnoreCase((String) value))
					return false;
			}
			
			throw new NumberFormatException(value + " is not a boolean");
		}
	}
	
	private static class Preference {
		private String fKey;
		private String fName;
		private String fDescription; // for tooltips
		
		public Preference(String key, String name, String description) {
			Assert.isNotNull(key);
			Assert.isNotNull(name);
			fKey= key;
			fName= name;
			fDescription= description;
		}
		public final String getKey() {
			return fKey;
		}
		public final String getName() {
			return fName;
		}
		public final String getDescription() {
			return fDescription;
		}
	}
	
	private OverlayPreferenceStore fOverlayStore;
	
	/**
	 * Tells whether the fields are initialized.
	 * @since 3.0
	 */
	private boolean fFieldsInitialized= false;
	
	private java.util.List fInitializers= new ArrayList();
	
	private InitializerFactory fInitializerFactory= new InitializerFactory();

	private Control fContents;

	
	public TextEditorPreferencePage2() {
		setDescription(TextEditorMessages.getString("TextEditorPreferencePage.description")); //$NON-NLS-1$
		setPreferenceStore(EditorsPlugin.getDefault().getPreferenceStore());
		
		fOverlayStore= createOverlayStore();
	}
	
	private OverlayPreferenceStore createOverlayStore() {
		
		ArrayList overlayKeys= new ArrayList();
		
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.INT, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH));

		OverlayPreferenceStore.OverlayKey[] keys= new OverlayPreferenceStore.OverlayKey[overlayKeys.size()];
		overlayKeys.toArray(keys);
		return new OverlayPreferenceStore(getPreferenceStore(), keys);
	}
	
	/*
	 * @see IWorkbenchPreferencePage#init()
	 */	
	public void init(IWorkbench workbench) {
	}

	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		WorkbenchHelp.setHelp(getControl(), ITextEditorHelpContextIds.TEXT_EDITOR_PREFERENCE_PAGE);
	}

	protected Preferences getPreferences() {
		return new Preferences();
	}
	
	
	protected Label createDescriptionLabel(Composite parent) {
		return null; // since we supply a link text introduction
	}

	private Control createAppearancePage(Composite parent) {

		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		composite.setLayout(layout);
		
		
		Control description= createLinkText(composite, new Object[] {
				"Text editor preferences. Note that some settings are configured on the ", 
				new String[] {"general text editor preference page", "org.eclipse.ui.preferencePages.GeneralTextEditor", "Go to the text editor preferences" },
				"."});
		GridData gd= new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gd.widthHint= 150; // only expand further if anyone else requires it
		gd.horizontalSpan= 2;
		description.setLayoutData(gd);
		
		Label spacer= new Label(composite, SWT.LEFT );
		gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= 2;
		gd.heightHint= convertHeightInCharsToPixels(1) / 2;
		spacer.setLayoutData(gd);
		
		if (false) {
			// TODO create an inherited preference that defaults to its ancestor when set to -1
			String label= TextEditorMessages.getString("TextEditorPreferencePage.displayedTabWidth"); //$NON-NLS-1$
			Preference tabWidth= new Preference(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, label, null);
			EnumeratedDomain tabWidthDomain= new EnumeratedDomain();
			tabWidthDomain.addValue(new EnumValue(-1, "Default"));
			tabWidthDomain.addRange(1, 16);
			addCombo(composite, tabWidth, tabWidthDomain, 0);
		}
		
		return composite;
	}
	
	private Control createLinkText(Composite contents, Object[] tokens) {
		Composite description= new Composite(contents, SWT.NONE);
		RowLayout rowLayout= new RowLayout(SWT.HORIZONTAL);
		rowLayout.justify= false;
		rowLayout.fill= true;
		rowLayout.marginBottom= 0;
		rowLayout.marginHeight= 0;
		rowLayout.marginLeft= 0;
		rowLayout.marginRight= 0;
		rowLayout.marginTop= 0;
		rowLayout.marginWidth= 0;
		rowLayout.spacing= 0;
		description.setLayout(rowLayout);
		
		for (int i= 0; i < tokens.length; i++) {
			String text;
			if (tokens[i] instanceof String[]) {
				String[] strings= (String[]) tokens[i];
				text= strings[0];
				final String target= strings[1];
				CHyperLink link= new CHyperLink(description, SWT.NONE);
				link.setText(text);
				link.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						WorkbenchPreferenceDialog.createDialogOn(target);
					}
				});
				if (strings.length > 2)
					link.setToolTipText(strings[2]);
				continue;
			}
			
			text= (String) tokens[i];
			StringTokenizer tokenizer= new StringTokenizer(text);
			while (tokenizer.hasMoreTokens()) {
				Label label= new Label(description, SWT.NONE);
				String token= tokenizer.nextToken();
				label.setText(token + " "); //$NON-NLS-1$
			}
		}
		
		return description;
	}
	
	/*
	 * @see PreferencePage#createContents(Composite)
	 */
		
	protected Control createContents(Composite parent) {
		fOverlayStore.load();
		fOverlayStore.start();
		
		fContents= createAppearancePage(parent);
		initialize();
		Dialog.applyDialogFont(fContents);
		return fContents;
	}
	
	private void initialize() {
		initializeFields();
	}
	
	private void initializeFields() {
		
		for (Iterator it= fInitializers.iterator(); it.hasNext();) {
			Initializer initializer= (Initializer) it.next();
			initializer.initialize();
		}
		
		fFieldsInitialized= true;

		updateStatus(new StatusInfo()); //$NON-NLS-1$
		
	}
	
	/*
	 * @see PreferencePage#performOk()
	 */
	public boolean performOk() {
		fOverlayStore.propagate();
		EditorsPlugin.getDefault().savePluginPreferences();
		return true;
	}
	
	/*
	 * @see PreferencePage#performDefaults()
	 */
	protected void performDefaults() {

		fOverlayStore.loadDefaults();
		
		initializeFields();

		super.performDefaults();
	}
	
	/*
	 * @see DialogPage#dispose()
	 */
	public void dispose() {
		
		if (fOverlayStore != null) {
			fOverlayStore.stop();
			fOverlayStore= null;
		}
		
		super.dispose();
	}
	
	private Combo addCombo(Composite composite, final Preference preference, final EnumeratedDomain domain, int indentation) {		
		Label labelControl= new Label(composite, SWT.NONE);
		labelControl.setText(preference.getName());
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent= indentation;
		labelControl.setLayoutData(gd);

		final Combo combo= new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		combo.setLayoutData(gd);
		combo.setToolTipText(preference.getDescription());
		for (Iterator it= domain.fItems.iterator(); it.hasNext();) {
			EnumValue value= (EnumValue) it.next();
			combo.add(value.getLabel());
		}
		
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index= combo.getSelectionIndex();
				EnumValue value= domain.getValueByIndex(index);
				IStatus status= domain.validate(value);
				if (!status.matches(IStatus.ERROR))
					fOverlayStore.setValue(preference.getKey(), value.getIntValue());
				updateStatus(status);
			}
		});
		
		fInitializers.add(fInitializerFactory.create(preference, combo, domain));
		
		return combo;
	}
	
	void updateStatus(IStatus status) {
		if (!fFieldsInitialized)
			return;
		
		setValid(!status.matches(IStatus.ERROR));
		applyToStatusLine(this, status);
	}

	/**
	 * Applies the status to the status line of a dialog page.
	 * 
	 * @param page the dialog page
	 * @param status the status
	 */
	public void applyToStatusLine(DialogPage page, IStatus status) {
		String message= status.getMessage();
		switch (status.getSeverity()) {
			case IStatus.OK:
				page.setMessage(message, IMessageProvider.NONE);
				page.setErrorMessage(null);
				break;
			case IStatus.WARNING:
				page.setMessage(message, IMessageProvider.WARNING);
				page.setErrorMessage(null);
				break;				
			case IStatus.INFO:
				page.setMessage(message, IMessageProvider.INFORMATION);
				page.setErrorMessage(null);
				break;			
			default:
				if (message.length() == 0) {
					message= null;
				}
				page.setMessage(null);
				page.setErrorMessage(message);
				break;		
		}
	}
}