# Base Search FrameWork
Simplified query framework
 
#Simple
```java
new QueryGroup("a", "test1");
==> a = :a0
```
```java
new QueryGroup("a", "test1", NO_EQUALS)
==> a <> :a0
```
```java
new QueryGroup("a", 1, GREAT_THAN).and("b", 2, GREAT_THAN_EQUALS);
==> a > :a0 AND b >= :b1
```

#Multi sample parameters generate
```java
new QueryGroup("a", 4).and(new QueryGroup("b", 3).and("c", 1).and("d", 2))
==> (a = :a3 AND ((b = :b0 AND c = :c1) AND d = :d2))
```

#Sort
```java
new QueryGroup().sort(Direction.ASC, "a", "b", "c")
==> a ASC,b ASC,c ASC
```
```java
List<Sort.Order> orders = new ArrayList<>();
orders.add(new Sort.Order(Direction.ASC, "a"));
orders.add(new Sort.Order(Direction.DESC, "b"));
==> a ASC,b DESC
```

#Search
```java
UserEntity userEntity = userRepository.get(1);
```
```java
UserEntity user = new UserEntity();
user.setUserId(4);
user.setName("neal");
user.setRoleId(0);
user.setCreateTime(new Date());
userRepository.save(user);
```
```java
UserEntity user = new UserEntity();
user.setUserId(3);
userRepository.delete(user);
userRepository.deleteById(2);
```
```java
UserEntity userEntity = userRepository.get(1);
userEntity.setName("neal");
int update = userRepository.update(userEntity);
```

#Entity defined
```java
@Entity(table = "user")
public class UserEntity {
    @Id(strategy = GenerationType.AUTO)
    @Column("user_id")
    private Integer userId;
    private String name;
    @Column("role_id")
    private Integer roleId;
    @Column("create_time")
    private Date createTime;
    @Transient
    private Integer age;
    set...
    get...
}

@Entity defined table info for Entity
@Id defined primary key
@Column defined column name for Entity
@Transient skip this property
```
#More example please see unit test