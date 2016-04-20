import com.example.User;
import com.example.UserPrime;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class JacksonJsonDeserializeTest {
    private ObjectMapper objectMapper = Jackson.newObjectMapper();

    @Test
    public void decodeWithCondition() throws Exception {
        final URL resource = Resources.getResource("test.buggy.users.jsonroots");
        assertThat(resource).isNotNull();
        final JsonFactory jsonFactory = new JsonFactory();

        final ArrayList<User> users = Lists.newArrayList();
        try (InputStream inputStream = resource.openStream();
             final JsonParser parser = jsonFactory.createParser(inputStream)) {
            final MappingIterator<User> userMappingIterator = objectMapper.readValues(parser, User.class);
            while (userMappingIterator.hasNext()) {
                users.add(userMappingIterator.next());
            }
        }
        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(0).getName().toString()).isEqualTo("jupiter");
        assertThat(users.get(0).getUserId()).isNull();
        assertThat(users.get(1).getName().toString()).isEqualTo("zach");
        assertThat(users.get(1).getUserId()).isEqualTo(0L);
        assertThat(users.get(2).getName().toString()).isEqualTo("sarah");
        assertThat(users.get(2).getUserId()).isEqualTo(1L);
    }


    @Test
    public void decodeIgnoreTroubledField() throws Exception {
        final URL resource = Resources.getResource("test.buggy.users.jsonroots");
        assertThat(resource).isNotNull();
        final JsonFactory jsonFactory = new JsonFactory();

        final ArrayList<UserPrime> users = Lists.newArrayList();
        try (InputStream inputStream = resource.openStream();
             final JsonParser parser = jsonFactory.createParser(inputStream)) {
            final MappingIterator<UserPrime> userMappingIterator = objectMapper.readValues(parser, UserPrime.class);
            while (userMappingIterator.hasNext()) {
                users.add(userMappingIterator.next());
            }
        }
        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(0).getName().toString()).isEqualTo("jupiter");
        assertThat(users.get(0).getUserId()).isNull();
        assertThat(users.get(1).getName().toString()).isEqualTo("zach");
        assertThat(users.get(1).getUserId()).isEqualTo(0L);
        assertThat(users.get(2).getName().toString()).isEqualTo("sarah");
        assertThat(users.get(2).getUserId()).isEqualTo(1L);
    }
}
