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

import org.apache.log4j.Logger;
import org.ikasan.component.endpoint.rulecheck.Rule;
import org.ikasan.component.endpoint.rulecheck.service.RuleService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.flow.FlowEvent;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.*;

/**
 * <code>FlowEventJob</code> for invoking the ScheduledRuleCheckerService
 * <p/>
 * requires the parameter 'ruleName' to be passed on execution
 *
 * @author Ikasan Development Team
 */
public class ScheduleRuleEventJob implements FlowEventJob, Job
{
    /**
     * logger instance
     */
    private static final Logger logger = Logger.getLogger(ScheduleRuleEventJob.class);

    /**
     * underlying service
     */
    private final RuleService ruleService;

    /**
     * Error Reporting service used o notify failures in case of exceptions during scheduler
     * callback process.
     */
    private final ErrorReportingService errorReportingService;

    public static final String RULE_CLASS = "ruleClass";

    public static final String CRON_EXPRESSION = "cronExpression";

    public static final String RULE_CONFIGURATION_CLASS = "ruleConfigurationClass";

    public static final String RULE_CONFIGURATION_PREFIX = "ruleConfiguration.";

    /**
     * List of names of parameters supported by this job
     */
    private static final List<String> parameterNames = new ArrayList<String>();

    static
    {
        parameterNames.add(RULE_CLASS);
        parameterNames.add(RULE_CONFIGURATION_CLASS);
        parameterNames.add(CRON_EXPRESSION);
        parameterNames.add(RULE_CONFIGURATION_PREFIX + "timeInterval");
    }

    /**
     * Constructor
     *
     * @param ruleService           The rule service to use
     * @param errorReportingService the error service
     */
    public ScheduleRuleEventJob(final RuleService ruleService, final ErrorReportingService errorReportingService)
    {
        super();
        this.ruleService = ruleService;
        this.errorReportingService = errorReportingService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.flow.event.service.FlowEventJob#execute(java.lang
     * .String, java.lang.String, java.lang.String,
     * org.ikasan.spec.flow.event.FlowEvent, java.util.Map)
     */
    public void execute(String location, String moduleName, String flowName, FlowEvent event,
            Map<String, String> params)
    {
        String ruleName = location + "|" + flowName;
        Rule rule = ruleService.getRule(ruleName);
        if (rule != null)
        {
            logger.debug("Updating rule [" + ruleName + "]");
            rule.update(event);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ikasan.framework.flow.event.service.FlowEventJob#getParameters()
     */
    public List<String> getParameters()
    {
        return new ArrayList<String>(parameterNames);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ikasan.framework.flow.event.service.FlowEventJob#validateParameters
     * (java.util.Map)
     */
    public Map<String, String> validateParameters(Map<String, String> params)
    {
        Map<String, String> result = new HashMap<String, String>();
        return result;
    }

    /**
     * Method is triggered by quartz scheduler.
     *
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        String flowElementName = null;
        try
        {
            String ruleName = jobExecutionContext.getJobDetail().getKey().getName();
            flowElementName = getFlowElementName(ruleName);
            Rule rule = ruleService.getRule(ruleName);
            if (rule != null)
            {
                logger.debug("Execute rule check [" + ruleName + "] base on scheduler[" + new Date() + "]");
                rule.check(jobExecutionContext);
            }
        }
        catch (Throwable t)
        {
            this.errorReportingService.notify(flowElementName, t);
        }
    }

    private String getFlowElementName(String ruleName)
    {
        int separatorIndex = ruleName.indexOf('|');
        if (separatorIndex == -1)
        {
            return ruleName;
        }
        String flowElementName = ruleName.substring(0, separatorIndex);
        flowElementName = flowElementName.replace("before ", "");
        flowElementName = flowElementName.replace("after ", "");
        return flowElementName;
    }
}
