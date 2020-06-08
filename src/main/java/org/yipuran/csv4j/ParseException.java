/*
 * Copyright 2007 Shawn Boyce.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yipuran.csv4j;

/**
 * This exception indicates a CSV Parser error occurred.
 * @author Shawn Boyce
 * @since 1.0
 */
public class ParseException extends RuntimeException
{
	private Integer columno;
    /**
     * Constructor.
     * @param message exception message
     */
    public ParseException( String message )
    {
        super( message );
    }
    public ParseException( String message, int columno )
    {
        super( message );
        this.columno = columno;
    }
    public Integer getColumNo() {
   	 return columno;
    }


    /**
     * Constructor.
     * @param message exception message
     * @param cause linked exception
     */
    public ParseException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
