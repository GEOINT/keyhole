/*
 * Copyright 2005 Anders Nyman.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package net.sf.j2ep.rules;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import net.sf.j2ep.model.Rule;

/**
 * This rule consists of many other rules using the 
 * composite design pattern. The rule is matches if
 * all the included rules are matched.
 *
 * @author Anders Nyman
 */
public class CompositeRule extends BaseRule {

    /** 
     * The list of rules.
     */
    private final LinkedList rules;
    
    /**
     * Empty constructor, will only create the list of rules.
     */
    public CompositeRule() {
        rules = new LinkedList();
    }
    
    /**
     * Used to add a rule to the list.
     * @param rule The rule to be added
     */
    public void addRule(Rule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Rule to add cannot be null.");
        } else {
            rules.add(rule);
        }
    }
    
    /**
     * Iterates over all the rules in the list checking that they all match.
     * 
     * @param request
     * @return true if all the rules match or there are no rules, false otherwise
     * @see net.sf.j2ep.model.Rule#matches(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public boolean matches(HttpServletRequest request) {
        Iterator itr = rules.iterator();
        boolean matches = true;
        while (itr.hasNext() && matches) {
            Rule rule = (Rule) itr.next();
            matches = rule.matches(request);
        }
        
        return matches;
    }
    
    /**
     * Process all the rules in the list, allowing them all to change
     * the URI.
     * 
     * @param uri
     * @return 
     * @see net.sf.j2ep.model.Rule#process(java.lang.String)
     */
    @Override
    public String process(String uri) {
        String returnString = uri;
        Iterator itr = rules.iterator();
        while (itr.hasNext()) {
            Rule rule = (Rule) itr.next();
            returnString = rule.process(returnString);
        }
        
        return returnString;
    }
    
    /**
     * Will do the opposite of process, that is revert all URIs to there default
     * value. This method will call all rules in the rule list and call revert on them.
     * Rules are called in a reversed order in comparison with process.
     * 
     * @param uri
     * @return 
     * @see net.sf.j2ep.model.Rule#revert(java.lang.String)
     */
    @Override
    public String revert(String uri) {
        String returnString = uri;
        ListIterator itr = rules.listIterator(rules.indexOf(rules.getLast()));
        while (itr.hasPrevious()) {
            Rule rule = (Rule) itr.previous();
            returnString = rule.revert(returnString);
        }
        return returnString;
    }

    /**
     * Returns a String representation of this object.
     *
     * @return A string representation
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        buffer.append("[");
        buffer.append("CompositeRule containing ");
        
        Iterator itr = rules.iterator();
        while (itr.hasNext()) {
            Rule rule = (Rule) itr.next();
            buffer.append("(");
            buffer.append(rule.getClass().getName());
            buffer.append(") ");
        }
        
        buffer.append(": ");
        buffer.append("]");
        return buffer.toString();
    }

}
