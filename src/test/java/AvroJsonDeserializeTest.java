import com.example.User;
import com.example.item_conditions;
import com.google.common.io.Resources;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.junit.Assert.assertEquals;

public class AvroJsonDeserializeTest {

    private static Schema schema;
    private User user;
    private String bob;
    private Long bob_user_id;

    @Before
    public void setUp() throws Exception {
        schema = User.getClassSchema();

        final User.Builder builder = User.newBuilder();
        bob_user_id = 100L;
        builder.setUserId(bob_user_id);
        bob = "bob";
        builder.setName(bob);
        builder.setItemCondition(item_conditions.open_box);
        user = builder.build();
    }

    @Test
    public void createJsonWithFix() throws Exception {
        GenericDatumWriter<GenericRecord> gdw = new GenericDatumWriter<>(schema);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().jsonEncoder(schema, baos);
        gdw.write(user, encoder);
        encoder.flush();

        final String s = baos.toString();
        System.out.println("s = " + s);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Decoder decoder = DecoderFactory.get().jsonDecoder(schema, bais);

        ReflectDatumReader<User> rdr = new ReflectDatumReader<>(User.class);
        User joeClone = rdr.read(null, decoder);
        assertEquals(bob, joeClone.getName());
        assertEquals(bob_user_id, joeClone.getUserId());
    }


    @Test(expected = AvroTypeException.class)
    public void decodeBuggyDataWithAvro() throws Exception {
        final URL resource = Resources.getResource("test.buggy.users.jsonroots");
        assertThat(resource).isNotNull();
        try (InputStream inputStream = resource.openStream()) {
            Decoder decoder = DecoderFactory.get().jsonDecoder(schema, inputStream);
            ReflectDatumReader<User> rdr = new ReflectDatumReader<>(User.class);
            User joeClone = rdr.read(null, decoder);
            assertEquals(bob, joeClone.getName());
        }
    }


}
