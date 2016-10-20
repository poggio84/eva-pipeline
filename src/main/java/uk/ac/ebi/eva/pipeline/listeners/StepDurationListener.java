package uk.ac.ebi.eva.pipeline.listeners;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * Created by diego on 20/10/2016.
 */
public class StepDurationListener implements StepExecutionListener {
    private static final Logger logger = LoggerFactory.getLogger(StepDurationListener.class);

    private StopWatch stopWatch;
    private String stepName;

    public StepDurationListener(String stepName) {
        this.stepName = stepName;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        stopWatch = new StopWatch();
        stopWatch.start("Processing step " + stepName);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stopWatch.stop();

        long duration = stopWatch.getLastTaskTimeMillis();

        logger.info(String.format("Job %s took: %d minutes, %d seconds.",
                stepName,
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        ));
        return ExitStatus.UNKNOWN;
    }
}
