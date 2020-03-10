package org.apache.tomcat.maven.common.run;

/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Modifications Copyright (C) 2020 Sean Dumont
 */

import org.apache.catalina.loader.WebappLoader;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@linkplain WebappLoader} implementation that allows scanning for changes to project classpath in support of
 * context reloads.
 *
 * @author Ryan Connolly
 * @since 2.0
 */
public class ExternalRepositoriesReloadableWebappLoader
    extends WebappLoader
{

    /**
     * Last modification times of all jar and class files.
     */
    private Map<String, Long> modificationTimeMap = new HashMap<String, Long>();

    private Log log;

    /**
     * Default Constructor.
     */
    public ExternalRepositoriesReloadableWebappLoader()
    {
        super();
    }


    /**
     * Convenience Constructor allows setting of a parent ClassLoader.
     *
     * @param parent the ClassLoader instance to set as this Loader's parent ClassLoader.
     */
    public ExternalRepositoriesReloadableWebappLoader( ClassLoader parent, Log log )
    {
        super( parent );
        this.log = log;
    }


    /* This method doesn't exist in tomcat 8+
    temporarily commenting out until I find out if this class is even needed anymore.
    I don't think it is. I'm fairly certain that functionality has moved and been taken care of by web resource loading.
    @Override
    public void addRepository( String repository )
    {
        super.addRepository( repository );
        try
        {
            File file = new File( new URL( repository ).getPath().replaceAll( "%20", " " ) );
            if ( file.isDirectory() )
            {
                addClassDirectory( file );
            }
            else if ( file.isFile() && file.getName().endsWith( ".jar" ) )
            {
                addFile( file );
            }
        }
        catch ( MalformedURLException muex )
        {
            throw new RuntimeException( muex );
        }
    }*/

    /**
     * Tracks modification times of files in the given class directory.
     *
     * @param directory the File directory to track modification times for.
     */
    private void addClassDirectory( File directory )
    {
        for ( File file : directory.listFiles() )
        {
            if ( file.isDirectory() )
            {
                //remember also directory last modification time
                addFile( file );
                addClassDirectory( file );
            }
            else if ( file.isFile() )
            {
                addFile( file );
            }
        }
    }

    /**
     * Tracks last modification time of the given File.
     *
     * @param file the File for which to track last modification time.
     */
    private void addFile( File file )
    {
        modificationTimeMap.put( file.getAbsolutePath(), file.lastModified() );
    }

    /**
     * Check if {@link WebappLoader} says modified(), if not then check files from added repositories.
     */
    @Override
    public boolean modified()
    {
        boolean modified = super.modified();
        if ( !modified )
        {
            if ( log != null )
            {
                log.debug( "classPath scanning started at " + new Date().toString() );
            }
            for ( Map.Entry<String, Long> entry : modificationTimeMap.entrySet() )
            {
                String key = entry.getKey();
                File file = new File( key );
                if ( file.exists() )
                {
                    // file could be deleted.
                    Long savedLastModified = modificationTimeMap.get( key );
                    if ( file.lastModified() > savedLastModified )
                    {
                        modified = true;
                        modificationTimeMap.put( key, file.lastModified() );

                        // directory last modification time can change when some class,
                        // jar or subdirectory was added or deleted.
                        if ( file.isDirectory() )
                        {
                            addClassDirectory( file );
                        }
                    }
                }
            }
        }
        if ( log != null )
        {
            log.debug( "context " + modified + " at " + new Date().toString() );
        }
        return modified;
    }

}
