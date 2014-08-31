package com.denarced.jsontojava.plugin;

import com.denarced.jsontojava.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;

/**
 * Convert json file into Java source files.
 * 
 * @goal generate-sources
 * @phase generate-sources
 */
public class JsonToJavaMojo extends AbstractMojo {
    /**
     * @parameter default-value="com.example"
     */
    private String targetPackage;
    
    /**
     * @parameter default-value="target/gen"
     */
    private String output;

    /**
     * @parameter
     * @required
     */
    private File jsonFile;

    public void execute() throws MojoExecutionException {
        if (jsonFile != null) {
            InputStream json = null;
            try {
                json = new FileInputStream(jsonFile);
            } catch (FileNotFoundException e) {
                throw new MojoExecutionException("Creating stream from jsonFile threw.", e);
            }
            String rootClassName = "Root";

            String targetDirectory = output;
            PrintStream output = null;
            try {
                output = JavaFileOutputFactory.create(
                    rootClassName,
                    targetDirectory);
            } catch (IOException e) {
                throw new MojoExecutionException("IOException ...", e);
            }

            Parser parser = new JsonFileParser(json);
            JsonObject jsonObject = new JsonObject(rootClassName);
            parser.parse(jsonObject);
            JavaClassWriter javaClassWriter = new JavaClassWriterImpl(output);
            javaClassWriter.setTargetPackage(targetPackage);
            jsonObject.printInto(javaClassWriter);
        }
    }
}
