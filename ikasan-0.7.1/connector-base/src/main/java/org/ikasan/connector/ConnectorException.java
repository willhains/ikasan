/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.connector;

import org.ikasan.common.CommonException;
import org.ikasan.common.CommonExceptionType;
import org.ikasan.common.ExceptionType;

/**
 * ConnectorException exception extends the standard ResourceException
 * and includes an ExceptionType to allow categorisation of the thrown
 * exception at the point it is thrown.
 * 
 * If no ExceptionType is specified a default of UNDEFINED will be used.
 * 
 * @author Ikasan Development Team
 */
public class ConnectorException
    extends CommonException
{
    /** Serial ID */
    private static final long serialVersionUID = 1L;

    /** Allow type definition to categorise the exception */
    protected ExceptionType exceptionType;

    /**
     * Constructs a new connector exception with <code>null</code> 
     * as its detail message and an UNDEFINED ExceptionType.
     */
    public ConnectorException()
    {
        this(CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new connector exception with <code>null</code> 
     * as its detail message and a specified exception type.
     *
     * @param exceptionType the exception type
     */
    public ConnectorException(final ExceptionType exceptionType) 
    {
        super(exceptionType);
    }

    /**
     * Constructs a new connector exception with the specified 
     * detail message and an UNDEFINED ExceptionType.
     *
     * @param   message   the detail message.
     */
    public ConnectorException(final String message)
    {
        this(message, CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new connector exception with the specified 
     * detail message and given ExceptionType.
     *
     * @param   message   the detail message.
     * @param exceptionType the exception type
     */
    public ConnectorException(final String message, final ExceptionType exceptionType)
    {
        super(message, exceptionType);
    }

    /**
     * Constructs a new connector exception with the specified detail 
     * message and cause and an UNDEFINED ExceptionType.
     * 
     * @param  message the detail message.
     * @param  cause the cause.
     */
    public ConnectorException(final String message, final Throwable cause)
    {
        this(message, cause, CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new connector exception with the specified detail message and
     * cause and the specified exception type.
     * 
     * @param  message the detail message.
     * @param  cause the cause.
     * @param  exceptionType the exception type
     */
    public ConnectorException(final String message, final Throwable cause, 
            final ExceptionType exceptionType)
    {
        super(message, cause, exceptionType);
    }

    /**
     * Constructs a new connector exception with the specified cause
     * and an UNDEFINED ExceptionType.
     *
     * @param  cause the cause
     */
    public ConnectorException(final Throwable cause)
    {
        this(cause, CommonExceptionType.UNDEFINED);
    }

    /**
     * Constructs a new connector exception with the specified cause
     * and the specified exception type.
     *
     * @param  cause the cause
     * @param exceptionType the exception type
     */
    public ConnectorException(final Throwable cause, final ExceptionType exceptionType)
    {
        super(cause, exceptionType);
    }

//    /**
//     * Setter for exceptionType
//     * @param exceptionType type of exception
//     */
//    public void setExceptionType(final ExceptionType exceptionType)
//    {
//        this.exceptionType = exceptionType;
//    }
//
//    /**
//     * Getter for exceptionType
//     * 
//     * @return ExceptionType
//     */
//    public ExceptionType getExceptionType()
//    {
//        return this.exceptionType;
//    }
}