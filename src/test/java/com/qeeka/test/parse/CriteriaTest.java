package com.qeeka.test.parse;

import com.qeeka.domain.Sort;
import com.qeeka.enums.Direction;
import com.qeeka.query.Criteria;
import com.qeeka.query.Group;
import com.qeeka.query.Join;
import com.qeeka.query.Query;
import com.qeeka.test.domain.Book;
import com.qeeka.util.EntityHandle;
import com.qeeka.util.ReflectionUtil;
import org.junit.Test;

import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by neal.xu on 2019/11/18.
 */
public class CriteriaTest {
    @Test
    public void test1() {
        Criteria a = Criteria.where("a").eq(1);
        //Criteria.where("a").is(1).and("b").lt(1).gt(2).or(b).is(3).or(new Criteria(c).is(4).and(d).lt(5))
    }

    @Test
    public void test2() {
        Map<String, Object> params = new HashMap<>();
        params.put("t1", 1);
        params.put("t2", 2);
        params.put("t3", 3);

        Criteria criteria = Criteria.where("a").eq(1).and("b").gt(2).lt(3).or("c").eq(5).sub("in (:t1,:t2,t3)", params).and("d").nul().and("e").nNul()
                .or("f").like("%hello%").or("f").nLike("_xx%").and("g").ne("nn");

        Join join1 = Join.inner("A", "a").on(Criteria.where("a.id").eq("E.id"));
        Join join2 = Join.left("B", "b").on(Criteria.where("b.id").eq("a.id"));
        Join join3 = Join.leftOut("B", "b").on(Criteria.where("b.id").eq("a.id"));
        Join join4 = Join.right("B", "b").on(Criteria.where("b.id").eq("a.id"));
        Join join5 = Join.rightOut("B", "b").on(Criteria.where("b.id").eq("a.id"));
        Join join6 = Join.cross("B", "b").on(Criteria.where("b.id").eq("a.id"));

        Group group = Group.by("a.id", "b.id").having(Criteria.where("a").eq(12));
        Sort sort = Sort.by(Direction.DESC, "a.id").and(Sort.by(Direction.ASC_NULL, "b.id"));

        Query query = Query.query(criteria).selects("a.id,b.id,c.id")
                .join(join1).join(join2).join(join3)
                .join(join4).join(join5).join(join6)
                .group(group).offset(1).size(10).with(1, 10).with(sort).count().distinct();

    }

    @Test
    public void test3() {
        SerializedLambda resolve = ReflectionUtil.resolve(Book::getId);
        String column = EntityHandle.methodToColumn(Book.class, resolve.getImplMethodName());
        System.out.println(column);
    }

    @Test
    public void testColumn() {
        Book book = new Book();
        book.setAuthorId(1);

        Function<Integer, Integer> addTax = x -> x / 100 * (100 + 10);
        System.out.println(addTax.apply(100));

        Function<Book, Integer> getAuthorId = Book::getAuthorId;

        System.out.println(getAuthorId.getClass());
        Integer apply = getAuthorId.apply(book);
        System.out.println(apply);


        HashMap<Integer, String> map = new HashMap<>();
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");

        map.merge(4, "four", (v1, v2) -> v1 + v2);
        System.out.println(map);

        map.compute(3, (k, v) -> v == null ? "hello" : v.concat(String.valueOf(k)));
        System.out.println(map);

    }


    @Test
    public void test6() {
        Properties properties = System.getProperties();
        // not easy to sort this
        Set<Map.Entry<Object, Object>> entries = properties.entrySet();

        LinkedHashMap<String, String> collect = entries.stream()
                //Map<String, String>
                .collect(Collectors.toMap(k -> (String) k.getKey(), e -> (String) e.getValue()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        collect.forEach((k, v) -> System.out.println(k + ":" + v));

        Map<String, String> collect1 = entries.stream().collect(Collectors.toMap(k -> String.valueOf(k), v -> String.valueOf(v)));

    }
}
