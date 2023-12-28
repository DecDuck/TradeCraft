package com.decduck3.tradecraft.web.data;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdSerializer extends TypeAdapter<ObjectId> {
    @Override
    public void write(JsonWriter jsonWriter, ObjectId objectId) throws IOException {
        if(objectId != null){
            jsonWriter.value(objectId.toString());
        }else{
            jsonWriter.value("");
        }
    }

    @Override
    public ObjectId read(JsonReader jsonReader) throws IOException {
        return new ObjectId(jsonReader.nextString());
    }
}
