/*
 * $Id: SystemEventHousekeeperTest.java 31380 2014-01-24 19:52:23Z jeffmitchell $
 * $URL: http://svc-vcs:18080/svn/MSUSA/middleware/trunk/mceb-platform-maintenance/jar/src/test/java/com/mizuho/middleware/platform/maintenance/component/endpoint/SystemEventHousekeeperTest.java $
 *
 * ====================================================================
 * (C) Copyright Mizuho Securities USA
 * ====================================================================
 *
 */
package org.ikasan.component.converter.xml;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.ikasan.marshaller.Marshaller;
import org.ikasan.spec.component.transformation.Converter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import javax.resource.ResourceException;

/**
 * Functional unit test cases for
 * <code>JsonXmlConverter</code>.
 * 
 * @author Ikasan Development Team
 */
public class JsonXmlConverterTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** mocked marshaller */
    final Marshaller marshaller = mockery.mock(Marshaller.class, "mockedMarshaller");

    /**
     * Test successful invocation the converter for marshalling JSON to XML
     */
    @Test
    public void test_successful_json_unmarshall() throws ResourceException
    {
        final JSONObject jsonObject = new JSONObject();

        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(marshaller).unmarshall(jsonObject);
                will(returnValue("result"));
            }
        });

        Converter<JSON, String> converter = new JsonXmlConverter( marshaller );
        converter.convert(jsonObject);
        mockery.assertIsSatisfied();
    }

}