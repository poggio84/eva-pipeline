/*
 * Copyright 2016 EMBL - European Bioinformatics Institute
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
package embl.ebi.variation.eva.pipeline.steps;

import com.mongodb.*;
import com.mongodb.util.JSON;
import embl.ebi.variation.eva.VariantJobsArgs;
import embl.ebi.variation.eva.pipeline.annotation.generateInput.VariantAnnotationItemProcessor;
import embl.ebi.variation.eva.pipeline.annotation.generateInput.VariantWrapper;
import embl.ebi.variation.eva.pipeline.jobs.AnnotationConfig;
import embl.ebi.variation.eva.pipeline.jobs.VariantAnnotConfiguration;
import embl.ebi.variation.eva.pipeline.jobs.VariantAnnotConfigurationTest;
import embl.ebi.variation.eva.pipeline.jobs.VariantStatsConfigurationTest;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencb.opencga.storage.core.variant.VariantStorageManager;
import org.opencb.opencga.storage.mongodb.variant.DBObjectToVariantConverter;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.net.URL;
import java.util.Collections;

import static embl.ebi.variation.eva.pipeline.jobs.JobTestUtils.restoreMongoDbFromDump;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * @author Diego Poggioli
 *
 * Test {@link VariantsAnnotGenerateInput}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { VariantAnnotConfiguration.class, AnnotationConfig.class, JobLauncherTestUtils.class})
public class VariantsAnnotGenerateInputTest {

    @Autowired private MongoItemReader<DBObject> mongoItemReader;
    @Autowired private FlatFileItemWriter<VariantWrapper> writer;
    @Autowired private VariantJobsArgs variantJobsArgs;
    @Autowired private JobLauncherTestUtils jobLauncherTestUtils;

    private MongoClient mongoClient;
    private String dbName;
    private String collectionName;
    private String variantWithAnnotation;
    private String variantWithoutAnnotation;
    private ExecutionContext executionContext;

    @Before
    public void setUp() throws Exception {
        mongoClient = new MongoClient();

        executionContext = MetaDataInstanceFactory.createStepExecution().getExecutionContext();

        URL variantWithNoAnnotationUrl =
                VariantAnnotConfigurationTest.class.getResource("/annotation/VariantWithOutAnnotation");
        variantWithoutAnnotation = FileUtils.readFileToString(new File(variantWithNoAnnotationUrl.getFile()));

        URL variantWithAnnotationUrl =
                VariantAnnotConfigurationTest.class.getResource("/annotation/VariantWithAnnotation");
        variantWithAnnotation = FileUtils.readFileToString(new File(variantWithAnnotationUrl.getFile()));

        variantJobsArgs.loadArgs();
        dbName = variantJobsArgs.getPipelineOptions().getString("db.name");
        collectionName = variantJobsArgs.getPipelineOptions().getString("db.collections.variants.name");

        collection().drop();
    }

    @Test
    public void variantsAnnotGenerateInputStepShouldGenerateVepInput() throws Exception {
        String dump = VariantStatsConfigurationTest.class.getResource("/dump/").getFile();
        restoreMongoDbFromDump(dump);
        File vepInputFile = new File(variantJobsArgs.getPipelineOptions().getString("vep.input"));

        if(vepInputFile.exists())
            vepInputFile.delete();

        Assert.assertFalse(vepInputFile.exists());

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("Find variants to annotate");

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

        assertTrue(vepInputFile.exists());
        assertEquals("20\t61118\t61118\tA/G\t+", readFirstLine(vepInputFile));
    }

    @Test
    public void variantReaderShouldReadVariantsWithoutAnnotationField() throws Exception {
        insertDocuments();
        mongoItemReader.open(executionContext);

        int itemCount = 0;
        DBObject doc;
        while((doc = mongoItemReader.read()) != null) {
            itemCount++;
            assertTrue(doc.containsField("chr"));
            assertTrue(doc.containsField("start"));
            assertFalse(doc.containsField("annot"));
        }
        assertEquals(itemCount, 1);
        mongoItemReader.close();
    }

    @Test
    public void vepInputLineProcessorShouldConvertAllFieldsInVariant() throws Exception {
        DBObject dbo = constructDbo(variantWithoutAnnotation);

        ItemProcessor<DBObject, VariantWrapper> processor =  new VariantAnnotationItemProcessor();
        VariantWrapper variant = processor.process(dbo);
        assertEquals("+", variant.getStrand());
        assertEquals("20", variant.getChr());
        assertEquals("G/A", variant.getRefAlt());
        assertEquals(60343, variant.getEnd());
        assertEquals(60343, variant.getStart());
    }

    @Test
    public void vepInputWriterShouldWriteAllFieldsToFile() throws Exception {
        DBObjectToVariantConverter converter = new DBObjectToVariantConverter();
        File outputFile = new File(variantJobsArgs.getPipelineOptions().getString("vep.input"));
        VariantWrapper variant = new VariantWrapper(converter.convertToDataModelType(constructDbo(variantWithAnnotation)));

        writer.open(executionContext);
        writer.write(Collections.singletonList(variant));
        assertEquals("20\t60344\t60348\tG/A\t+", readFirstLine(outputFile));
        writer.close();
        outputFile.delete();
    }

    private void insertDocuments() throws IOException {
        collection().insert(constructDbo(variantWithAnnotation));
        collection().insert(constructDbo(variantWithoutAnnotation));
    }

    private DBCollection collection() {
        return mongoClient.getDB(dbName).getCollection(collectionName);
    }

    private DBObject constructDbo(String variant) {
        return (DBObject) JSON.parse(variant);
    }

    private String readFirstLine(File file) throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            return reader.readLine();
        }
    }

}