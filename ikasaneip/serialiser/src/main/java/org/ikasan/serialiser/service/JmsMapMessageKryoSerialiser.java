/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.serialiser.service;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.ikasan.serialiser.model.JmsMapMessageDefaultImpl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class JmsMapMessageKryoSerialiser extends Serializer<MapMessage>
{
    public void write(Kryo kryo, Output output, MapMessage message)
    {
        try
        {
        	JmsMapMessageDefaultImpl mapMessage = this.convert(message);
            kryo.writeClassAndObject(output, mapMessage);
        }
        catch (JMSException e)
        {
            throw new RuntimeException(e);
        }
    }

    public MapMessage read(Kryo kryo, Input input, Class<MapMessage> message)
    {
    	return (MapMessage)kryo.readClassAndObject(input);
    }
    
    private JmsMapMessageDefaultImpl convert(MapMessage message) throws JMSException
    {
    	JmsMapMessageDefaultImpl jmsMapMessageDefault = new JmsMapMessageDefaultImpl();
    	
    	jmsMapMessageDefault.setJMSCorrelationID(message.getJMSCorrelationID());
    	jmsMapMessageDefault.setJMSCorrelationIDAsBytes(message.getJMSCorrelationIDAsBytes());
    	jmsMapMessageDefault.setJMSDeliveryMode(message.getJMSDeliveryMode());
    	jmsMapMessageDefault.setJMSDestination(message.getJMSDestination());
    	jmsMapMessageDefault.setJMSExpiration(jmsMapMessageDefault.getJMSExpiration());
    	jmsMapMessageDefault.setJMSMessageID(message.getJMSMessageID());
    	jmsMapMessageDefault.setJMSPriority(message.getJMSPriority());
    	jmsMapMessageDefault.setJMSRedelivered(message.getJMSRedelivered());
    	jmsMapMessageDefault.setJMSReplyTo(message.getJMSReplyTo());
    	jmsMapMessageDefault.setJMSTimestamp(message.getJMSTimestamp());
    	jmsMapMessageDefault.setJMSType(jmsMapMessageDefault.getJMSType());
    	    	
    	Enumeration<String> names  = message.getPropertyNames();
    	
    	while(names.hasMoreElements())
    	{
    		String name = names.nextElement();

    		jmsMapMessageDefault.setObjectProperty(name, message.getObjectProperty(name));
    	}
    	
    	names  = message.getMapNames();
    	
    	while(names.hasMoreElements())
    	{
    		String name = names.nextElement();

    		jmsMapMessageDefault.setObject(name, message.getObjectProperty(name));
    	}
    	
    	return jmsMapMessageDefault;
    }
}