/*
 * Copyright 2016 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.eva.pipeline.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 *
 * {@link JobExecutionListener} implementation
 * that logs the duration of the job.
 *
 */
public class JobDurationListener implements JobExecutionListener{
    private static final Logger logger = LoggerFactory.getLogger(JobDurationListener.class);

    private StopWatch stopWatch;
    private String jobName;

    public JobDurationListener(String jobName) {
        this.jobName = jobName;
    }

    public void beforeJob(JobExecution jobExecution) {
        stopWatch = new StopWatch();
        stopWatch.start("Processing job " + jobName);
    }

    public void afterJob(JobExecution jobExecution) {
        stopWatch.stop();

        long duration = stopWatch.getLastTaskTimeMillis();

        logger.info(String.format("Job %s took: %d minutes, %d seconds.",
                jobName,
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        ));
    }
}
