package com.aventuracctv.ordermangement.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Deserializer
 *
 * Convert a JsonObject into an object.
 */
public class BuildItemDeserializer implements JsonDeserializer<ArrayList<BuildItem>>
{
    @Override
    public ArrayList<BuildItem> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException
    {

        final ArrayList<BuildItem> buildItems = new ArrayList<>();
        final JsonArray orders = json.getAsJsonObject().getAsJsonArray("orders");

        for (int i = 0; i < orders.size(); i++) {

            final BuildItem buildItem = new BuildItem();
            final JsonObject item = orders.get(i).getAsJsonObject();

            buildItem.setCustomerName(getNullAsEmptyString(item.get("customer_name")));
            buildItem.setBuildType(getNullAsEmptyString(item.get("build_type")));
            buildItem.setpdfPath(getNullAsEmptyString(item.get("pdf_path")));
            buildItem.setProgress(item.get("status").getAsInt());
            buildItem.setAssignedTo(getNullAsEmptyString(item.get("assigned_to")));

            if (buildItem.assignedTo() == null || buildItem.assignedTo().equals("")) {
                buildItem.setAssigned(false);
                buildItem.setAssignedTo("NONE");
            } else {
                buildItem.setAssigned(true);
            }
            buildItems.add(buildItem);
        }

        return buildItems;
    }

    private String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}