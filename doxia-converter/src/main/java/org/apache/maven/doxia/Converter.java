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
package org.apache.maven.doxia;

import org.apache.maven.doxia.logging.LogEnabled;
import org.apache.maven.doxia.wrapper.InputFileWrapper;
import org.apache.maven.doxia.wrapper.InputReaderWrapper;
import org.apache.maven.doxia.wrapper.OutputFileWrapper;
import org.apache.maven.doxia.wrapper.OutputWriterWrapper;

/**
 * Interface to convert a Doxia input wrapper to a Doxia output wrapper.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public interface Converter
    extends LogEnabled
{
    /**
     * @return a not null array containing supported input formats, i.e. <code>apt</code>.
     */
    String[] getInputFormats();

    /**
     * @return a not null array containing supported output formats, i.e. <code>xhtml</code>.
     */
    String[] getOutputFormats();

    /**
     * @param input an input file wrapper, not null.
     * @param output an output file wrapper, not null.
     * @throws UnsupportedFormatException if any
     * @throws ConverterException if any
     */
    void convert( InputFileWrapper input, OutputFileWrapper output )
        throws UnsupportedFormatException, ConverterException;

    /**
     * @param input an input reader wrapper, not null.
     * @param output an output writer wrapper, not null.
     * @throws UnsupportedFormatException if any
     * @throws ConverterException if any
     */
    void convert( InputReaderWrapper input, OutputWriterWrapper output )
        throws UnsupportedFormatException, ConverterException;

    /**
     * Make the generated files human readable.
     * <br/>
     * <b>Note</b>: actually, only XML based outputs could be formatted.
     *
     * @param formatOutput <code>true</code> to format the generated files, <code>false</code> otherwise.
     */
    void setFormatOutput( boolean formatOutput );
}
