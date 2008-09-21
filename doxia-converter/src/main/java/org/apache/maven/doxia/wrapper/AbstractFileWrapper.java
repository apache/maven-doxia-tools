package org.apache.maven.doxia.wrapper;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import com.ibm.icu.text.CharsetDetector;

/**
 * Abstract File wrapper for Doxia converter.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
abstract class AbstractFileWrapper
    extends AbstractWrapper
{
    public static final String AUTO_ENCODING = "auto";

    /** serialVersionUID */
    static final long serialVersionUID = 6072685230076158590L;

    private File file;

    private String encoding;

    /**
     *
     * @param absolutePath not null
     * @param format could be null
     * @param encoding could be null
     * @param supportedFormat not null
     * @throws UnsupportedEncodingException if the encoding is unsupported.
     * @throws IllegalArgumentException if any
     */
    AbstractFileWrapper( String absolutePath, String format, String encoding, String[] supportedFormat )
        throws UnsupportedEncodingException
    {
        super( format, supportedFormat );

        if ( StringUtils.isEmpty( absolutePath ) )
        {
            throw new IllegalArgumentException( "absolutePath is required" );
        }

        File file = new File( absolutePath );
        if ( !file.isAbsolute() )
        {
            file = new File( new File( "" ).getAbsolutePath(), absolutePath );
        }
        this.file = file;

        if ( StringUtils.isNotEmpty( encoding ) && !encoding.equalsIgnoreCase( encoding )
            && !validateEncoding( encoding ) )
        {
            StringBuffer msg = new StringBuffer();
            msg.append( "The encoding '" + encoding + "' is not a valid one. The supported charsets are: " );
            msg.append( StringUtils.join( CharsetDetector.getAllDetectableCharsets(), ", " ) );
            throw new UnsupportedEncodingException( msg.toString() );
        }
        this.encoding = ( StringUtils.isNotEmpty( encoding ) ? encoding : AUTO_ENCODING );
    }

    /**
     * @return the file
     */
    public File getFile()
    {
        return file;
    }

    /**
     * @param file new file.
     */
    void setFile( File file )
    {
        this.file = file;
    }

    /**
     * @return the encoding used for the file or <code>null</code> if not specified.
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * @param encoding new encoding.
     */
    void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    /**
     * Validate if a charset is supported on this platform.
     *
     * @param charsetName the charsetName to be checked.
     * @return <code>true</code> if the charset is supported by the JVM, <code>false</code> otherwise.
     */
    static boolean validateEncoding( String charsetName )
    {
        if ( StringUtils.isEmpty( charsetName ) )
        {
            return false;
        }

        OutputStream ost = new ByteArrayOutputStream();
        OutputStreamWriter osw = null;
        try
        {
            osw = new OutputStreamWriter( ost, charsetName );
        }
        catch ( UnsupportedEncodingException exc )
        {
            return false;
        }
        finally
        {
            IOUtil.close( osw );
        }
        return true;
    }

    /** {@inheritDoc} */
    public boolean equals( Object other )
    {
        if ( this == other )
        {
            return true;
        }

        if ( !( other instanceof AbstractFileWrapper ) )
        {
            return false;
        }

        AbstractFileWrapper that = (AbstractFileWrapper) other;
        boolean result = true;
        result = result && super.equals( other );
        result = result && ( getFile() == null ? that.getFile() == null : getFile().equals( that.getFile() ) );
        return result;
    }

    /** {@inheritDoc} */
    public int hashCode()
    {
        int result = super.hashCode();
        result = 37 * result + ( getFile() != null ? getFile().hashCode() : 0 );
        return result;
    }

    /** {@inheritDoc} */
    public java.lang.String toString()
    {
        StringBuffer buf = new StringBuffer( super.toString() + "\n" );
        buf.append( "file= '" );
        buf.append( getFile() + "'" );
        return buf.toString();
    }
}
