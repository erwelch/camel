/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.netty4.http;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;

public class NettyHttpReturnFaultTest extends BaseNettyTest {

    @Test
    public void testHttpFault() throws Exception {
        Exchange exchange = template.request("netty4-http:http://localhost:{{port}}/test", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("Hello World!");
            }

        });
        assertTrue(exchange.isFailed());
        NettyHttpOperationFailedException exception = exchange.getException(NettyHttpOperationFailedException.class);
        assertNotNull(exception);
        assertEquals(500, exception.getStatusCode());
        String message = context.getTypeConverter().convertTo(String.class, exception.getHttpContent().content());
        assertEquals("This is a fault", message);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("netty4-http:http://localhost:{{port}}/test")
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                exchange.getOut().setFault(true);
                                exchange.getOut().setBody("This is a fault");
                            }
                        });
            }
        };
    }
}