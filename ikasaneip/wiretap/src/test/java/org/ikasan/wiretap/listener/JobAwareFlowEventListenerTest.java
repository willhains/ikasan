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
package org.ikasan.wiretap.listener;

import org.ikasan.component.endpoint.rulecheck.Rule;
import org.ikasan.component.endpoint.rulecheck.service.RuleService;
import org.ikasan.scheduler.ScheduledJobFactory;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.flow.FlowElement;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.wiretap.WiretapService;
import org.ikasan.trigger.dao.TriggerDao;
import org.ikasan.trigger.model.*;
import org.ikasan.trigger.service.FlowEventJob;
import org.ikasan.trigger.service.ScheduleRuleEventJob;
import org.ikasan.trigger.service.WiretapEventJob;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.quartz.*;
import org.quartz.Trigger;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JobAwareFlowEventListener test
 */
public class JobAwareFlowEventListenerTest
{
    private JobAwareFlowEventListener uut;

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

    /** Mock trigger */
    final Trigger trigger = mockery.mock(Trigger.class, "mockTrigger");

    /** Mock jobExecutionContext **/
    final JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class);

    final TriggerDao triggerDao = mockery.mock(TriggerDao.class,"mockTriggerDao");
    final WiretapService wiretapService = mockery.mock(WiretapService.class,"mockWiretapService");
    final RuleService ruleService = mockery.mock(RuleService.class,"mockRuleService");
    final ErrorReportingService errorReportingService = mockery.mock(ErrorReportingService.class,"mockErrorReportingService");
    final ModuleService moduleService = mockery.mock(ModuleService.class,"mockModuleService");
    final ScheduledJobFactory scheduledJobFactory = mockery.mock(ScheduledJobFactory.class,"mockScheduledJobFactory");

    @Before
    public void setup(){
        Map<String, FlowEventJob> flowEventJobs = new HashMap<>();
        flowEventJobs.put("scheduleRuleEventJob",new ScheduleRuleEventJob(ruleService,errorReportingService));
        flowEventJobs.put("wiretapEventJob",new WiretapEventJob(wiretapService));
        uut= new JobAwareFlowEventListener(flowEventJobs,triggerDao,scheduledJobFactory,scheduler,ruleService,
                moduleService);
    }

    @Test
    public void loadTriggers_when_triggers_are_empty(){

        final Module module = mockery.mock(Module.class,"mockModule");
        final String moduleName="test-module";
        final List<Module> modules = new ArrayList<>();
        modules.add(module);
        final List<org.ikasan.trigger.model.Trigger> triggers = new ArrayList<>();


        mockery.checking(new Expectations()
        {
            {
                oneOf(moduleService).getModules();
                will(returnValue(modules));

                oneOf(module).getName();
                will(returnValue(moduleName));

                oneOf(triggerDao).findTriggers(moduleName);
                will(returnValue(triggers));
            }
        });

        // do test
        uut.loadTriggers();

        //assert
        mockery.assertIsSatisfied();

    }

    @Test
    public void loadTriggers_when_triggers__have_one(){

        final Module module = mockery.mock(Module.class,"mockModule");
        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String flowElementName="test-flow-element-name";
        final List<Module> modules = new ArrayList<>();
        modules.add(module);
        final List<org.ikasan.trigger.model.Trigger> triggers = new ArrayList<>();
        final org.ikasan.trigger.model.Trigger trigger = mockery.mock(org.ikasan.trigger.model.Trigger.class);
        triggers.add(trigger);

        mockery.checking(new Expectations()
        {
            {
                oneOf(moduleService).getModules();
                will(returnValue(modules));

                oneOf(module).getName();
                will(returnValue(moduleName));

                oneOf(triggerDao).findTriggers(moduleName);
                will(returnValue(triggers));

                oneOf(trigger).getModuleName();
                will(returnValue(moduleName));

                oneOf(trigger).getFlowName();
                will(returnValue(flowName));

                oneOf(trigger).getFlowElementName();
                will(returnValue(flowElementName));

                oneOf(trigger).getRelationship();
                will(returnValue(TriggerRelationship.get("before")));

                oneOf(trigger).appliesToFlowElement();
                will(returnValue(true));

                oneOf(trigger).getJobName();
                will(returnValue("jobName"));
            }
        });

        // do test
        uut.loadTriggers();

        //assert
        mockery.assertIsSatisfied();

    }

    @Test
    public void loadTriggers_when_triggers_have_one_scheduleRuleEventJob() throws SchedulerException
    {

        final Module module = mockery.mock(Module.class,"mockModule");
        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String flowElementName="test-flow-element-name";
        final List<Module> modules = new ArrayList<>();
        modules.add(module);
        final List<org.ikasan.trigger.model.Trigger> triggers = new ArrayList<>();
        final org.ikasan.trigger.model.Trigger trigger = mockery.mock(org.ikasan.trigger.model.Trigger.class);
        triggers.add(trigger);

        final Map<String,String> params = new HashMap<>();
        params.put("param1","value1");

        final Rule rule = mockery.mock(Rule.class,"mockRule");
        final JobDetail jobDetail = mockery.mock(JobDetail.class,"mockJobDetail");
        final JobKey jobKey = new JobKey("",moduleName);



        mockery.checking(new Expectations()
        {
            {
                oneOf(moduleService).getModules();
                will(returnValue(modules));

                oneOf(module).getName();
                will(returnValue(moduleName));

                oneOf(triggerDao).findTriggers(moduleName);
                will(returnValue(triggers));

                exactly(2).of(trigger).getModuleName();
                will(returnValue(moduleName));

                exactly(2).of(trigger).getFlowName();
                will(returnValue(flowName));

                exactly(2).of(trigger).getFlowElementName();
                will(returnValue(flowElementName));

                exactly(2).of(trigger).getRelationship();
                will(returnValue(TriggerRelationship.get("before")));

                oneOf(trigger).appliesToFlowElement();
                will(returnValue(true));

                oneOf(trigger).getJobName();
                will(returnValue("scheduleRuleEventJob"));

                exactly(2).of(trigger).getParams();
                will(returnValue(params));

                exactly(1).of(ruleService).createRule(params);
                will(returnValue(rule));

                exactly(1).of(ruleService).addRule("before test-flow-element-name|test-flow-name", rule);
                will(returnValue(rule));

                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)),with(any(Class.class)),with(any(String.class)),with(any(String.class)));
                will(returnValue(jobDetail));

                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));

                exactly(1).of(scheduler).checkExists(jobKey);
                will(returnValue(false));
            }
        });

        // do test
        uut.loadTriggers();

        //assert
        mockery.assertIsSatisfied();

    }


    @Test
    public void addDynamicTrigger_when_one(){

        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String flowElementName="test-flow-element-name";
        final org.ikasan.trigger.model.Trigger trigger = mockery.mock(org.ikasan.trigger.model.Trigger.class);

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerDao).save(trigger);

                oneOf(trigger).getModuleName();
                will(returnValue(moduleName));

                oneOf(trigger).getFlowName();
                will(returnValue(flowName));

                oneOf(trigger).getFlowElementName();
                will(returnValue(flowElementName));

                oneOf(trigger).getRelationship();
                will(returnValue(TriggerRelationship.get("before")));

                oneOf(trigger).appliesToFlowElement();
                will(returnValue(true));

                oneOf(trigger).getJobName();
                will(returnValue("jobName"));
            }
        });

        // do test
        uut.addDynamicTrigger(trigger);

        //assert
        mockery.assertIsSatisfied();

    }

    @Ignore
    @Test
    public void addDynamicTrigger_when_trigger_is_scheduleRuleEventJob() throws SchedulerException
    {

        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String flowElementName="test-flow-element-name";
        final org.ikasan.trigger.model.Trigger trigger = mockery.mock(org.ikasan.trigger.model.Trigger.class);

        final Map<String,String> params = new HashMap<>();
        params.put("param1","value1");

        final Rule rule = mockery.mock(Rule.class,"mockRule");
        final JobDetail jobDetail = mockery.mock(JobDetail.class,"mockJobDetail");
        final JobKey jobKey = new JobKey("",moduleName);



        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerDao).save(trigger);

                 exactly(2).of(trigger).getModuleName();
                will(returnValue(moduleName));

                exactly(2).of(trigger).getFlowName();
                will(returnValue(flowName));

                exactly(2).of(trigger).getFlowElementName();
                will(returnValue(flowElementName));

                exactly(2).of(trigger).getRelationship();
                will(returnValue(TriggerRelationship.get("before")));

                oneOf(trigger).appliesToFlowElement();
                will(returnValue(true));

                oneOf(trigger).getJobName();
                will(returnValue("scheduleRuleEventJob"));

                exactly(2).of(trigger).getParams();
                will(returnValue(params));

                exactly(1).of(ruleService).createRule(params);
                will(returnValue(rule));

                exactly(1).of(ruleService).addRule("before test-flow-element-name|test-flow-name", rule);
                will(returnValue(rule));

                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)),with(any(Class.class)),with(any(String.class)),with(any(String.class)));
                will(returnValue(jobDetail));

                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));

                exactly(1).of(scheduler).checkExists(jobKey);
                will(returnValue(false));
            }
        });

        // do test
        uut.addDynamicTrigger(trigger);

        //assert
        mockery.assertIsSatisfied();

    }

    @Test
    public void deleteDynamicTrigger_when_one(){

        ReflectionTestUtils.setField(uut, "triggersLoaded", true);

        final Long triggerId=1000L;
        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String flowElementName="test-flow-element-name";
        final org.ikasan.trigger.model.Trigger trigger = mockery.mock(org.ikasan.trigger.model.Trigger.class);

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerDao).findById(triggerId);
                will(returnValue(trigger));

                oneOf(trigger).getModuleName();
                will(returnValue(moduleName));

                oneOf(trigger).getFlowName();
                will(returnValue(flowName));

                oneOf(trigger).getFlowElementName();
                will(returnValue(flowElementName));

                oneOf(trigger).getRelationship();
                will(returnValue(TriggerRelationship.get("before")));

                oneOf(trigger).appliesToFlowElement();
                will(returnValue(true));

                exactly(1).of(triggerDao).delete(trigger);

            }
        });

        // do test
        uut.deleteDynamicTrigger(triggerId);

        //assert
        mockery.assertIsSatisfied();

    }

    @Test
    public void deleteDynamicTrigger_when_unmapTrigger_true(){

        ReflectionTestUtils.setField(uut, "triggersLoaded", true);

        final Long triggerId=1000L;
        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String flowElementName="test-flow-element-name";
        final org.ikasan.trigger.model.Trigger trigger = mockery.mock(org.ikasan.trigger.model.Trigger.class);

        final List<org.ikasan.trigger.model.Trigger> triggersList = new ArrayList<>();
        triggersList.add(trigger);
        Map<String, List<org.ikasan.trigger.model.Trigger>> triggers = new HashMap<String, List<org.ikasan.trigger.model.Trigger>>();

        triggers.put("test-moduletest-flow-namebeforetest-flow-element-name",triggersList);

        ReflectionTestUtils.setField(uut, "triggers", triggers);

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerDao).findById(triggerId);
                will(returnValue(trigger));

                exactly(1).of(trigger).getModuleName();
                will(returnValue(moduleName));

                exactly(1).of(trigger).getFlowName();
                will(returnValue(flowName));

                exactly(1).of(trigger).getFlowElementName();
                will(returnValue(flowElementName));

                exactly(1).of(trigger).getRelationship();
                will(returnValue(TriggerRelationship.get("before")));

                exactly(1).of(trigger).appliesToFlowElement();
                will(returnValue(true));

                exactly(2).of(trigger).getId();
                will(returnValue(triggerId));

                exactly(1).of(trigger).getJobName();
                will(returnValue("wiretapEventJob"));

                exactly(1).of(triggerDao).delete(trigger);

            }
        });

        // do test
        uut.deleteDynamicTrigger(triggerId);

        //assert
        mockery.assertIsSatisfied();

    }

    @Test
    public void deleteDynamicTrigger_when_unmapTrigger_true_and_job_is_scheduleRuleEventJob_and_job_does_not_exist_in_scheduler() throws SchedulerException
    {

        ReflectionTestUtils.setField(uut, "triggersLoaded", true);

        final Long triggerId=1000L;
        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String flowElementName="test-flow-element-name";
        final org.ikasan.trigger.model.Trigger trigger = mockery.mock(org.ikasan.trigger.model.Trigger.class);

        final List<org.ikasan.trigger.model.Trigger> triggersList = new ArrayList<>();
        triggersList.add(trigger);
        Map<String, List<org.ikasan.trigger.model.Trigger>> triggers = new HashMap<String, List<org.ikasan.trigger.model.Trigger>>();

        triggers.put("test-moduletest-flow-namebeforetest-flow-element-name",triggersList);

        ReflectionTestUtils.setField(uut, "triggers", triggers);

        final JobDetail jobDetail = mockery.mock(JobDetail.class,"mockJobDetail");
        final JobKey jobKey = new JobKey("",moduleName);


        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerDao).findById(triggerId);
                will(returnValue(trigger));

                exactly(2).of(trigger).getModuleName();
                will(returnValue(moduleName));

                exactly(2).of(trigger).getFlowName();
                will(returnValue(flowName));

                exactly(2).of(trigger).getFlowElementName();
                will(returnValue(flowElementName));

                exactly(2).of(trigger).getRelationship();
                will(returnValue(TriggerRelationship.get("before")));

                exactly(1).of(trigger).appliesToFlowElement();
                will(returnValue(true));

                exactly(2).of(trigger).getId();
                will(returnValue(triggerId));

                exactly(1).of(trigger).getJobName();
                will(returnValue("scheduleRuleEventJob"));

                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)),
                        with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));

                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));

                exactly(1).of(scheduler).checkExists(jobKey);
                will(returnValue(false));

                exactly(1).of(triggerDao).delete(trigger);

            }
        });

        // do test
        uut.deleteDynamicTrigger(triggerId);

        //assert
        mockery.assertIsSatisfied();

    }

    @Test
    public void deleteDynamicTrigger_when_unmapTrigger_true_and_job_is_scheduleRuleEventJob_and_job_exists_in_scheduler() throws SchedulerException
    {

        ReflectionTestUtils.setField(uut, "triggersLoaded", true);

        final Long triggerId=1000L;
        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String flowElementName="test-flow-element-name";
        final org.ikasan.trigger.model.Trigger trigger = mockery.mock(org.ikasan.trigger.model.Trigger.class);

        final List<org.ikasan.trigger.model.Trigger> triggersList = new ArrayList<>();
        triggersList.add(trigger);
        Map<String, List<org.ikasan.trigger.model.Trigger>> triggers = new HashMap<String, List<org.ikasan.trigger.model.Trigger>>();

        triggers.put("test-moduletest-flow-namebeforetest-flow-element-name",triggersList);

        ReflectionTestUtils.setField(uut, "triggers", triggers);

        final JobDetail jobDetail = mockery.mock(JobDetail.class,"mockJobDetail");
        final JobKey jobKey = new JobKey("",moduleName);


        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(triggerDao).findById(triggerId);
                will(returnValue(trigger));

                exactly(2).of(trigger).getModuleName();
                will(returnValue(moduleName));

                exactly(2).of(trigger).getFlowName();
                will(returnValue(flowName));

                exactly(2).of(trigger).getFlowElementName();
                will(returnValue(flowElementName));

                exactly(2).of(trigger).getRelationship();
                will(returnValue(TriggerRelationship.get("before")));

                exactly(1).of(trigger).appliesToFlowElement();
                will(returnValue(true));

                exactly(2).of(trigger).getId();
                will(returnValue(triggerId));

                exactly(1).of(trigger).getJobName();
                will(returnValue("scheduleRuleEventJob"));

                exactly(1).of(scheduledJobFactory).createJobDetail(with(any(Job.class)), with(any(Class.class)),
                        with(any(String.class)), with(any(String.class)));
                will(returnValue(jobDetail));

                exactly(1).of(jobDetail).getKey();
                will(returnValue(jobKey));

                exactly(1).of(scheduler).checkExists(jobKey);
                will(returnValue(true));

                exactly(1).of(scheduler).deleteJob(jobKey);
                will(returnValue(true));

                exactly(1).of(triggerDao).delete(trigger);

            }
        });

        // do test
        uut.deleteDynamicTrigger(triggerId);

        //assert
        mockery.assertIsSatisfied();

    }

    @Test
    public void beforeFlowElement_when_before_flow_element_is_wiretapEventJob(){

        ReflectionTestUtils.setField(uut, "triggersLoaded", true);

        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String flowElementName="test-flow-element-name";
        final FlowEvent flowEvent = mockery.mock(FlowEvent.class);
        final FlowElement flowElement = mockery.mock(FlowElement.class);

        final org.ikasan.trigger.model.Trigger trigger = mockery.mock(org.ikasan.trigger.model.Trigger.class);

        final List<org.ikasan.trigger.model.Trigger> triggersList = new ArrayList<>();
        triggersList.add(trigger);
        Map<String, List<org.ikasan.trigger.model.Trigger>> triggers = new HashMap<String, List<org.ikasan.trigger.model.Trigger>>();
        triggers.put("test-moduletest-flow-namebeforetest-flow-element-name", triggersList);

        ReflectionTestUtils.setField(uut, "triggers", triggers);

        final Map<String,String> params = new HashMap<>();
        params.put("param1","value1");


        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowElement).getComponentName();
                will(returnValue(flowElementName));

                exactly(1).of(trigger).getJobName();
                will(returnValue("wiretapEventJob"));

                exactly(1).of(trigger).getParams();
                will(returnValue(params));

                exactly(1).of(wiretapService).tapEvent(flowEvent,"before test-flow-element-name", moduleName, flowName, 10080L);

            }
        });

        // do test
        uut.beforeFlowElement(moduleName, flowName, flowElement, flowEvent);

        //assert
        mockery.assertIsSatisfied();

    }

    @Test
    public void beforeFlowElement_when_before_flow_element_is_scheduleRuleEventJob(){

        ReflectionTestUtils.setField(uut, "triggersLoaded", true);

        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String flowElementName="test-flow-element-name";
        final FlowEvent flowEvent = mockery.mock(FlowEvent.class);
        final FlowElement flowElement = mockery.mock(FlowElement.class);

        final org.ikasan.trigger.model.Trigger trigger = mockery.mock(org.ikasan.trigger.model.Trigger.class);

        final List<org.ikasan.trigger.model.Trigger> triggersList = new ArrayList<>();
        triggersList.add(trigger);
        Map<String, List<org.ikasan.trigger.model.Trigger>> triggers = new HashMap<String, List<org.ikasan.trigger.model.Trigger>>();
        triggers.put("test-moduletest-flow-namebeforetest-flow-element-name",triggersList);

        ReflectionTestUtils.setField(uut, "triggers", triggers);

        final Map<String,String> params = new HashMap<>();
        params.put("param1","value1");


        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowElement).getComponentName();
                will(returnValue(flowElementName));

                exactly(1).of(trigger).getJobName();
                will(returnValue("scheduleRuleEventJob"));

                exactly(1).of(trigger).getParams();
                will(returnValue(params));

                exactly(1).of(ruleService).getRule("before test-flow-element-name|test-flow-name");

            }
        });

        // do test
        uut.beforeFlowElement(moduleName,flowName,flowElement,flowEvent);

        //assert
        mockery.assertIsSatisfied();

    }


    @Test
    public void afterFlowElement_when_after_flow_element_is_wiretapEventJob(){

        ReflectionTestUtils.setField(uut, "triggersLoaded", true);

        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String flowElementName="test-flow-element-name";
        final FlowEvent flowEvent = mockery.mock(FlowEvent.class);
        final FlowElement flowElement = mockery.mock(FlowElement.class);

        final org.ikasan.trigger.model.Trigger trigger = mockery.mock(org.ikasan.trigger.model.Trigger.class);

        final List<org.ikasan.trigger.model.Trigger> triggersList = new ArrayList<>();
        triggersList.add(trigger);
        Map<String, List<org.ikasan.trigger.model.Trigger>> triggers = new HashMap<String, List<org.ikasan.trigger.model.Trigger>>();
        triggers.put("test-moduletest-flow-nameaftertest-flow-element-name", triggersList);

        ReflectionTestUtils.setField(uut, "triggers", triggers);

        final Map<String,String> params = new HashMap<>();
        params.put("param1","value1");


        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowElement).getComponentName();
                will(returnValue(flowElementName));

                exactly(1).of(trigger).getJobName();
                will(returnValue("wiretapEventJob"));

                exactly(1).of(trigger).getParams();
                will(returnValue(params));

                exactly(1).of(wiretapService).tapEvent(flowEvent,"after test-flow-element-name", moduleName, flowName, 10080L);

            }
        });

        // do test
        uut.afterFlowElement(moduleName, flowName, flowElement, flowEvent);

        //assert
        mockery.assertIsSatisfied();

    }

    @Test
    public void afterFlowElement_when_after_flow_element_is_scheduleRuleEventJob(){

        ReflectionTestUtils.setField(uut, "triggersLoaded", true);

        final String moduleName="test-module";
        final String flowName="test-flow-name";
        final String flowElementName="test-flow-element-name";
        final FlowEvent flowEvent = mockery.mock(FlowEvent.class);
        final FlowElement flowElement = mockery.mock(FlowElement.class);

        final org.ikasan.trigger.model.Trigger trigger = mockery.mock(org.ikasan.trigger.model.Trigger.class);

        final List<org.ikasan.trigger.model.Trigger> triggersList = new ArrayList<>();
        triggersList.add(trigger);
        Map<String, List<org.ikasan.trigger.model.Trigger>> triggers = new HashMap<String, List<org.ikasan.trigger.model.Trigger>>();
        triggers.put("test-moduletest-flow-nameaftertest-flow-element-name",triggersList);

        ReflectionTestUtils.setField(uut, "triggers", triggers);

        final Map<String,String> params = new HashMap<>();
        params.put("param1","value1");


        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(flowElement).getComponentName();
                will(returnValue(flowElementName));

                exactly(1).of(trigger).getJobName();
                will(returnValue("scheduleRuleEventJob"));

                exactly(1).of(trigger).getParams();
                will(returnValue(params));

                exactly(1).of(ruleService).getRule("after test-flow-element-name|test-flow-name");

            }
        });

        // do test
        uut.afterFlowElement(moduleName,flowName,flowElement,flowEvent);

        //assert
        mockery.assertIsSatisfied();

    }



}
