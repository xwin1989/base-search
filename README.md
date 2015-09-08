# Easy generate hql Statement from alone entity

You can generate hql by QueryGroup 

#Simple
```java
new QueryGroup("a", 1).and("b", 2) 
==> (a = :a0 AND b = :b1)
```
```java
new QueryGroup(new QueryNode("a", 1)).and(new QueryNode("b", 2)).and("c", 3)
==>(c = :c2 AND (a = :a0 AND b = :b1)))
```

#Multi sample parameters generate
```java
new QueryGroup("a", 30).and("b", 10).or("a", 20)  ==>  (a = :a2 OR (a = :a0 AND b = :b1)))
```

#You can get hql and parameters with SimpleQuery
```java
QueryGroup group = new QueryGroup("a", 30).and("b", 10).or("a", 20);
QueryModel queryModel = parser.parse(group);
Assert.assertEquals(queryModel.getStatement(), "(a = :a2 OR (a = :a0 AND b = :b1)))");
Assert.assertTrue(queryModel.getParameters().size() == 3);
Assert.assertEquals(queryModel.getParameters().get("a0"), 30);
Assert.assertEquals(queryModel.getParameters().get("b1"), 10);
Assert.assertEquals(queryModel.getParameters().get("a2"), 20);
```
#Json transaction
```java
QueryGroup group = new QueryGroup("name", "%n%", QueryOperate.LIKE)
                .and("type", 1).and("status", "type", QueryOperate.COLUMN_EQUALS)
                .and("password", "p1").and("id", ids, QueryOperate.NOT_IN).or("id", 0, QueryOperate.IN);
String json = QueryJSONBinder.toJSON(new QueryRequest(group));
QueryResponse<Person> response = personService.search(QueryJSONBinder.fromJSON(json));
```

#Sort
```java
QueryGroup group = new QueryGroup().sort(new Sort("a", "b", "c"));
String orderStatement = queryParser.parse(group).getOrderStatement();
Assert.assertEquals(orderStatement, "a ASC,b ASC,c ASC");
```
```java
List<Sort.Order> orders = new ArrayList<>();
orders.add(new Sort.Order(Direction.ASC, "a"));
orders.add(new Sort.Order(Direction.DESC, "b"));
Assert.assertEquals(queryParser.parse(new QueryGroup().sort(new Sort(orders))).getOrderStatement(),
        "a ASC,b DESC");
```

#Search
```java
QueryRequest request = new QueryRequest(0, 2);
QueryGroup queryGroup = new QueryGroup("name", "book", QueryOperate.CONTAIN).and("status", 0, QueryOperate.GREAT_THAN)
        .sort(new Sort(Direction.DESC, "type"));
request.setQueryGroup(queryGroup);
QueryResponse<Book> response = bookService.search(request);
Assert.assertTrue(response.getRecords().size() == 2);
Assert.assertTrue(response.getRecords().get(0).getId() == 3);
Assert.assertTrue(response.getTotal() == 3);
```

#More example please see unit test