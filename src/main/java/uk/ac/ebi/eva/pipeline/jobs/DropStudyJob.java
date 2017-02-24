/*
 * Copyright 2015-2017 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.eva.pipeline.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import uk.ac.ebi.eva.pipeline.jobs.steps.DropSingleStudyVariantsStep;
import uk.ac.ebi.eva.pipeline.parameters.validation.job.AggregatedVcfJobParametersValidator;

import static uk.ac.ebi.eva.pipeline.configuration.BeanNames.DROP_SINGLE_STUDY_VARIANTS_STEP;
import static uk.ac.ebi.eva.pipeline.configuration.BeanNames.DROP_STUDY_JOB;

/**
 * Job that removes a study from the database. Given a study to remove:
 * <p>
 * remove single study variants --> pull study entries in the rest of variants --> remove file entry in files collection
 */
@Configuration
@EnableBatchProcessing
@Import({DropSingleStudyVariantsStep.class})
public class DropStudyJob {

    private static final Logger logger = LoggerFactory.getLogger(DropStudyJob.class);

    @Autowired
    @Qualifier(DROP_SINGLE_STUDY_VARIANTS_STEP)
    private Step dropSingleStudyVariantsStep;

    @Bean(DROP_STUDY_JOB)
    @Scope("prototype")
    public Job dropStudyJob(JobBuilderFactory jobBuilderFactory) {
        logger.debug("Building '" + DROP_STUDY_JOB + "'");

        JobBuilder jobBuilder = jobBuilderFactory
                .get(DROP_STUDY_JOB)
                .incrementer(new RunIdIncrementer())
                .validator(new AggregatedVcfJobParametersValidator());
        SimpleJobBuilder builder = jobBuilder
                .start(dropSingleStudyVariantsStep);

        return builder.build();
    }
}
