/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.core.items.events;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.events.Event;
import org.openhab.core.events.EventSubscriber;

/**
 * <p>
 * The {@link AbstractItemEventSubscriber} defines an abstract implementation of the {@link EventSubscriber} interface
 * for receiving {@link ItemStateEvent}s and {@link ItemCommandEvent}s from the openHAB event bus.
 *
 * A subclass can implement the methods {@link #receiveUpdate(ItemStateEvent)} and
 * {@link #receiveCommand(ItemCommandEvent)} in order to receive and handle such events.
 *
 * @author Stefan Bußweiler - Initial contribution
 */
@NonNullByDefault
public abstract class AbstractItemEventSubscriber implements EventSubscriber {

    private final Set<String> subscribedEventTypes = Set.of(ItemStateEvent.TYPE, ItemCommandEvent.TYPE,
            ItemTimeSeriesEvent.TYPE);

    @Override
    public Set<String> getSubscribedEventTypes() {
        return subscribedEventTypes;
    }

    @Override
    public void receive(Event event) {
        if (event instanceof ItemStateEvent stateEvent) {
            receiveUpdate(stateEvent);
        } else if (event instanceof ItemCommandEvent commandEvent) {
            receiveCommand(commandEvent);
        } else if (event instanceof ItemTimeSeriesEvent timeSeriesEvent) {
            receiveTimeSeries(timeSeriesEvent);
        }
    }

    /**
     * Callback method for receiving item command events from the openHAB event bus.
     *
     * @param commandEvent the item command event
     */
    protected void receiveCommand(ItemCommandEvent commandEvent) {
        // Default implementation: do nothing.
        // Can be implemented by subclass in order to handle item commands.
    }

    /**
     * Callback method for receiving item update events from the openHAB event bus.
     *
     * @param updateEvent the item update event
     */
    protected void receiveUpdate(ItemStateEvent updateEvent) {
        // Default implementation: do nothing.
        // Can be implemented by subclass in order to handle item updates.
    }

    /**
     * Callback method for receiving item timeseries events from the openHAB event bus.
     *
     * @param timeSeriesEvent the timeseries event
     */
    protected void receiveTimeSeries(ItemTimeSeriesEvent timeSeriesEvent) {
        // Default implementation: do nothing.
        // Can be implemented by subclass in order to handle timeseries updates.
    }
}
