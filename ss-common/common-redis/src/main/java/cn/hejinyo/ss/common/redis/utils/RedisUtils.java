package cn.hejinyo.ss.common.redis.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis工具类
 *
 * @author : HejinYo
 * @date : 2017/8/19 17:31
 */
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static RedisTemplate<String, String> REDIS_TEMPLATE;
    private static ValueOperations<String, String> valueOperations;
    private static ListOperations<String, String> listOperations;
    private static HashOperations<String, String, String> hashOperations;
    private static SetOperations<String, String> setOperations;
    private static ZSetOperations<String, String> zSetOperations;


    /**
     * 静态注入
     */
    @PostConstruct
    public void init() {
        REDIS_TEMPLATE = redisTemplate;
        valueOperations = REDIS_TEMPLATE.opsForValue();
        listOperations = REDIS_TEMPLATE.opsForList();
        hashOperations = REDIS_TEMPLATE.opsForHash();
        setOperations = REDIS_TEMPLATE.opsForSet();
        zSetOperations = REDIS_TEMPLATE.opsForZSet();
    }

    /**
     * 默认过期时长，单位：秒
     */
    private final static long DEFAULT_EXPIRE = 60 * 60 * 24;

    /**
     * 不设置过期时长
     */
    private final static long NOT_EXPIRE = -1;
    /**
     * key不存在返回的过期時常
     */
    private final static long NO_KEY_EXPIRE = -2;

    /**
     * Object转成JSON数据
     */
    private static String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return JSON.toJSONString(object);
    }

    /**
     * json字符串转对象
     */
    private static <T> T fromJson(String value, Type clazz) {
        return value == null ? null : JSON.parseObject(value, clazz);
    }

    /**
     * DEL key [key ...]
     * 删除指定的key（一个或多个）
     */
    public static void delete(String key) {
        REDIS_TEMPLATE.delete(key);
    }

    /**
     * 清除所有前缀为key
     */
    public static void cleanKey(String key) {
        Set<String> keys = REDIS_TEMPLATE.keys(key);
        Optional.ofNullable(keys).filter(v -> v.size() > 0).map(k -> REDIS_TEMPLATE.delete(k));
    }

    /**
     * EXISTS key [key ...]
     * 查询一个key是否存在
     */
    public static Boolean exists(String key) {
        return REDIS_TEMPLATE.hasKey(key);
    }

    /**
     * PEXPIRE key milliseconds
     * 设置key的有效时间以毫秒为单位
     */
    public static Boolean pExpire(String key, long timeout) {
        return REDIS_TEMPLATE.expire(key, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * EXPIRE key seconds
     * 设置一个key的过期的秒数
     */
    public static Boolean expire(String key, long timeout) {
        return REDIS_TEMPLATE.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * EXPIREAT key timestamp
     * 设置一个UNIX时间戳的过期时间
     */
    public static Boolean expireAt(String key, Date date) {
        return REDIS_TEMPLATE.expireAt(key, date);
    }

    /**
     * KEYS pattern
     * 查找所有匹配给定的模式的键
     */
    public static Set<String> keys(String pattern) {
        return REDIS_TEMPLATE.keys(pattern);
    }

    /**
     * PERSIST key
     * 移除key的过期时间
     */
    public static Boolean expireAt(String key) {
        return REDIS_TEMPLATE.persist(key);
    }

    /**
     * TTL key
     * 获取key的有效时间（单位：秒）
     */
    public static Long ttl(String key) {
        return REDIS_TEMPLATE.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * PTTL key
     * 获取key的有效毫秒数
     */
    public static Long pttl(String key) {
        return REDIS_TEMPLATE.getExpire(key, TimeUnit.MILLISECONDS);
    }

    /**
     * RANDOMKEY
     * 返回一个随机的key
     */
    public static String randomKey() {
        return REDIS_TEMPLATE.randomKey();
    }

    /**
     * RENAME key newkey
     * 将一个key重命名
     */
    public static void rename(String oldKey, String newKey) {
        REDIS_TEMPLATE.rename(oldKey, newKey);
    }

    /**
     * RENAMENX key newkey
     * 重命名一个key,新的key必须是不存在的key
     */
    public static Boolean renameNX(String oldKey, String newKey) {
        return REDIS_TEMPLATE.renameIfAbsent(oldKey, newKey);
    }

    /**
     * TYPE key
     * 获取key的存储类型
     */
    public static DataType type(String key) {
        return REDIS_TEMPLATE.type(key);
    }

    /**
     * SET key value [EX seconds] [PX milliseconds] [NX|XX] 设置一个key的value值,有效期永久
     * EX seconds – 设置键key的过期时间，单位时秒
     * PX milliseconds – 设置键key的过期时间，单位时毫秒
     * NX – 只有键key不存在的时候才会设置key的值
     * XX – 只有键key存在的时候才会设置key的值
     */
    public static void set(String key, Object value) {
        valueOperations.set(key, toJson(value));
    }

    /**
     * SETNX key value 设置的一个关键的价值，只有当该键不存在
     */
    public static Boolean setNX(String key, Object value) {
        return valueOperations.setIfAbsent(key, toJson(value));
    }

    /**
     * SETEX key seconds value 设置key-value并设置过期时间（单位：秒）
     */
    public static void setEX(String key, Object value, long expire) {
        valueOperations.set(key, toJson(value), expire, TimeUnit.SECONDS);
    }

    /**
     * 设置 key value,不改变原来有效期
     */
    public static void setHold(String key, Object value) {
        // Key不在，超时返回-2
        Long expire = Optional.ofNullable(REDIS_TEMPLATE.getExpire(key)).filter(k -> k != NO_KEY_EXPIRE).orElse(DEFAULT_EXPIRE);
        valueOperations.set(key, toJson(value), expire, TimeUnit.SECONDS);
    }

    /**
     * 获取一个值
     */
    public static String get(String key) {
        return get(key, NOT_EXPIRE);
    }

    /**
     * 获取一个值，并设置有效时间
     */
    public static String get(String key, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            REDIS_TEMPLATE.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    /**
     * 获取一个值，并转换成指定对象
     */
    public static <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    /**
     * 获取一个值，并转换成指定对象，并设置有效时间
     */
    public static <T> T get(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            REDIS_TEMPLATE.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : JSON.parseObject(value, clazz);
    }

    /**
     * APPEND key value
     * 追加一个值到key上
     */
    public static Integer append(String key, Object value) {
        return valueOperations.append(key, toJson(value));
    }

    /**
     * DECR key 整数原子减1
     */
    public static Long decr(String key) {
        return valueOperations.increment(key, -1);
    }

    /**
     * DECR key 整数原子减指定整数
     */
    public static Long decrBy(String key, Long decrement) {
        return valueOperations.increment(key, -decrement);
    }

    /**
     * INCR key 执行原子加1操作
     */
    public static Long incr(String key) {
        return valueOperations.increment(key, 1);
    }

    /**
     * INCRBY key 整数原子加指定整数
     */
    public static Long incrBy(String key, Long decrement) {
        return valueOperations.increment(key, decrement);
    }

    /**
     * GETSET key value 设置一个key的value，并获取设置前的值
     */
    public static String getSet(String key, Object value) {
        return valueOperations.getAndSet(key, toJson(value));
    }

    /**
     * BLPOP key [key ...] timeout
     * 删除，并获得该列表中的第一元素，或阻塞，直到有一个可用
     */
    public static String bLPop(String key, long timeout) {
        return listOperations.leftPop(key, timeout, TimeUnit.SECONDS);
    }

    public static <T> T bLPop(String key, long timeout, Class<T> clazz) {
        return fromJson(bLPop(key, timeout), clazz);
    }

    /**
     * BRPOP key [key ...] timeout 删除，并获得该列表中的最后一个元素，或阻塞，直到有一个可用
     */
    public static String bRPop(String key, long timeout) {
        return listOperations.rightPop(key, timeout, TimeUnit.SECONDS);
    }

    public static <T> T bRPop(String key, long timeout, Class<T> clazz) {
        return fromJson(bRPop(key, timeout), clazz);
    }

    /**
     * BRPOPLPUSH source destination timeout 弹出一个列表的值，将它推到另一个列表，并返回它;或阻塞，直到有一个可用
     */
    public static String brpoplpush(String source, String destination, long timeout) {
        if (timeout == 0) {
            // timeout 为 0 能用于无限期阻塞客户端。
            return toJson(listOperations.rightPopAndLeftPush(source, destination));
        }
        return toJson(listOperations.rightPopAndLeftPush(source, destination, timeout, TimeUnit.SECONDS));
    }

    public static <T> T brpoplpush(String source, String destination, long timeout, Class<T> clazz) {
        return fromJson(brpoplpush(source, destination, timeout), clazz);
    }

    /**
     * RPOPLPUSH source destination 删除列表中的最后一个元素，将其追加到另一个列表
     */
    public static String rpoplpush(String sourceKey, String destinationKey) {
        return listOperations.rightPopAndLeftPush(sourceKey, destinationKey);
    }

    public static <T> T rpoplpush(String sourceKey, String destinationKey, Class<T> clazz) {
        return fromJson(rpoplpush(sourceKey, destinationKey), clazz);
    }

    /**
     * LINDEX key index 获取一个元素，通过其索引列表
     */
    public static String lindex(String key, long index) {
        return toJson(listOperations.index(key, index));
    }

    public static <T> T lindex(String key, long index, Class<T> clazz) {
        return fromJson(lindex(key, index), clazz);
    }

    /**
     * LLEN key 获得队列(List)的长度
     */
    public static Long llen(String key) {
        return listOperations.size(key);
    }

    /**
     * LPOP key 从队列的左边出队一个元素
     */
    public static String lpop(String key) {
        return listOperations.leftPop(key);
    }

    public static <T> T lpop(String key, Class<T> clazz) {
        return fromJson(lpop(key), clazz);
    }

    /**
     * LPUSH key value [value ...] 从队列的左边入队一个元素
     */
    public static Long lpush(String key, Object value) {
        return listOperations.leftPush(key, toJson(value));
    }

    /**
     * LPUSH key value [value ...] 从队列的左边入队多个元素
     */
    public static Long lpush(String key, String... values) {
        return listOperations.leftPushAll(key, values);
    }

    /**
     * LPUSH key value [value ...] 从队列的左边入队一个集合
     */
    public static Long lpush(String key, Collection<Object> values) {
        return listOperations.leftPushAll(key, values.stream().map(RedisUtils::toJson).collect(Collectors.toList()));
    }

    /**
     * LPUSHX key value 当队列存在时，从队到左边入队一个元素
     */
    public static Long lpushx(String key, Object value) {
        return listOperations.leftPushIfPresent(key, toJson(value));
    }

    /**
     * LRANGE key start stop 从列表中获取指定返回的元素
     * list的第一个元素下标是0（list的表头），第二个元素下标是1，以此类推。
     * 偏移量也可以是负数，表示偏移量是从list尾部开始计数。 例如， -1 表示列表的最后一个元素，-2 是倒数第二个，以此类推。
     */
    public static List<String> lrange(String key, long start, long end) {
        return listOperations.range(key, start, end);
    }

    public static <T> List<T> lrange(String key, long start, long end, Class<T> clazz) {
        List<String> value = lrange(key, start, end);
        return value == null ? null : JSON.parseObject(String.valueOf(value), new TypeReference<List<T>>() {
        }.getType());
        //return value == null ? null : value.stream().map(o -> (T) JsonUtil.fromJson(o, clazz)).collect(Collectors.toList());
    }

    /**
     * LREM key count value 从列表中删除元素
     * count > 0: 从头往尾移除值为 value 的元素。
     * count < 0: 从尾往头移除值为 value 的元素。
     * count = 0: 移除所有值为 value 的元素。
     */
    public static Long lrem(String key, long count, Object value) {
        return listOperations.remove(key, count, toJson(value));
    }

    /**
     * LSET key index value 设置队列里面一个元素的值
     */
    public static void lset(String key, long index, Object value) {
        listOperations.set(key, index, toJson(value));
    }

    /**
     * LTRIM key start stop 修剪到指定范围内的清单
     * 如果 start 超过列表尾部，或者 start > end，结果会是列表变成空表（即该 key 会被移除）。
     * 如果 end 超过列表尾部，Redis 会将其当作列表的最后一个元素。
     */
    public static void ltrim(String key, long start, long end) {
        listOperations.trim(key, start, end);
    }

    /**
     * RPOP key 从队列的右边出队一个元素
     */
    public static String rpop(String key) {
        return listOperations.rightPop(key);
    }

    public static <T> T rpop(String key, Class<T> clazz) {
        return fromJson(rpop(key), clazz);
    }

    /**
     * RPUSH key value [value ...] 从队列的右边入队一个元素
     */
    public static Long rpush(String key, String value) {
        return listOperations.rightPush(key, value);
    }

    /**
     * RPUSH key value [value ...] 从队列的右边入队多個元素
     */
    public static Long rpush(String key, String... values) {
        return listOperations.rightPushAll(key, values);
    }

    /**
     * RPUSH key value [value ...] 从队列的右边入队一个集合
     */
    public static Long rpush(String key, Collection<String> values) {
        return listOperations.rightPushAll(key, values);
    }

    /**
     * LPUSHX key value 当队列存在时，从队到左边入队一个元素
     */
    public static Long rpushx(String key, String value) {
        return listOperations.rightPushIfPresent(key, value);
    }

    /**
     * http://redis.cn/commands.html#hash
     * HSTRLEN key field 获取hash里面指定field的长度
     * HSCAN key cursor [MATCH pattern] [COUNT count] 迭代hash里面的元素
     */

    /**
     * HDEL key field [field ...] 删除一个或多个Hash的field
     */
    public static Long hdel(String key, String field) {
        return hashOperations.delete(key, field);
    }

    /**
     * HEXISTS key field 判断field是否存在于hash中
     */
    public static Boolean hexists(String key, String field) {
        return hashOperations.hasKey(key, field);
    }

    /**
     * HGET key field 获取hash中field的值
     */
    public static String hget(String key, String field) {
        return hashOperations.get(key, field);
    }

    public static <T> T hget(String key, String field, Class<T> clazz) {
        return fromJson(hashOperations.get(key, field), clazz);
    }

    public static <T> T hget(String key, String field, Type typeOfT) {
        return fromJson(hashOperations.get(key, field), typeOfT);
    }

    /**
     * HGETALL key 从hash中读取全部的域和值
     */
    public static Map<String, String> hgetall(String key) {
        return hashOperations.entries(key);
    }

    /**
     * HINCRBY key field increment 将hash中指定域的值增加给定的数字
     */
    public static Long hincrby(String key, String field) {
        return hashOperations.increment(key, field, 1L);
    }

    /**
     * HINCRBYFLOAT key field increment 将hash中指定域的值增加给定的浮点数
     */
    public static Long hincrbyfloat(String key, String field) {
        return hashOperations.increment(key, field, 1L);
    }

    /**
     * HKEYS key 获取hash的所有字段
     */
    public static Set<String> hkeys(String key) {
        return hashOperations.keys(key);
    }

    /**
     * HLEN key 获取hash里所有字段的数量
     */
    public static Long hlen(String key) {
        return hashOperations.size(key);
    }

    /**
     * HMGET key field [field ...] 获取hash里面指定字段的值
     */
    public static List<String> hmget(String key, Collection<String> hashKeys) {
        return hashOperations.multiGet(key, hashKeys);
    }

    public static List<String> hmget(String key, String... hashKeys) {
        return hashOperations.multiGet(key, Arrays.asList(hashKeys));
    }

    /**
     * HMSET key field value [field value ...] 设置hash字段值
     */
    public static void hmset(String key, Map<String, String> m) {
        hashOperations.putAll(key, m);
    }

    /**
     * HSET key field value 设置hash里面一个字段的值
     */
    public static void hset(String key, String hashKey, Object value) {
        hashOperations.put(key, hashKey, toJson(value));
    }

    /**
     * HSET key field value 设置hash里面一个字段的值,并设置有效时间
     */
    public static void hsetEX(String key, String hashKey, Object value, long expire) {
        hashOperations.put(key, hashKey, toJson(value));
        if (expire != NOT_EXPIRE) {
            REDIS_TEMPLATE.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    /**
     * HSETNX key field value 设置hash的一个字段，只有当这个字段不存在时有效
     */
    public static Boolean hsetNX(String key, String hashKey, Object value) {
        return hashOperations.putIfAbsent(key, hashKey, toJson(value));
    }

    /**
     * HVALS key  获得hash的所有值
     */
    public static List<String> hvals(String key) {
        return hashOperations.values(key);
    }

    /*
     *http://redis.cn/commands.html#set
     * SSCAN key cursor [MATCH pattern] [COUNT count]
     * 迭代set里面的元素
     */

    /**
     * SADD key member [member ...]
     * 添加一个或者多个元素到集合(set)里
     **/
    public static Long sadd(String key, String... values) {
        return setOperations.add(key, values);
    }

    /**
     * SCARD key
     * 获取集合里面的元素数量
     */
    public static Long scard(String key) {
        return setOperations.size(key);
    }

    /**
     * SDIFF key [key ...]
     * 获得队列不存在的元素
     **/
    public static Set<String> sdiff(String key, String otherKey) {
        return setOperations.difference(key, otherKey);
    }

    /**
     * SDIFFSTORE destination key [key ...]
     * 获得队列不存在的元素，并存储在一个关键的结果集
     **/
    public static Long sdiffstore(String key, String otherKey, String destKey) {
        return setOperations.differenceAndStore(key, otherKey, destKey);
    }

    /**
     * SINTER key [key ...]
     * 获得两个集合的交集
     **/
    public static Set<String> sinter(String key, String otherKey) {
        return setOperations.intersect(key, otherKey);
    }

    /**
     * SINTERSTORE destination key [key ...]
     * 获得两个集合的交集，并存储在一个关键的结果集
     **/
    public static Long sinterstore(String key, String otherKey, String destKey) {
        return setOperations.intersectAndStore(key, otherKey, destKey);
    }

    /**
     * SISMEMBER key member
     * 确定一个给定的值是一个集合的成员
     **/
    public static Boolean sismember(String key, Object o) {
        return setOperations.isMember(key, toJson(o));
    }

    /**
     * SMEMBERS key
     * 获取集合里面的所有元素
     **/
    public static Set<String> smembers(String key) {
        return setOperations.members(key);
    }

    /**
     * SMOVE source destination member
     * 移动集合里面的一个元素到另一个集合
     **/
    public static Boolean smove(String key, String value, String destKey) {
        return setOperations.move(key, value, destKey);
    }

    /**
     * SPOP key [count]
     * 删除并获取一个集合里面的元素
     **/
    public static String spop(String key) {
        return setOperations.pop(key);
    }

    /**
     * SRANDMEMBER key
     * 从集合里面随机获取一个元素
     **/
    public static String srandmember(String key) {
        return setOperations.randomMember(key);
    }

    /**
     * SRANDMEMBER key [count]
     * 从集合里面随机获取多个元素
     **/
    public static List<String> srandmember(String key, long count) {
        return setOperations.randomMembers(key, count);
    }

    /**
     * SREM key member [member ...]
     * 从集合里删除一个或多个元素
     **/
    public static Long srem(String key, Object... values) {
        return setOperations.remove(key, values);
    }

    /**
     * SUNION key [key ...]
     * 添加多个set元素
     **/
    public static Set<String> sunion(String key, String otherKey) {
        return setOperations.union(key, otherKey);
    }

    /**
     * SUNIONSTORE destination key [key ...]
     * 合并set元素，并将结果存入新的set里面
     **/
    public static Long sunionstore(String key, String otherKey, String destKey) {
        return setOperations.unionAndStore(key, otherKey, destKey);
    }

}
