package org.phoenix.planet.configuration.security;

// 간단 직렬화 유틸 (스프링 core의 org.springframework.util.SerializationUtils 써도 됨)
public class SerializationUtils {
    public static byte[] serialize(Object obj) {
        try (var bos = new java.io.ByteArrayOutputStream();
             var oos = new java.io.ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return bos.toByteArray();
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
    public static Object deserialize(byte[] bytes) {
        try (var bis = new java.io.ByteArrayInputStream(bytes);
             var ois = new java.io.ObjectInputStream(bis)) {
            return ois.readObject();
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
}