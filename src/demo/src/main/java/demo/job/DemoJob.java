/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package demo.job;

import africa.absa.inception.scheduler.IJob;
import africa.absa.inception.scheduler.JobExecutionContext;
import africa.absa.inception.scheduler.JobExecutionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <b>DemoJob</b> class.
 *
 * @author Marcus Portmann
 */
public class DemoJob implements IJob {

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(DemoJob.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionFailedException {
    logger.info("Demo job execution started");

    //    if (true) {
    //      throw new JobExecutionFailedException("Testing 1.. 2.. 3..");
    //    }

    try {
      Thread.sleep(20000L);
    } catch (Throwable e) {
    }

    logger.info("Demo job execution finished");
  }
}
