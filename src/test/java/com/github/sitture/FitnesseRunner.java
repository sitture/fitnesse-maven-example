package com.github.sitture;

import java.io.File;

import fitnesse.components.PluginsClassLoaderFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.runners.model.InitializationError;

import com.github.sitture.config.Environment;

import fitnesse.ContextConfigurator;
import fitnesse.FitNesseContext;
import fitnesse.junit.FitNesseRunner;

/**
 * JUnit Runner to run a FitNesse suite or page as JUnit test.
 *
 * The suite/page to run must be specified either via the Java property
 * 'fitnesseSuiteToRun', or by adding a FitNesseSuite.Name annotation to the test class.
 * If both are present the system property is used.
 *
 * The HTML generated for each page is saved in target/fitnesse-results
 */
public class FitnesseRunner extends FitNesseRunner {
    private final static String suiteOverrideVariableName = "fitnesseSuiteToRun";

    public FitnesseRunner(Class<?> suiteClass) throws InitializationError {
        super(suiteClass);
        try {
            // we include images in output so build server will have single
            // directory containing both HTML results and the images created by the tests
            String outputDir = getOutputDir(suiteClass);
            new File(outputDir).mkdirs();
            Environment.getInstance().setFitNesseRoot(outputDir);
        } catch (Exception e) {
            throw new InitializationError(e);
        }
    }

    @Override
    protected String getSuiteName(Class<?> klass) throws InitializationError {
        String name = System.getProperty(suiteOverrideVariableName);
        if (StringUtils.isEmpty(name)) {
            Suite nameAnnotation = klass.getAnnotation(Suite.class);
            if (nameAnnotation == null) {
                throw new InitializationError("There must be a @Suite annotation");
            }
            name = nameAnnotation.value();
        }
        return name;
    }

    @Override
    protected String getFitNesseDir(Class<?> suiteClass) {
		return "FitNesse";
    }

    @Override
    protected String getOutputDir(Class<?> klass) {
        return "target/fitnesse-results";
    }

    @Override
    protected String getFitNesseRoot(Class<?> suiteClass) {
        return ContextConfigurator.DEFAULT_ROOT;
    }

    @Override
    protected FitNesseContext createContext(Class<?> suiteClass) throws Exception {
        // disable maven-classpath-plugin, we expect all jars to be loaded as part of this jUnit run
        System.setProperty("fitnesse.wikitext.widgets.MavenClasspathSymbolType.Disable", "true");
        ClassLoader cl = PluginsClassLoaderFactory.getClassLoader(getFitNesseDir(suiteClass));
        ContextConfigurator configurator = initContextConfigurator().withClassLoader(cl);
        return configurator.makeFitNesseContext();
    }
}
