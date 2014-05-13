/*
 * Copyright (C) 2014 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.languager;

import org.junit.Test;

import java.util.SortedMap;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class PropertiesFinderTest {
    @Test
    public void testFindProperties() throws Exception {
        PropertiesFinder finder = new PropertiesFinder();
        finder.addPropertyLocation("classpath*:org/hibernate/validator/ValidationMessages");
        SortedMap<String, Message> properties = finder.findProperties();
        assertTrue(properties.size() > 10);
        Message message = properties.values().iterator().next();
        assertTrue(message.getValues().size() > 1);
    }
}
