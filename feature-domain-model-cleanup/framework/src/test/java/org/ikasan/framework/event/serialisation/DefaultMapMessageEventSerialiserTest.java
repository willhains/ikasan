package org.ikasan.framework.event.serialisation;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import junit.framework.Assert;

import org.ikasan.common.Payload;
import org.ikasan.common.component.Spec;
import org.ikasan.common.factory.PayloadFactory;
import org.ikasan.framework.component.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class DefaultMapMessageEventSerialiserTest {

	/**
	 * Class under test
	 */
	private DefaultMapMessageEventSerialiser defaultJmsMessageEventSerialiser;
	
	
	private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
	private PayloadFactory payloadFactory = mockery.mock(PayloadFactory.class);
    
	final Payload payload1 = mockery.mock(Payload.class, "payload1");
	final Payload payload2 = mockery.mock(Payload.class, "payload2");
	
	private List<Payload> payloads = null;
	
	final String payload1Prefix = DefaultMapMessageEventSerialiser.PAYLOAD_PREFIX+0;
	final String payload2Prefix = DefaultMapMessageEventSerialiser.PAYLOAD_PREFIX+1;
	
	//payload content
	final String payload1ContentKey = payload1Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_CONTENT_SUFFIX;
	final String payload2ContentKey = payload2Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_CONTENT_SUFFIX;
	final byte[] payload1Content = "payload1Content".getBytes();
	final byte[] payload2Content = "payload2Content".getBytes();
	
	//payload srcSystem
	final String payload1SrcSystemKey = payload1Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_SRC_SYSTEM_SUFFIX;
	final String payload2SrcSystemKey = payload2Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_SRC_SYSTEM_SUFFIX;
	final String payload1SrcSystem = "payload1SrcSystem";
	final String payload2SrcSystem = "payload2SrcSystem";
	
	//payload name
	final String payload1NameKey = payload1Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_NAME_SUFFIX;
	final String payload2NameKey = payload2Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_NAME_SUFFIX;
	final String payload1Name = "payload1Name";
	final String payload2Name = "payload2Name";	
	
	//payload id
	final String payload1IdKey = payload1Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_ID_SUFFIX;
	final String payload2IdKey = payload2Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_ID_SUFFIX;
	final String payload1Id = "payload1Id";
	final String payload2Id = "payload2Id";	
	
	//payload spec
	final String payload1SpecKey = payload1Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_SPEC_SUFFIX;
	final String payload2SpecKey = payload2Prefix + DefaultMapMessageEventSerialiser.PAYLOAD_SPEC_SUFFIX;
	final Spec payload1Spec = Spec.TEXT_HTML;
	final Spec payload2Spec = Spec.BYTE_ZIP;	
	
	//event id
	final String eventIdKey = DefaultMapMessageEventSerialiser.EVENT_FIELD_ID;
	final String eventId = "eventId";
	
	//event priority
	final String eventPriorityKey = DefaultMapMessageEventSerialiser.EVENT_FIELD_PRIORITY;
	final int priority = 8;

	//event timestamp
	final String eventTimestampKey = DefaultMapMessageEventSerialiser.EVENT_FIELD_TIMESTAMP;
	final long timestamp = 1000l;
	
	//event srcSystem
	final String eventSrcSystemKey = DefaultMapMessageEventSerialiser.EVENT_FIELD_SRC_SYSTEM;
	final String srcSystem = "srcSystem";
	
	
	public DefaultMapMessageEventSerialiserTest(){
		defaultJmsMessageEventSerialiser = new DefaultMapMessageEventSerialiser();
		defaultJmsMessageEventSerialiser.setPayloadFactory(payloadFactory);
		payloads = new ArrayList<Payload>();
		payloads.add(payload1);
		payloads.add(payload2);
		
	}
	
    /**
     * Tests the successful deserialisation
     * 
     * @throws EventSerialisationException
     * @throws EnvelopeOperationException
     * @throws PayloadOperationException
     * @throws JMSException
     */
    @Test
    public void testFromMapMessage() throws EventSerialisationException, JMSException
    {
    	final MapMessage mapMessage = mockery.mock(MapMessage.class);
    	final String moduleName = "moduleName";
    	final String componentName = "componentName";

    	


    	
    	
    	Map<String, Object> map = new HashMap<String, Object>();
    	//event fields
    	map.put(DefaultMapMessageEventSerialiser.EVENT_FIELD_ID, eventId);
    	map.put(DefaultMapMessageEventSerialiser.EVENT_FIELD_PRIORITY, priority);
    	map.put(DefaultMapMessageEventSerialiser.EVENT_FIELD_TIMESTAMP, timestamp);
    	map.put(DefaultMapMessageEventSerialiser.EVENT_FIELD_SRC_SYSTEM, srcSystem);
    	
    	//payload content
    	map.put(payload1ContentKey, payload1Content);
    	map.put(payload2ContentKey, payload2Content);
    	
    	//payload srcSystem
    	map.put(payload1SrcSystemKey, payload1SrcSystem);
    	map.put(payload2SrcSystemKey, payload2SrcSystem);

    	//payload name
    	map.put(payload1NameKey, payload1Name);
    	map.put(payload2NameKey, payload2Name);
    	
    	//payload id
    	map.put(payload1IdKey, payload1Id);
    	map.put(payload2IdKey, payload2Id);
    	
    	//payload spec
    	map.put(payload1SpecKey, payload1Spec.name());
    	map.put(payload2SpecKey, payload2Spec.name());
    	
    	final Enumeration mapNamesEnumeration = new Vector(map.keySet()).elements();

    	
    	
    	final List<String> mappedNames = new ArrayList<String>();

    	
    	mockery.checking(new Expectations()
        {
            {
            	one(mapMessage).getMapNames();will(returnValue(mapNamesEnumeration));
            	
            	//event fields
            	one(mapMessage).getString(DefaultMapMessageEventSerialiser.EVENT_FIELD_ID);will(returnValue(eventId));
            	one(mapMessage).getInt(DefaultMapMessageEventSerialiser.EVENT_FIELD_PRIORITY);will(returnValue(priority));
            	one(mapMessage).getLong(DefaultMapMessageEventSerialiser.EVENT_FIELD_TIMESTAMP);will(returnValue(timestamp));
            	one(mapMessage).getString(DefaultMapMessageEventSerialiser.EVENT_FIELD_SRC_SYSTEM);will(returnValue(srcSystem));
            	
            	//payload content
            	one(mapMessage).getBytes(payload1ContentKey);will(returnValue(payload1Content));
            	one(mapMessage).getBytes(payload2ContentKey);will(returnValue(payload2Content));
            	
            	//payload srcSystem
            	one(mapMessage).getString(payload1SrcSystemKey);will(returnValue(payload1SrcSystem));
            	one(mapMessage).getString(payload2SrcSystemKey);will(returnValue(payload2SrcSystem));

            	//payload name
            	one(mapMessage).getString(payload1NameKey);will(returnValue(payload1Name));
            	one(mapMessage).getString(payload2NameKey);will(returnValue(payload2Name));
            	
            	//payload id
            	one(mapMessage).getString(payload1IdKey);will(returnValue(payload1Id));
            	one(mapMessage).getString(payload2IdKey);will(returnValue(payload2Id));
            	
            	//payload spec
            	one(mapMessage).getString(payload1SpecKey);will(returnValue(payload1Spec.name()));
            	one(mapMessage).getString(payload2SpecKey);will(returnValue(payload2Spec.name()));

 	
                one(payloadFactory).newPayload(payload1Id, payload1Name, payload1Spec, payload1SrcSystem, payload1Content);will(returnValue(payload1));
                one(payloadFactory).newPayload(payload2Id, payload2Name, payload2Spec, payload2SrcSystem, payload2Content);will(returnValue(payload2));

                
            }
        });
    	
    	Event event = defaultJmsMessageEventSerialiser.fromMessage(mapMessage, moduleName, componentName);
    	
    	mockery.assertIsSatisfied();
  	
    	
    	Assert.assertEquals("event should have id, obtained from appropriate field in mapMessage", eventId, event.getId());
    	Assert.assertEquals("event should have priority, obtained from appropriate field in mapMessage", priority, event.getPriority());
    	Assert.assertEquals("event should have timestamp, obtained from appropriate field in mapMessage", timestamp, event.getTimestamp());
    	Assert.assertEquals("event should have srcSystem, obtained from appropriate field in mapMessage", srcSystem, event.getSrcSystem());
    	
    	//check the payloads are present
    	Assert.assertEquals("event should have payloads as produced by payloadFactory", event.getPayloads(), payloads);

    }

	@Test
	public void testToMapMessage() throws JMSException {
		final Event event = mockery.mock(Event.class);

		final Session session = mockery.mock(Session.class);
		final MapMessage mapMessage = mockery.mock(MapMessage.class);

		
        mockery.checking(new Expectations()
        {
            {
            	one(session).createMapMessage();will(returnValue(mapMessage));
            	
                one(event).getPayloads();will(returnValue(payloads));
                
                //payload content
                one(payload1).getContent();will(returnValue(payload1Content));
                one(mapMessage).setBytes(payload1ContentKey, payload1Content);

                one(payload2).getContent();will(returnValue(payload2Content));
                one(mapMessage).setBytes(payload2ContentKey, payload2Content);
                

                
                //payload srcSystem
                one(payload1).getSrcSystem();will(returnValue(payload1SrcSystem));
                one(mapMessage).setString(payload1SrcSystemKey, payload1SrcSystem);

                one(payload2).getSrcSystem();will(returnValue(payload2SrcSystem));
                one(mapMessage).setString(payload2SrcSystemKey, payload2SrcSystem);
                
                //payload name
                one(payload1).getName();will(returnValue(payload1Name));
                one(mapMessage).setString(payload1NameKey, payload1Name);

                one(payload2).getName();will(returnValue(payload2Name));
                one(mapMessage).setString(payload2NameKey, payload2Name);
                
                //payload id
                one(payload1).getId();will(returnValue(payload1Id));
                one(mapMessage).setString(payload1IdKey, payload1Id);

                one(payload2).getId();will(returnValue(payload2Id));
                one(mapMessage).setString(payload2IdKey, payload2Id);
                
                //payload spec
                one(payload1).getSpec();will(returnValue(payload1Spec));
                one(mapMessage).setString(payload1SpecKey, payload1Spec.name());

                one(payload2).getSpec();will(returnValue(payload2Spec));
                one(mapMessage).setString(payload2SpecKey, payload2Spec.name());
                
                
                //event Id
                one(event).getId();will(returnValue(eventId));
                one(mapMessage).setString(eventIdKey, eventId);
                
                //event priority
                one(event).getPriority();will(returnValue(priority));
                one(mapMessage).setInt(eventPriorityKey, priority);
                
                //event timestamp
                one(event).getTimestamp();will(returnValue(timestamp));
                one(mapMessage).setLong(eventTimestampKey, timestamp);
                
                //event srcSystem
                one(event).getSrcSystem();will(returnValue(srcSystem));
                one(mapMessage).setString(eventSrcSystemKey, srcSystem);

            }
        });
        Assert.assertEquals("produced MapMessage should be that obtained from session",mapMessage, defaultJmsMessageEventSerialiser.toMessage(event, session));
        
        mockery.assertIsSatisfied();
	}

}
