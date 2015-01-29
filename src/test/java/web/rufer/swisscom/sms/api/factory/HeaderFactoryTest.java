/*
 * Copyright (C) 2015 Marc Rufer (m.rufer@gmx.ch)
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
package web.rufer.swisscom.sms.api.factory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import static org.junit.Assert.assertEquals;

public class HeaderFactoryTest {

    private final String API_KEY = "12345";

    @Before
    public void init() {
    }

    @Test
    public void createHeadersReturnsHeadersWithAPIKey() {
        HttpHeaders headers = HeaderFactory.createHeaders(API_KEY);
        assertEquals(API_KEY, headers.get("client_id").get(0));
    }
}
