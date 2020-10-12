/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.sitture.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Simple class that allows us to load classes stored in a jar (or set of jars)
 * 
 * @author timl
 */
public class JarClassLoader extends URLClassLoader
{
	private String pkgFilter;

	public JarClassLoader(URL[] urls)
	{
		super(urls, JarClassLoader.class.getClassLoader());
		pkgFilter = "";
	}

	public void addFile(String path) throws MalformedURLException
	{
		String urlPath = "jar:file://" + path + "!/";
		addURL(new URL(urlPath));
	}

	public void setFilter(String filter)
	{
		pkgFilter = filter;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		Package[] packages;
		String pkgName;
		String className;
		Class<?> clazz;

		// First up - see if we can find the class in a set of packages.
		packages = super.getPackages();
		for(Package pkg : packages)
		{
			pkgName = pkg.getName();
			if(pkgName.startsWith(pkgFilter))
			{
				try
				{
					className = pkgName;
					if(!className.endsWith("."))
						className += ".";

					className += name;
					clazz = super.loadClass(className);

					// If we get here we've got it ... or at least a class named what we're looking for. :)
					return clazz;
				} catch(ClassNotFoundException cnfe) {
					// Not found in there.  Try next one.
				}
			}
		}
		
		return super.findClass(name);
	}
}
