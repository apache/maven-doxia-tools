package org.apache.maven.doxia.linkcheck.validation;

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

/**
 * @author <a href="mailto:bwalding@apache.org">Ben Walding</a>
 * @author <a href="mailto:aheritier@apache.org">Arnaud Heritier</a>
 * @version $Id$
 */
public interface LinkValidator
{
    /**
     * If getResourceKey(lvi) returned null, this will NOT be called.
     *
     * @param lvi The LinkValidationItem to validate.
     * @return The LinkValidationResult.
     */
    LinkValidationResult validateLink( LinkValidationItem lvi );

    /**
     * The resource key is used by the cache to determine if it really needs to validate the link. No actual validation
     * should be done at this point.
     *
     * @param lvi The LinkValidationItem to validate.
     * @return Object null if this validator should not be doing this work.
     */
    Object getResourceKey( LinkValidationItem lvi );
}
