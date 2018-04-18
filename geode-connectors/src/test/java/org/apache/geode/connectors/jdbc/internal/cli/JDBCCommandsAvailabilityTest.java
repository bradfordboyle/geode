/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.geode.connectors.jdbc.internal.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.shell.core.CommandMarker;

import org.apache.geode.management.internal.cli.CommandManager;
import org.apache.geode.management.internal.cli.commands.CommandAvailabilityIndicatorTest;
import org.apache.geode.test.junit.categories.UnitTest;


@Category(UnitTest.class)
public class JDBCCommandsAvailabilityTest {

  @Test
  public void jdbcCommandsAvailable() {
    CommandManager manager = new CommandManager();
    Set<Class<? extends CommandMarker>> collections = manager.getCommandMarkers().stream()
        .map(CommandMarker::getClass).collect(Collectors.toSet());

    // make sure the jdbc commands are loaded
    assertThat(collections).contains(CreateConnectionCommand.class);

    CommandAvailabilityIndicatorTest.assertOnlineCommandsHasAvailabilityIndicator(manager);
  }
}
