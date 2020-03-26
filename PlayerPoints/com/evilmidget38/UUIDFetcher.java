/*    */ package com.evilmidget38;
/*    */ 
/*    */ import com.google.common.collect.ImmutableList;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.OutputStream;
/*    */ import java.net.HttpURLConnection;
/*    */ import java.net.URL;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.Arrays;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.UUID;
/*    */ import java.util.concurrent.Callable;
/*    */ import org.json.simple.JSONArray;
/*    */ import org.json.simple.JSONObject;
/*    */ import org.json.simple.parser.JSONParser;
/*    */ 
/*    */ public class UUIDFetcher
/*    */   implements Callable<Map<String, UUID>>
/*    */ {
/*    */   private static final double PROFILES_PER_REQUEST = 100.0D;
/*    */   private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
/* 24 */   private final JSONParser jsonParser = new JSONParser();
/*    */   private final List<String> names;
/*    */   private final boolean rateLimiting;
/*    */   
/*    */   public UUIDFetcher(List<String> names, boolean rateLimiting) {
/* 29 */     this.names = ImmutableList.copyOf(names);
/* 30 */     this.rateLimiting = rateLimiting;
/*    */   }
/*    */   
/*    */   public UUIDFetcher(List<String> names) {
/* 34 */     this(names, true);
/*    */   }
/*    */   
/*    */   public Map<String, UUID> call() throws Exception {
/* 38 */     Map<String, UUID> uuidMap = new HashMap();
/* 39 */     int requests = (int)Math.ceil(this.names.size() / 100.0D);
/* 40 */     for (int i = 0; i < requests; i++) {
/* 41 */       HttpURLConnection connection = createConnection();
/* 42 */       String body = JSONArray.toJSONString(this.names.subList(i * 100, Math.min((i + 1) * 100, this.names.size())));
/* 43 */       writeBody(connection, body);
/* 44 */       JSONArray array = (JSONArray)this.jsonParser.parse(new InputStreamReader(connection.getInputStream()));
/* 45 */       for (Object profile : array) {
/* 46 */         JSONObject jsonProfile = (JSONObject)profile;
/* 47 */         String id = (String)jsonProfile.get("id");
/* 48 */         String name = (String)jsonProfile.get("name");
/* 49 */         UUID uuid = getUUID(id);
/* 50 */         uuidMap.put(name, uuid);
/*    */       }
/* 52 */       if ((this.rateLimiting) && (i != requests - 1)) {
/* 53 */         Thread.sleep(100L);
/*    */       }
/*    */     }
/* 56 */     return uuidMap;
/*    */   }
/*    */   
/*    */   private static void writeBody(HttpURLConnection connection, String body) throws Exception {
/* 60 */     OutputStream stream = connection.getOutputStream();
/* 61 */     stream.write(body.getBytes());
/* 62 */     stream.flush();
/* 63 */     stream.close();
/*    */   }
/*    */   
/*    */   private static HttpURLConnection createConnection() throws Exception {
/* 67 */     URL url = new URL("https://api.mojang.com/profiles/minecraft");
/* 68 */     HttpURLConnection connection = (HttpURLConnection)url.openConnection();
/* 69 */     connection.setRequestMethod("POST");
/* 70 */     connection.setRequestProperty("Content-Type", "application/json");
/* 71 */     connection.setUseCaches(false);
/* 72 */     connection.setDoInput(true);
/* 73 */     connection.setDoOutput(true);
/* 74 */     return connection;
/*    */   }
/*    */   
/*    */   private static UUID getUUID(String id) {
/* 78 */     return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
/*    */   }
/*    */   
/*    */   public static byte[] toBytes(UUID uuid) {
/* 82 */     ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
/* 83 */     byteBuffer.putLong(uuid.getMostSignificantBits());
/* 84 */     byteBuffer.putLong(uuid.getLeastSignificantBits());
/* 85 */     return byteBuffer.array();
/*    */   }
/*    */   
/*    */   public static UUID fromBytes(byte[] array) {
/* 89 */     if (array.length != 16) {
/* 90 */       throw new IllegalArgumentException("Illegal byte array length: " + array.length);
/*    */     }
/* 92 */     ByteBuffer byteBuffer = ByteBuffer.wrap(array);
/* 93 */     long mostSignificant = byteBuffer.getLong();
/* 94 */     long leastSignificant = byteBuffer.getLong();
/* 95 */     return new UUID(mostSignificant, leastSignificant);
/*    */   }
/*    */   
/*    */   public static UUID getUUIDOf(String name) throws Exception {
/* 99 */     return (UUID)new UUIDFetcher(Arrays.asList(new String[] { name })).call().get(name);
/*    */   }
/*    */ }


/* Location:              G:\Minecraft Program\MinecraftServer\jd-gui-windows-1.4.0\PlayerPoints.jar!\com\evilmidget38\UUIDFetcher.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */