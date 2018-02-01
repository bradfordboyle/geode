/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.distributed.ConfigurationProperties;
import org.apache.geode.internal.cache.backup.BackupService;
import org.apache.geode.test.junit.categories.IntegrationTest;

@Category(IntegrationTest.class)
public class DiskStoreImplIntegrationTest {
  private static final String DISK_STORE_NAME = "testDiskStore";
  private static final String REGION_NAME = "testRegion";

  @Rule
  public TemporaryFolder temporaryDirectory = new TemporaryFolder();

  private Cache cache;

  @Before
  public void setup() {
    cache = createCache();
  }

  @After
  public void tearDown() {
    if (cache != null && !cache.isClosed()) {
      cache.close();
    }
  }

  @Test
  public void cleansUpOrphanedBackupFilesOnDiskStoreCreation() throws Exception {
    File baseDir = temporaryDirectory.newFolder();
    createRegionWithDiskStore(baseDir);
    DiskStore diskStore = cache.findDiskStore(DISK_STORE_NAME);

    List<Path> tempDirs = new ArrayList<>();
    for (File diskDir : diskStore.getDiskDirs()) {
      Path tempDir =
          diskDir.toPath().resolve(BackupService.DATA_STORES_TEMPORARY_DIRECTORY + "testing");
      Files.createDirectories(tempDir);
      tempDirs.add(tempDir);
    }

    cache.close();
    cache = createCache();
    createRegionWithDiskStore(baseDir);

    tempDirs.forEach(tempDir -> assertThat(Files.exists(tempDir)).isFalse());
  }

  private void createRegionWithDiskStore(File baseDir) {
    cache.createDiskStoreFactory().setDiskDirs(new File[] {baseDir}).create(DISK_STORE_NAME);
    cache.<String, String>createRegionFactory(RegionShortcut.PARTITION_PERSISTENT)
        .setDiskStoreName(DISK_STORE_NAME).create(REGION_NAME);
  }

  private Cache createCache() {
    // Setting MCAST port explicitly is currently required due to default properties set in gradle
    return new CacheFactory().set(ConfigurationProperties.MCAST_PORT, "0").create();
  }
}
