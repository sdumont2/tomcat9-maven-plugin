package org.apache.tomcat.maven.common.messages;

/*
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
 */

import org.codehaus.plexus.component.annotations.Component;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Olivier Lamy
 * @since 2.0
 */
@Component( role = MessagesProvider.class )
public class DefaultMessagesProvider
    implements MessagesProvider
{

    /**
     * plugin messages
     */
    private ResourceBundle messages;


    public DefaultMessagesProvider()
    {
        String packageName = getClass().getPackage().getName();

        messages = ResourceBundle.getBundle( packageName + ".messages" );
    }

    public ResourceBundle getResourceBundle()
    {
        return this.messages;
    }

    /**
     * Gets the message for the given key from this packages resource bundle.
     *
     * @param key the key for the required message
     * @return the message
     */
    public String getMessage( String key )
    {
        try
        {
            return getResourceBundle().getString( key );
        }
        catch ( NullPointerException exception )
        {
            return "???" + key + "???";
        }
        catch ( MissingResourceException exception )
        {
            return "???" + key + "???";
        }
        catch ( ClassCastException exception )
        {
            return "???" + key + "???";
        }
    }

    /**
     * Gets the message for the given key from this packages resource bundle and formats it with the given parameter.
     *
     * @param key   the key for the required message
     * @param params the parameters to be used to format the message with
     * @return the formatted message
     */
    public String getMessage( String key, Object... params )
    {
        return MessageFormat.format( getMessage( key ), params );
    }

}
