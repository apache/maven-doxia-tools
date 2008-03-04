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

import org.apache.maven.doxia.convertor.model.InputFile;
import org.apache.maven.doxia.convertor.model.OutputFile;
import org.apache.maven.doxia.logging.LogEnabled;

/**
 * Interface to convert a Doxia file to another one.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public interface Converter
    extends LogEnabled
{
    /**
     * @return a not null array containing supported input format, i.e. <code>apt</code>.
     */
    String[] getInputFormats();

    /**
     * @return a not null array containing supported input format, i.e. <code>xhtml</code>.
     */
    String[] getOutputFormats();

    /**
     * @param input an input file, not null.
     * @param output an output file, not null.
     * @throws UnsupportedFormatException if any
     * @throws ConverterException if any
     */
    void convert( InputFile input, OutputFile output )
        throws UnsupportedFormatException, ConverterException;
}
