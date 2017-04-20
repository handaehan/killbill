/*
 * Copyright 2010-2012 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.util.tag.api.user;

import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.killbill.billing.util.UtilTestSuiteNoDB;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.util.jackson.ObjectMapper;
import org.killbill.billing.util.tag.DefaultTagDefinition;
import org.killbill.billing.util.tag.TagDefinition;

public class TestDefaultUserTagDefinitionDeletionEvent extends UtilTestSuiteNoDB {

    @Test(groups = "fast")
    public void testPojo() throws Exception {
        final UUID tagDefinitionId = UUID.randomUUID();
        final String tagDefinitionName = UUID.randomUUID().toString();
        final String tagDefinitionDescription = UUID.randomUUID().toString();
        final boolean controlTag = false;
        final TagDefinition tagDefinition = new DefaultTagDefinition(tagDefinitionId, tagDefinitionName, tagDefinitionDescription, controlTag);
        final UUID userToken = UUID.randomUUID();

        final DefaultUserTagDefinitionDeletionEvent event = new DefaultUserTagDefinitionDeletionEvent(tagDefinitionId, tagDefinition, 1L, 2L, UUID.randomUUID());
        Assert.assertEquals(event.getBusEventType(), BusInternalEvent.BusInternalEventType.USER_TAGDEFINITION_DELETION);

        Assert.assertEquals(event.getTagDefinitionId(), tagDefinitionId);
        Assert.assertEquals(event.getTagDefinition(), tagDefinition);
        Assert.assertEquals(event.getTagDefinition().getId(), tagDefinitionId);
        Assert.assertEquals(event.getTagDefinition().getName(), tagDefinitionName);
        Assert.assertEquals(event.getTagDefinition().getDescription(), tagDefinitionDescription);

        Assert.assertEquals(event, event);
        Assert.assertEquals(event, new DefaultUserTagDefinitionDeletionEvent(tagDefinitionId, tagDefinition, 1L, 2L, UUID.randomUUID()));
        Assert.assertTrue(event.equals(event));
        Assert.assertTrue(event.equals(new DefaultUserTagDefinitionDeletionEvent(tagDefinitionId, tagDefinition, 1L, 2L, UUID.randomUUID())));
    }

    @Test(groups = "fast")
    public void testSerialization() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        final UUID tagDefinitionId = UUID.randomUUID();
        final String tagDefinitionName = UUID.randomUUID().toString();
        final String tagDefinitionDescription = UUID.randomUUID().toString();
        final boolean controlTag = false;
        final TagDefinition tagDefinition = new DefaultTagDefinition(tagDefinitionId, tagDefinitionName, tagDefinitionDescription, controlTag);
        final UUID userToken = UUID.randomUUID();

        final DefaultUserTagDefinitionDeletionEvent event = new DefaultUserTagDefinitionDeletionEvent(tagDefinitionId, tagDefinition, 1L, 2L, UUID.randomUUID());

        final String json = objectMapper.writeValueAsString(event);
        final DefaultUserTagDefinitionDeletionEvent fromJson = objectMapper.readValue(json, DefaultUserTagDefinitionDeletionEvent.class);
        Assert.assertEquals(fromJson, event);
    }
}
