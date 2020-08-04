package com.aventuracctv.ordermangement.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Deserializer
 *
 * Convert a JsonObject into an object.
 */
public class RegisterDeserializer implements JsonDeserializer<User>
{
    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {
        final User user = new User();
        final JsonObject jsonObject = json.getAsJsonObject();

        user.setmessage(jsonObject.get("message").getAsString());

        return user;
    }
}