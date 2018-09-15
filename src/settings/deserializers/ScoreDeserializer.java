package settings.deserializers;

import com.google.gson.*;
import java.lang.reflect.Type;
import javafx.scene.paint.Color;
import settings.*;

public class ScoreDeserializer implements JsonDeserializer<Score>{

    @Override
    public Score deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        JsonObject jObject = (JsonObject)je;
        return new Score(
                Color.valueOf(jObject.get("color").getAsString()),
                jObject.get("name").getAsString(),
                            jObject.get("points").getAsInt(),
                            jObject.get("time").getAsInt());
    }
    
}
