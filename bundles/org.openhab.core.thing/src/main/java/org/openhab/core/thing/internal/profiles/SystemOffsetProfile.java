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
package org.openhab.core.thing.internal.profiles;

import java.math.BigDecimal;

import javax.measure.UnconvertibleException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.profiles.ProfileCallback;
import org.openhab.core.thing.profiles.ProfileContext;
import org.openhab.core.thing.profiles.ProfileTypeUID;
import org.openhab.core.thing.profiles.StateProfile;
import org.openhab.core.thing.profiles.SystemProfiles;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.Type;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Applies the given parameter "offset" to a {@link QuantityType} or {@link DecimalType} state.
 *
 * @author Stefan Triller - Initial contribution
 */
@NonNullByDefault
public class SystemOffsetProfile implements StateProfile {

    static final String OFFSET_PARAM = "offset";

    private final Logger logger = LoggerFactory.getLogger(SystemOffsetProfile.class);

    private final ProfileCallback callback;

    private QuantityType<?> offset = QuantityType.ONE;

    public SystemOffsetProfile(ProfileCallback callback, ProfileContext context) {
        this.callback = callback;

        Object paramValue = context.getConfiguration().get(OFFSET_PARAM);
        logger.debug("Configuring profile with {} parameter '{}'", OFFSET_PARAM, paramValue);
        if (paramValue instanceof String string) {
            try {
                offset = new QuantityType<>(string);
            } catch (IllegalArgumentException e) {
                logger.error(
                        "Cannot convert value '{}' of parameter '{}' into a valid offset of type QuantityType. Using offset 0 now.",
                        paramValue, OFFSET_PARAM);
            }
        } else if (paramValue instanceof BigDecimal bd) {
            offset = new QuantityType<>(bd.toString());
        } else {
            logger.error(
                    "Parameter '{}' is not of type String or BigDecimal. Please make sure it is one of both, e.g. 3, \"-1.4\" or \"3.2°C\".",
                    OFFSET_PARAM);
        }
    }

    @Override
    public ProfileTypeUID getProfileTypeUID() {
        return SystemProfiles.OFFSET;
    }

    @Override
    public void onStateUpdateFromItem(State state) {
    }

    @Override
    public void onCommandFromItem(Command command) {
        callback.handleCommand((Command) applyOffset(command, false));
    }

    @Override
    public void onCommandFromHandler(Command command) {
        callback.sendCommand((Command) applyOffset(command, true));
    }

    @Override
    public void onStateUpdateFromHandler(State state) {
        callback.sendUpdate((State) applyOffset(state, true));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Type applyOffset(Type state, boolean towardsItem) {
        if (state instanceof UnDefType) {
            // we cannot adjust UNDEF or NULL values, thus we simply return them without reporting an error or warning
            return state;
        }

        QuantityType finalOffset = towardsItem ? offset : offset.negate();
        Type result = UnDefType.UNDEF;
        if (state instanceof QuantityType qtState) {
            try {
                if (Units.ONE.equals(finalOffset.getUnit()) && !Units.ONE.equals(qtState.getUnit())) {
                    // allow offsets without unit -> implicitly assume its the same as the one from the state, but warn
                    // the user
                    logger.warn(
                            "Received a QuantityType state '{}' with unit, but the offset is defined as a plain number without unit ({}), please consider adding a unit to the profile offset.",
                            state, offset);
                    finalOffset = new QuantityType<>(finalOffset.toBigDecimal(), qtState.getUnit());
                }
                result = qtState.add(finalOffset);
            } catch (UnconvertibleException e) {
                logger.warn("Cannot apply offset '{}' to state '{}' because types do not match.", finalOffset, qtState);
            }
        } else if (state instanceof DecimalType decState && Units.ONE.equals(finalOffset.getUnit())) {
            result = new DecimalType(decState.toBigDecimal().add(finalOffset.toBigDecimal()));
        } else {
            logger.warn(
                    "Offset '{}' cannot be applied to the incompatible state '{}' sent from the binding. Returning original state.",
                    offset, state);
            result = state;
        }
        return result;
    }
}
