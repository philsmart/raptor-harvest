package uk.ac.cardiff.raptor.harvest.batch;

import java.util.List;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.PushPipeline;

public class CallbackTestPushPipeline implements PushPipeline {

	private final EventPipelineCallback callback;

	public CallbackTestPushPipeline(final EventPipelineCallback callback) {
		this.callback = callback;
	}

	@Override
	public void pushPipeline(final List<Event> events) {
		callback.giveBack(events);
	}

}
