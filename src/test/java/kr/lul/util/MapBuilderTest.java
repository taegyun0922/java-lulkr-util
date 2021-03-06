package kr.lul.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

public class MapBuilderTest {
  class Key {
  }

  class Value {
  }

  @Test
  public void testHashmap() throws Exception {
    MapBuilder<String, BigDecimal> builder = MapBuilder.<String, BigDecimal>hashmap();
    Map<String, BigDecimal> map = builder.build();

    assertThat(builder).isNotNull();
    assertThat(map).isNotNull()
        .isInstanceOf(HashMap.class)
        .isEmpty();
  }

  @Test
  public void testHashmapWithEntry() throws Exception {
    // Given
    String key = RandomStringUtils.random(5);
    BigDecimal value = BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(Long.MAX_VALUE));

    // When
    MapBuilder<String, BigDecimal> builder = MapBuilder.<String, BigDecimal>hashmap(key, value);
    Map<String, BigDecimal> map = builder.build();

    // Then
    assertThat(builder).isNotNull();
    assertThat(map).isNotNull()
        .isInstanceOf(HashMap.class)
        .containsEntry(key, value)
        .hasSize(1);
    assertThatThrownBy(() -> builder.put(RandomStringUtils.random(10), BigDecimal.TEN))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  public void testLinkedhashmap() throws Exception {
    // When
    MapBuilder<Key, Value> builder = MapBuilder.<Key, Value>linkedhash();
    Map<Key, Value> map = builder.build();

    // Then
    assertThat(builder).isNotNull();
    assertThat(map).isNotNull().isInstanceOf(LinkedHashMap.class).isEmpty();
  }

  @Test
  public void testLinkedhashmapWithEntry() throws Exception {
    // Given
    Key k1 = new Key();
    Value v1 = new Value();
    Key k2 = new Key();
    Value v2 = new Value();

    // When
    MapBuilder<Key, Value> builder = MapBuilder.<Key, Value>linkedhash(k1, v1).put(k2, v2);
    Map<Key, Value> map = builder.build();

    // Then
    assertThat(builder).isNotNull();
    assertThat(map).isNotNull()
        .isInstanceOf(LinkedHashMap.class)
        .containsEntry(k1, v1)
        .containsEntry(k2, v2)
        .hasSize(2);

    Iterator<Entry<Key, Value>> iterator = map.entrySet().iterator();
    assertThat(iterator.next())
        .hasFieldOrPropertyWithValue("key", k1)
        .hasFieldOrPropertyWithValue("value", v1);
    assertThat(iterator.next())
        .hasFieldOrPropertyWithValue("key", k2)
        .hasFieldOrPropertyWithValue("value", v2);
    assertThat(iterator.hasNext()).isEqualTo(false);

    assertThatThrownBy(() -> builder.put(new Key(), new Value())).isInstanceOf(IllegalStateException.class);
    assertThatThrownBy(() -> builder.build()).isInstanceOf(IllegalStateException.class);
  }

  @Test
  public void testBuilderWithNull() throws Exception {
    assertThatThrownBy(() -> MapBuilder.<Key, Value>builder(null)).isInstanceOf(AssertionException.class);
  }

  @Test
  public void testBuilder() throws Exception {
    // Given
    final Map<Key, Value> map = new HashMap<>();

    // When
    MapBuilder<Key, Value> builder = MapBuilder.builder(map);

    // Then
    assertThat(builder.build()).isSameAs(map);
    assertThatThrownBy(() -> builder.build()).isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("expired");
  }

  @Test
  public void testBuilderWithElement() throws Exception {
    // Given
    final Map<Key, Value> expected = new HashMap<>();
    final Key k1 = new Key();
    final Value v1 = new Value();
    final Key k2 = new Key();
    final Value v2 = new Value();
    expected.put(k1, v1);

    // When
    Map<Key, Value> actual = MapBuilder.builder(expected, k2, v2).build();

    // Then
    assertThat(actual).isNotNull()
        .isSameAs(expected)
        .containsEntry(k1, v1)
        .containsEntry(k2, v2);
  }
}
