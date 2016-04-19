import com.example.User;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ProcesssTest {
    private static final Schema schema = User.getClassSchema();

    @Test
    public void testFile() throws Exception {
        final URL resource = Resources.getResource("test.users.jsonroots");
        assertThat(resource).isNotNull();
        try (InputStream inputStream = resource.openStream()) {
            final ArrayList<User> events = read(inputStream);
            System.out.println("read = " + events);
        }
    }

    private static ArrayList<User> read(InputStream input) throws IOException {
        final ArrayList<User> events = Lists.newArrayList();

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            final Stream<String> lines = buffer.lines();
            lines.forEach(line -> {
                try {
                    Decoder decoder = DecoderFactory.get().jsonDecoder(schema, line);
                    final GenericDatumReader<User> eventGenericDatumReader = new GenericDatumReader<>(schema);
                    final User event = eventGenericDatumReader.read(null, decoder);
                    events.add(event);
                    System.out.println("event = " + event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


//            return lines.collect(Collectors.joining("\n"));
            return events;
        }
    }
}