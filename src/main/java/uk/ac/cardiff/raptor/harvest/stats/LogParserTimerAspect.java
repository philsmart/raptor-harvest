package uk.ac.cardiff.raptor.harvest.stats;

import java.time.Duration;
import java.time.Instant;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogParserTimerAspect {

	private static final Logger log = LoggerFactory.getLogger(LogParserTimerAspect.class);

	// TODO just around the parse method, not all methods.
	@Around("execution(* uk.ac.cardiff.raptor.harvest.parse.LogParser.*(..)) || execution(* uk.ac.cardiff.raptor.harvest.comms.EventPush.*(..))")
	public Object timeParse(final ProceedingJoinPoint pjp) throws Throwable {

		final Instant starts = Instant.now();
		final Object toReturn = pjp.proceed();
		final Instant ends = Instant.now();
		log.info("Process [{}] took {}", pjp.getTarget().getClass(), Duration.between(starts, ends));
		return toReturn;

	}

}
