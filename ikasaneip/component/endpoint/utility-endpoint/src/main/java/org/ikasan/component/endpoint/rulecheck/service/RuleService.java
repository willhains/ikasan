package org.ikasan.component.endpoint.rulecheck.service;

import org.ikasan.component.endpoint.rulecheck.Rule;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amajewski on 19/03/16.
 */
public class RuleService
{
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

    public Rule createRule(String ruleClass)
    {
        try
        {
            Class<?> c = Class.forName(ruleClass);
            Constructor<?> cons = c.getConstructor();
            Object object = cons.newInstance();
            if (object instanceof Rule) {
                return (Rule)object;
            }else{
                throw new RuntimeException("Provided class"+ruleClass+" is not a instance of Rule.class");
            }

        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);

        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }
}
