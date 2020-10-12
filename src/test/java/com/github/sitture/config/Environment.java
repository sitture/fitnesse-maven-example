package com.github.sitture.config;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import fit.exception.FitFailureException;
import fitnesse.ContextConfigurator;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;

/**
 * Holds overall environment settings. Expected to be set up before actual tests
 * are performed.
 */
public class Environment {

    private final static Environment INSTANCE = new Environment();
    private String fitNesseRoot = ContextConfigurator.DEFAULT_ROOT;
    private Configuration freemarkerConfig;
    private FreeMarkerHelper fmHelper;
    private ConcurrentHashMap<String, Template> templateCache;
    private ConcurrentHashMap<String, String> symbols;

    private Environment() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        // Specify the data source where the template files come from.
        cfg.setClassForTemplateLoading(getClass(), "/templates/");
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_23);
        builder.setExposeFields(true);
        cfg.setObjectWrapper(builder.build());
        freemarkerConfig = cfg;
        
        fmHelper = new FreeMarkerHelper();
        templateCache = new ConcurrentHashMap<String, Template>();

        symbols = new ConcurrentHashMap<String, String>();
    }

    /**
     * @return singleton instance.
     */
    public static Environment getInstance() {
        return INSTANCE;
    }

    /**
     * @return new instance of class.
     * @throws RuntimeException if no instance could be created.
     */
    public <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create instance of: " + clazz.getName(), e);
        }
    }

    /**
     * Stores key/value to be used.
     * @param key
     * @param value
     */
    public void setSymbol(String key, String value) {
        if (value == null) {
            symbols.remove(key);
        } else {
            symbols.put(key, value);
        }
    }

    /**
     * Retrieves value previously stored.
     * @param key
     * @return value stored for key.
     */
    public String getSymbol(String key) {
        return symbols.get(key);
    }


    /**
     * Gets symbol value, or throws exception if no symbol by that key exists.
     * @param key symbol's key.
     * @return symbol's value.
     */
    public String getRequiredSymbol(String key) {
        String result = null;
        Object symbol = getSymbol(key);
        if (symbol == null) {
            throw new FitFailureException("No Symbol defined with key: " + key);
        } else {
            result = symbol.toString();
        }
        return result;
    }

    /**
     * @return FreeMarker configuration to use.
     */
    public Configuration getConfiguration() {
        return freemarkerConfig;
    }

    /**
     * @param name name of template to get
     * @return template by that name
     */
    public Template getTemplate(String name) {
        Template result;
        if (!templateCache.containsKey(name)) {
            Template t = fmHelper.getTemplate(getConfiguration(), name);
            result = templateCache.putIfAbsent(name, t);
            if (result == null) {
                result = t;
            }
        } else {
            result = templateCache.get(name);
        }
        return result;
    }

    /**
     * @param templateName name of template to apply
     * @param model model to supply to template
     * @return result of template
     */
    public String processTemplate(String templateName, Object model) {
        Template t = getTemplate(templateName);
        return fmHelper.processTemplate(t, model);
    }

    /**
     * @return directory containing FitNesse's root.
     */
    public String getFitNesseRootDir() {
        return fitNesseRoot;
    }

    /**
     * @return directory containing FitNesse's files section.
     */
    public String getFitNesseFilesSectionDir() {
        return new File(fitNesseRoot, "files").getAbsolutePath();
    }

    /**
     * @param fitNesseRoot directory containing FitNesse's root.
     */
    public void setFitNesseRoot(String fitNesseRoot) {
        File root = new File(fitNesseRoot);
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException("value for fitNesseRoot must be an existing directory");
        }
        this.fitNesseRoot = fitNesseRoot;
    }

}
