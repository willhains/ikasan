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
package org.ikasan.component.endpoint.rulecheck.service;

import org.ikasan.component.endpoint.rulecheck.Rule;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Rule service to create Rule instance based on provided parameters.
 * Parameters provided shall provide rule class and rule configuration class.
 */
public class RuleService
{
    public static final String RULE_CLASS = "ruleClass";

    public static final String RULE_CONFIGURATION_CLASS = "ruleConfigurationClass";

    public static final String RULE_CONFIGURATION_PREFIX = "ruleConfiguration.";

    Map<String, Rule> rules = new HashMap<String, Rule>();

    public Map<String, Rule> getRules()
    {
        return rules;
    }

    public void setRules(Map<String, Rule> rules)
    {
        this.rules = rules;
    }

    public Rule getRule(String name)
    {
        return rules.get(name);
    }

    public Rule addRule(String name, Rule rule)
    {
        return rules.put(name, rule);
    }

    public Rule createRule(Map<String, String> param)
    {
        Object ruleConfiguration = null;
        String ruleConfigurationClass = param.get(RULE_CONFIGURATION_CLASS);
        if (ruleConfigurationClass != null && !ruleConfigurationClass.isEmpty())
        {
            ruleConfiguration = instantiateClass(ruleConfigurationClass);
            setConfigurationProperties(ruleConfiguration, param);
        }
        String ruleClass = param.get(RULE_CLASS);
        Object rule = instantiateClass(ruleClass);


        if (ruleConfiguration!=null && BeanUtils.findDeclaredMethod(rule.getClass(), "setConfiguration", ruleConfiguration.getClass()) != null)
        {
            PropertyAccessor ruleAccessor = PropertyAccessorFactory.forDirectFieldAccess(rule);
            ruleAccessor.setPropertyValue("configuration", ruleConfiguration);
        }
        if (rule instanceof Rule)
        {
            ((Rule) rule).rebase();
            return (Rule) rule;
        }
        else
        {
            throw new RuntimeException("Provided class" + ruleClass + " is not a instance of Rule.class");
        }
    }

    private Object instantiateClass(String className)
    {
        try
        {
            Class<?> c = Class.forName(className);
            Object object = BeanUtils.instantiateClass(c);
            return object;
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void setConfigurationProperties(Object ruleConfiguration, Map<String, String> param)
    {
        PropertyAccessor myAccessor = PropertyAccessorFactory.forDirectFieldAccess(ruleConfiguration);
        for (Map.Entry<String, String> entry : param.entrySet())
        {
            if (entry.getKey().startsWith(RULE_CONFIGURATION_PREFIX))
            {
                String propertyName = entry.getKey().substring(RULE_CONFIGURATION_PREFIX.length());
                myAccessor.setPropertyValue(propertyName, entry.getValue());
            }
        }
    }
}
