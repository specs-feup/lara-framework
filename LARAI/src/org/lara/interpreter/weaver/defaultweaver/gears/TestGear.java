/**
 * Copyright 2014 SPeCS Research Group.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.interpreter.weaver.defaultweaver.gears;

import org.lara.interpreter.weaver.defaultweaver.options.DefaulWeaverKeys;
import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.events.data.ActionEvent;
import org.lara.interpreter.weaver.interf.events.data.ApplyEvent;
import org.lara.interpreter.weaver.interf.events.data.AspectEvent;
import org.lara.interpreter.weaver.interf.events.data.JoinPointEvent;
import org.lara.interpreter.weaver.interf.events.data.SelectEvent;
import org.lara.interpreter.weaver.interf.events.data.WeaverEvent;
import org.suikasoft.jOptions.Interfaces.DataStore;

/**
 * @author Tiago
 * 
 */
public class TestGear extends AGear {

    private boolean debug;

    public TestGear() {
	setDebug(false);
    }

    private void parseArgs(DataStore args) {
	// for (final String string : args) {
	// if (string.equals("/d") || string.endsWith("/debug")) {
	// }}
	if (args.hasValue(DefaulWeaverKeys.TEST_BOOLEAN)) {
	    setDebug(args.get(DefaulWeaverKeys.TEST_BOOLEAN));
	}
	if (debug) {
	    System.out.println("TestGear - Debug mode ON");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.lara.interpreter.weaver.interf.AGear#onWeaver(org.lara.interpreter.
     * weaver.interf.events.data.WeaverEvent)
     */
    @Override
    public void onWeaver(WeaverEvent data) {
	parseArgs(data.getArgs());
	if (debug) {
	    System.out.println("On Weaver - " + data.toString());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.lara.interpreter.weaver.interf.AGear#onAspect(org.lara.interpreter.
     * weaver.interf.events.data.AspectEvent)
     */
    @Override
    public void onAspect(AspectEvent data) {
	if (debug) {
	    System.out.println("On Aspect - " + data.toString());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.lara.interpreter.weaver.interf.AGear#onSelect(org.lara.interpreter.
     * weaver.interf.events.data.SelectEvent)
     */
    @Override
    public void onSelect(SelectEvent data) {
	if (debug) {
	    System.out.println("On Select - " + data.toString());
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.lara.interpreter.weaver.interf.AGear#onJoinPoint(org.lara.interpreter
     * .weaver.interf.events.data.JoinPointEvent)
     */
    @Override
    public void onJoinPoint(JoinPointEvent data) {
	if (debug) {
	    System.out.println("On JoinPoint - " + data.toString());
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.lara.interpreter.weaver.interf.AGear#onApply(org.lara.interpreter.
     * weaver.interf.events.data.ApplyEvent)
     */
    @Override
    public void onApply(ApplyEvent data) {
	if (debug) {
	    System.out.println("On Apply - " + data.toString());
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.lara.interpreter.weaver.interf.AGear#onAction(org.lara.interpreter.
     * weaver.interf.events.data.ActionEvent)
     */
    @Override
    public void onAction(ActionEvent data) {
	if (debug) {
	    System.out.println("On Action - " + data.toString());
	}

    }

    /**
     * @return the debug
     */
    public boolean isDebug() {
	return debug;
    }

    /**
     * @param debug
     *            the debug to set
     */
    public void setDebug(boolean debug) {
	this.debug = debug;
    }

}
