import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.here.owc.EmrJobMessageCollectorHandler;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;

class EmrJobMessageCollectorHandlerTest {

    @Ignore
    @Test
    void handleRequest() throws IOException {

        Context mockContext = Mockito.mock(Context.class);
        EmrJobMessageCollectorHandler emrJobRegistrationHandler = new EmrJobMessageCollectorHandler();
        ScheduledEvent scheduledEvent = new ScheduledEvent();
        HashMap<String, Object> detail = new HashMap<>();
        detail.put("clusterId", "j-3H4JRMMVK9UDY");
        detail.put("state", "adasdsdm");
        scheduledEvent.setDetail(detail);
        emrJobRegistrationHandler.handleRequest(scheduledEvent, mockContext);
    }
}
