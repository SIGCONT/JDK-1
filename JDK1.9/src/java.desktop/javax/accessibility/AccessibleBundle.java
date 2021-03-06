/*
 * Copyright (c) 1997, 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package javax.accessibility;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import sun.awt.AWTAccessor;

/**
 * Base class used to maintain a strongly typed enumeration. This is the
 * superclass of {@link AccessibleState} and {@link AccessibleRole}.
 * <p>
 * The {@link #toDisplayString()} method allows you to obtain the localized
 * string for a locale independent key from a predefined {@code ResourceBundle}
 * for the keys defined in this class. This localized string is intended to be
 * readable by humans.
 *
 * @author Willie Walker
 * @author Peter Korn
 * @author Lynn Monsanto
 * @see AccessibleRole
 * @see AccessibleState
 */
public abstract class AccessibleBundle {

    private static Hashtable<Locale, Hashtable<String, Object>> table = new Hashtable<>();

    private final String defaultResourceBundleName
        = "com.sun.accessibility.internal.resources.accessibility";

    static {
        AWTAccessor.setAccessibleBundleAccessor(
                new AWTAccessor.AccessibleBundleAccessor() {

                    @Override
                    public String getKey(AccessibleBundle accessibleBundle) {
                        return accessibleBundle.key;
                    }
                });
    }

    /**
     * Construct an {@code AccessibleBundle}.
     */
    public AccessibleBundle() {
    }

    /**
     * The locale independent name of the state. This is a programmatic name
     * that is not intended to be read by humans.
     *
     * @see #toDisplayString
     */
    protected String key = null;

    /**
     * Obtains the key as a localized string. If a localized string cannot be
     * found for the key, the locale independent key stored in the role will be
     * returned. This method is intended to be used only by subclasses so that
     * they can specify their own resource bundles which contain localized
     * strings for their keys.
     *
     * @param  resourceBundleName the name of the resource bundle to use for
     *         lookup
     * @param  locale the locale for which to obtain a localized string
     * @return a localized string for the key
     */
    protected String toDisplayString(String resourceBundleName,
                                     Locale locale) {

        // loads the resource bundle if necessary
        loadResourceBundle(resourceBundleName, locale);

        // returns the localized string
        Hashtable<String, Object> ht = table.get(locale);
        if (ht != null) {
            Object o = ht.get(key);
            if (o != null && o instanceof String) {
                return (String)o;
            }
        }
        return key;
    }

    /**
     * Obtains the key as a localized string. If a localized string cannot be
     * found for the key, the locale independent key stored in the role will be
     * returned.
     *
     * @param  locale the locale for which to obtain a localized string
     * @return a localized string for the key
     */
    public String toDisplayString(Locale locale) {
        return toDisplayString(defaultResourceBundleName, locale);
    }

    /**
     * Gets localized string describing the key using the default locale.
     *
     * @return a localized string describing the key using the default locale
     */
    public String toDisplayString() {
        return toDisplayString(Locale.getDefault());
    }

    /**
     * Gets localized string describing the key using the default locale.
     *
     * @return a localized string describing the key using the default locale
     * @see #toDisplayString
     */
    public String toString() {
        return toDisplayString();
    }

    /**
     * Loads the Accessibility resource bundle if necessary.
     */
    private void loadResourceBundle(String resourceBundleName,
                                    Locale locale) {
        if (! table.contains(locale)) {

            try {
                Hashtable<String, Object> resourceTable = new Hashtable<>();

                ResourceBundle bundle = ResourceBundle.getBundle(resourceBundleName, locale);

                Enumeration<String> iter = bundle.getKeys();
                while(iter.hasMoreElements()) {
                    String key = iter.nextElement();
                    resourceTable.put(key, bundle.getObject(key));
                }

                table.put(locale, resourceTable);
            }
            catch (MissingResourceException e) {
                System.err.println("loadResourceBundle: " + e);
                // Just return so toDisplayString() returns the
                // non-localized key.
                return;
            }
        }
    }
}
