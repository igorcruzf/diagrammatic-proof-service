package uff.br.tcc.helper

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import uff.br.tcc.model.term.ITerm
import java.lang.reflect.Type

fun buildGson(): Gson = GsonBuilder().registerTypeAdapter(ITerm::class.java, InterfaceAdapter<ITerm>())
    .create()

class InterfaceAdapter<T> : JsonSerializer<T>, JsonDeserializer<T> {
    override fun serialize(`object`: T, interfaceType: Type?, context: JsonSerializationContext): JsonElement {
        val wrapper = JsonObject()
        wrapper.addProperty("type", `object`!!::class.java.name)
        wrapper.add("data", context.serialize(`object`))
        return wrapper
    }

    @Throws(JsonParseException::class)
    override fun deserialize(elem: JsonElement, interfaceType: Type?, context: JsonDeserializationContext): T {
        val wrapper = elem as JsonObject
        val typeName = get(wrapper, "type")
        val data = get(wrapper, "data")
        val actualType: Type = typeForName(typeName)
        return context.deserialize(data, actualType)
    }

    private fun typeForName(typeElem: JsonElement): Type {
        return try {
            Class.forName(typeElem.asString)
        } catch (e: ClassNotFoundException) {
            throw JsonParseException(e)
        }
    }

    private operator fun get(wrapper: JsonObject, memberName: String): JsonElement {
        return wrapper[memberName]
            ?: throw JsonParseException("no '$memberName' member found in what was expected to be an interface wrapper")
    }
}
