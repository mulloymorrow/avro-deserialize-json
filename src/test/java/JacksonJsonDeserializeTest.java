import com.example.User;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import io.dropwizard.jackson.Jackson;
import org.apache.avro.Schema;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class JacksonJsonDeserializeTest {

    private static Schema schema;
    private User user;
    private String bob;
    private Long bob_user_id;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        schema = User.getClassSchema();

        final User.Builder builder = User.newBuilder();
        bob_user_id = 100L;
        builder.setUserId(bob_user_id);
        bob = "bob";
        builder.setName(bob);
        user = builder.build();

        objectMapper = Jackson.newObjectMapper();
    }



    @Test
    public void decodeBuggyDataWithJackson() throws Exception {
        final URL resource = Resources.getResource("test.buggy.users.jsonroots");
        assertThat(resource).isNotNull();
        final JsonFactory jsonFactory = new JsonFactory();

        final ArrayList<User> users = Lists.newArrayList();
        try (InputStream inputStream = resource.openStream();
             final JsonParser parser = jsonFactory.createParser(inputStream)) {
            final MappingIterator<User> userMappingIterator = objectMapper.readValues(parser, User.class);
            while(userMappingIterator.hasNext()){
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
