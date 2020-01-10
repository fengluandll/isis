/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.core.commons.internal.context;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.isis.core.commons.collections.Can;
import org.apache.isis.core.commons.internal.base._Casts;
import org.apache.isis.core.commons.internal.collections._Lists;
import org.apache.isis.core.commons.internal.base._NullSafe;
import org.apache.isis.core.commons.internal.base._With;

import static org.apache.isis.core.commons.internal.base._NullSafe.stream;

/**
 * <h1>- internal use only -</h1>
 * <p>
 * Provides a context for storing and retrieving singletons (usually application scoped).
 * Writes to the store are implemented thread-safe.
 * </p>
 * <p>
 * <b>WARNING</b>: Do <b>NOT</b> use any of the classes provided by this package! <br/>
 * These may be changed or removed without notice!
 * </p>
 * @since 2.0
 */
public final class _Context {

    private _Context(){}

    /**
     * Thread-safety note: We let threads synchronize on writes to the singletonMap,
     * NOT on reads.<br/>
     * If there is a race-condition between a writing and a reading thread, by design
     * the first one wins.<br/>
     * If synchronization is required it should happen elsewhere, not here!<br/>
     */
    private final static Map<Class<?>, Object> singletonMap = new ConcurrentHashMap<>();

    private final static Object $LOCK = new Object[0];


    /**
     * Puts a singleton instance onto the current context.
     * @param type non-null
     * @param singleton non-null
     * @throws IllegalStateException if there is already an instance of same {@code type}
     *  on the current context.
     */
    public static <T> void putSingleton(Class<? super T> type, T singleton) {
        _With.requires(type, "type");
        _With.requires(singleton, "singleton");

        // let writes to the map be atomic
        synchronized ($LOCK) {
            if(singletonMap.containsKey(type))
                throw new IllegalStateException(
                        "there is already a singleton of type '"+type+"' on this context.");
            singletonMap.put(type, singleton);
        }
    }

    /**
     * Puts a singleton instance onto the current context, that
     * either overrides any already present or ignores the call depending on {@code override}.
     * @param type non-null
     * @param singleton non-null
     * @param override whether to overrides any already present singleton or not
     * @return whether the {@code singleton} was put on the context or ignored because there is already one present
     */
    public static <T> boolean put(Class<? super T> type, T singleton, boolean override) {
        _With.requires(type, "type");
        _With.requires(singleton, "singleton");

        // let writes to the map be atomic
        synchronized ($LOCK) {
            if(singletonMap.containsKey(type)) {
                if(!override)
                    return false;
            }
            singletonMap.put(type, singleton);
            return true;
        }
    }


    /**
     * Gets a singleton instance of {@code type} if there is any, null otherwise.
     * @param type non-null
     * @return null, if there is no such instance
     */
    public static <T> T getIfAny(Class<? super T> type) {
        return _Casts.uncheckedCast(singletonMap.get(type));
    }

    /**
     * If the specified key is not already associated with a value (or is mapped to null),
     * attempts to compute its value using the given factory function and enters it into this map unless null.
     * @param type
     * @param factory
     * @return null, if there is no such instance
     */
    public static <T> T computeIfAbsent(Class<? super T> type, Function<Class<? super T>, T> factory) {
        _With.requires(type, "type");
        _With.requires(factory, "factory");

        final T existingIfAny = _Casts.uncheckedCast(singletonMap.get(type));
        if(existingIfAny!=null) {
            return existingIfAny;
        }

        // Note: we don't want to do this inside the synchronized block
        final T t = factory.apply(type);

        // we don't store null to the map
        if(t==null) {
            return null;
            //        	throw _Exceptions.unrecoverable(String.format("factory to compute new value for type '%s' "
            //        			+ "returned 'null', which is not allowed", type));
        }

        // let writes to the map be atomic
        synchronized ($LOCK) {

            // Note: cannot just use 'singletonMap.computeIfAbsent(toKey(type), __->factory.apply(type));'
            // here because it does not allow for modification of singletonMap inside the factory call
            // Also we do need a second check for existing key, since it might have been changed by another
            // thread since.
            final T existingIfAny2 = _Casts.uncheckedCast(singletonMap.get(type));
            if(existingIfAny2!=null) {
                return existingIfAny2;
            }        	

            singletonMap.put(type, t);
            return t;
        }
    }

    /**
     * If the specified key is not already associated with a value (or is mapped to null),
     * attempts to compute its value using the given factory supplier and enters it into this map unless null.
     * @param type
     * @param factory
     * @return null, if there is no such instance
     */
    public static <T> T computeIfAbsent(Class<? super T> type, Supplier<T> factory) {
        _With.requires(type, "type");
        _With.requires(factory, "factory");
        return computeIfAbsent(type, __->factory.get());
    }



    /**
     * Gets a singleton instance of {@code type} if there is any,
     * otherwise returns the {@code fallback}'s result,
     * which could be null.
     * @param type non-null
     * @param fallback non-null
     * @return
     */
    public static <T> T getOrElse(Class<? super T> type, Supplier<T> fallback) {
        _With.requires(fallback, "fallback");
        return _With.ifPresentElseGet(getIfAny(type), fallback);
    }

    /**
     * Gets a singleton instance of {@code type} if there is any,
     * otherwise throws the {@code onNotFound}'s result.
     * @param type non-null
     * @param onNotFound non-null
     * @return
     * @throws Exception
     */
    public static <T, E extends Exception> T getElseThrow (
            Class<? super T> type,
            Supplier<E> onNotFound)
                    throws E {

        _With.requires(type, "type");
        _With.requires(onNotFound, "onNotFound");
        return _With.ifPresentElseThrow(getIfAny(type), onNotFound);
    }

    /**
     * Gets a singleton instance of {@code type} if there is any,
     * otherwise throws a NoSuchElementException.
     * @param type non-null
     * @return
     */
    public static <T> T getElseFail(Class<? super T> type) {
        return _With.ifPresentElseThrow(getIfAny(type), ()->
        new NoSuchElementException(String.format("Could not resolve an instance of type '%s'", type.getName())));
    }


    // -- REMOVAL

    public static void remove(Class<?> type) {
        // let writes to the map be atomic
        synchronized ($LOCK) {
            singletonMap.remove(type);
        }
        tryClose(type);
    }

    // -- CLEARING

    /**
     * Removes any singleton references from the current context. <br/>
     * Any singletons that implement the AutoClosable interface are being closed.
     */
    public static void clear() {

        // let writes to the map be atomic
        synchronized ($LOCK) {

            closeAnyClosables(_Lists.newArrayList(singletonMap.values()));

            singletonMap.clear();
            _Context_ThreadLocal.clear();
        }
    }

    private static void closeAnyClosables(List<Object> objects) {
        _NullSafe.stream(objects)
        .forEach(_Context::tryClose);
    }

    // -- THREAD LOCAL SUPPORT

    /**
     * Clear key {@code type} from current thread's map.
     * @param type - the key into the thread-local store
     */
    public static void threadLocalClear(Class<?> type) {
        _Context_ThreadLocal.clear(type);
    }

    /**
     * Puts {@code payload} onto the current thread's map.
     * @param type - the key into the thread-local store
     * @param payload
     * @return a Runnable which, when run, removes any references to payload
     */
    public static <T> Runnable threadLocalPut(Class<? super T> type, T payload) {
        return _Context_ThreadLocal.put(type, payload);
    }


    /**
     * Looks up current thread's values for any instances that match the given type, as previously stored 
     * with {@link _Context#threadLocalPut(Class, Object)}.
     * @param type - the key into the thread-local store
     * @return
     */
    public static <T> Can<T> threadLocalGet(Class<? super T> type) {
        return _Context_ThreadLocal.get(type);
    }

    /**
     * Looks up current thread's values for any instances that match the given type, as previously stored 
     * with {@link _Context#threadLocalPut(Class, Object)}.
     * @param type - the key into the thread-local store
     * @param requiredType - the required type of the elements in the returned bin
     * @return
     */
    public static <T> Can<T> threadLocalSelect(Class<? super T> type, Class<? super T> requiredType) {
        return _Context_ThreadLocal.select(type, requiredType);
    }

    /**
     * Removes any of current thread's values as stored with {@link _Context#threadLocalPut(Class, Object)}.
     */
    public static <T> void threadLocalCleanup() {
        _Context_ThreadLocal.cleanupThread();
    }


    // -- DEFAULT CLASSLOADER

    private final static Supplier<ClassLoader> FALLBACK_CLASSLOADER =
            Thread.currentThread()::getContextClassLoader;

    /**
     * As set by the framework's bootstrapping mechanism.
     * @return the default class loader (non-null)
     */
    public static ClassLoader getDefaultClassLoader() {
        return getOrElse(ClassLoader.class, FALLBACK_CLASSLOADER);
    }

    /**
     * Set by the framework's bootstrapping mechanism.
     * @param classLoader the framework's default class loader
     * @param override whether to override if already registered
     */
    public static void setDefaultClassLoader(ClassLoader classLoader, boolean override) {
        final boolean alreadyRegistered = _Context.getIfAny(ClassLoader.class)!=null;
        if(!alreadyRegistered || override) {
            // let writes to the map be atomic
            synchronized ($LOCK) {
                singletonMap.put(ClassLoader.class, _With.requires(classLoader, "classLoader"));
            }
        }
    }

    // -- CLASS LOADING SHORTCUTS

    /**
     * Uses the frameworks default-ClassLoader to load a class by name.
     * @param className
     * @return class by name
     * @throws ClassNotFoundException
     */
    public static Class<?> loadClass(String className) throws ClassNotFoundException{
        return getDefaultClassLoader().loadClass(className);
    }

    /**
     * Uses the frameworks default-ClassLoader to load and initialize a class by name.<br/>
     * <b>Initialize</b> the class, that is, all static initializers will be run. <br/>
     * (For details on initialize see Section 12.4 of The Java Language Specification)
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> loadClassAndInitialize(String className) throws ClassNotFoundException{
        return Class.forName(className, true, getDefaultClassLoader());
    }

    // -- HELPER

    private static void tryClose(Object singleton) {
        if(singleton==null) {
            return;
        }
        if(singleton instanceof AutoCloseable) {
            try {
                ((AutoCloseable)singleton).close();
            } catch (Exception e) {
                // [ahuber] nothing we can do here, so ignore
            }
        }
    }






}