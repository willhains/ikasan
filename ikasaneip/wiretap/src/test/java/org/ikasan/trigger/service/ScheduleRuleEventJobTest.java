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
package org.ikasan.trigger.service;

import org.ikasan.component.endpoint.rulecheck.Rule;
import org.ikasan.component.endpoint.rulecheck.RuleBreachException;
import org.ikasan.component.endpoint.rulecheck.service.RuleService;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.trigger.dao.TriggerDao;
import org.ikasan.trigger.model.TriggerRelationship;
import org.ikasan.wiretap.listener.JobAwareFlowEventListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.quartz.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ScheduleRuleEventJob test
 */
public class ScheduleRuleEventJobTest
{
    private ScheduleRuleEventJob uut;

    /**
     * The context that the tests run in, allows for mocking actual concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };


    /** Mock scheduler */
    final Scheduler scheduler = mockery.mock(Scheduler.class, "mockScheduler");

    final RuleService ruleService = mockery.mock(RuleService.class,"mockRuleService");

    @Before
    public void setup(){
        uut = new ScheduleRuleEventJob(ruleService);
    }

    @Test
    public void execute_when_ruleService_returns_rule(){

        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String location="after test-flow-element-name";
        final FlowEvent event = mockery.mock(FlowEvent.class);

        final Map<String,String> params = new HashMap<>();
        params.put("param1","value1");

        final Rule rule = mockery.mock(Rule.class,"mockRule");

        mockery.checking(new Expectations()
        {
            {

                exactly(1).of(ruleService).getRule("after test-flow-element-name|test-flow-name");
                will(returnValue(rule));

                exactly(1).of(rule).update(event);

            }
        });

        // do test
        uut.execute(location, moduleName, flowName, event, params);

        //assert
        mockery.assertIsSatisfied();

    }

    @Test
    public void execute_when_ruleService_returns_null(){

        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String location="after test-flow-element-name";
        final FlowEvent event = mockery.mock(FlowEvent.class);

        final Map<String,String> params = new HashMap<>();
        params.put("param1","value1");

        mockery.checking(new Expectations()
        {
            {

                exactly(1).of(ruleService).getRule("after test-flow-element-name|test-flow-name");
                will(returnValue(null));

            }
        });

        // do test
        uut.execute(location, moduleName, flowName, event, params);

        //assert
        mockery.assertIsSatisfied();

    }

    @Test
    public void execute_by_scheduler_when_ruleService_returns_null() throws JobExecutionException
    {

        final String ruleName="after test-flow-element-name|test-flow-name";
        final String moduleName="testModule";

        final JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class,"jobExecutionContextMock");
        final JobDetail jobDetail = mockery.mock(JobDetail.class,"jobDetailMock");
        final JobKey jobKey = new JobKey(ruleName,moduleName);

        mockery.checking(new Expectations()
        {
            {

                exactly(1).of(jobExecutionContext).getJobDetail();
                will(returnValue(jobDetail));

                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));

                exactly(1).of(ruleService).getRule(ruleName);
                will(returnValue(null));

            }
        });

        // do test
        uut.execute(jobExecutionContext);

        //assert
        mockery.assertIsSatisfied();

    }

    @Test
    public void execute_by_scheduler_when_ruleService_returns_rule() throws JobExecutionException, RuleBreachException
    {

        final String ruleName="after test-flow-element-name|test-flow-name";
        final String moduleName="testModule";

        final JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class,"jobExecutionContextMock");
        final JobDetail jobDetail = mockery.mock(JobDetail.class,"jobDetailMock");
        final JobKey jobKey = new JobKey(ruleName,moduleName);

        final Rule rule = mockery.mock(Rule.class,"mockRule");


        mockery.checking(new Expectations()
        {
            {

                exactly(1).of(jobExecutionContext).getJobDetail();
                will(returnValue(jobDetail));

                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));

                exactly(1).of(ruleService).getRule(ruleName);
                will(returnValue(rule));

                exactly(1).of(rule).check(jobExecutionContext);

            }
        });

        // do test
        uut.execute(jobExecutionContext);

        //assert
        mockery.assertIsSatisfied();

    }



}