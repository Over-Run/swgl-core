/*
 * MIT License
 *
 * Copyright (c) 2022 Overrun Organization
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.overrun.swgl.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logger factory by {@link StackWalker}.
 *
 * @author squid233
 * @since 0.2.0
 */
public final class LogFactory9 {
    /**
     * Gets the logger by caller.
     *
     * @return the logger
     */
    public static Logger getLogger() {
        return LoggerFactory.getLogger(CallerGetter.GETTER.getCallerClass());
    }

    /**
     * Gets the logger by caller.
     * the logger name is simple
     *
     * @return the logger
     */
    public static Logger getLoggerS() {
        return LoggerFactory.getLogger(CallerGetter.GETTER.getCallerClass().getSimpleName());
    }
}
