#### redo和bin log日志在一条更新语句中的作用

当有一条记录需要更新的时候，InnoDB 引擎就会先把记录写到 redo log里面，并更新内存，这个时候更新就算完成了。同时，InnoDB 引擎会在适当的时候，将这个操作记录更新到磁盘里面，而这个更新往往是在系统比较空闲的时候做--WAL技术,先写日志,再写磁盘

redo log是innodb特有,且固定大小,循环写入,有了redo log,即使数据库重启,也不会丢失提交记录

bin log: 属于Serve层的日志(逻辑日志)

**两段式提交**让两份日志数据一致,由于redo log和bin log:是两份独立的日志,如果不使用两段式提交,会造成一些问题:

1 先写redo再写binlog:假设在 redo log 写完，binlog 还没有写完的时候，MySQL 进程异常重启。由于我们前面说过的，redo log 写完之后，系统即使崩溃，仍然能够把数据恢复回来，所以恢复后这一行 c 的值是 1。但是由于 binlog 没写完就 crash 了，这时候 binlog 里面就没有记录这个语句。因此，之后备份日志的时候，存起来的 binlog 里面就没有这条语句。然后你会发现，如果需要用这个 binlog 来恢复临时库的话，由于这个语句的 binlog 丢失，这个临时库就会少了这一次更新，恢复出来的这一行 c 的值就是 0，与原库的值不同。

2 先写 binlog 后写 redo log。如果在 binlog 写完之后 crash，由于 redo log 还没写，崩溃恢复以后这个事务无效，所以这一行 c 的值是 0。但是 binlog 里面已经记录了“把 c 从 0 改成 1”这个日志。所以，在之后用 binlog 来恢复的时候就多了一个事务出来，恢复出来的这一行 c 的值就是 1，与原库的值不同。

两段式:引擎将这行新数据更新到内存中，同时将这个更新操作记录到 redo log 里面，此时 redo log 处于 prepare 状态。然后告知执行器执行完成了，随时可以提交事务。执行器生成这个操作的 binlog，并把 binlog 写入磁盘。执行器调用引擎的提交事务接口，引擎把刚刚写入的 redo log 改成提交（commit）状态，更新完成。

1 prepare阶段 2 写binlog 3 commit
当在2之前崩溃时
重启恢复：后发现没有commit，回滚。备份恢复：没有binlog 。
一致
当在3之前崩溃
重启恢复：虽没有commit，但满足prepare和binlog完整，所以重启后会自动commit。备份：有binlog. 一致

#### 事物的隔离级别

读未提交:造成脏读

读已提交:造成不可重复读,一个事务中两次读取不一样

可重复读: 会有幻读现象,同一个数据范围读取的数量不一致

串行化

在实现上，数据库里面会创建一个视图，访问的时候以视图的逻辑结果为准。在“可重复读”隔离级别下，这个视图是在事务启动时创建的，**整个事务存在期间都用这个视图**。在“读提交”隔离级别下，这个视图是在每个 SQL 语句开始执行的时候创建的。这里需要注意的是，“读未提交”隔离级别下直接返回记录上的最新值，没有视图概念；而“串行化”隔离级别下直接用加锁的方式来避免并行访问。 

事务隔离的实现:MVCC,多版本并发控制，通过undo log版本链和read-view实现事务隔离

#### 一次查大量数据,会不会将数据库内存打爆

**mysql是边读边发的**,如果客户端接收得慢，会导致 MySQL 服务端由于结果发不出去，这个事务的执行时间变长。所以不会存在"内存用光"的问题,server不会保存完整的数据集,,如果客户端读取不及时,则会堵住mysql的查询过程,不会将内存打爆

#### 怎么最快复制一张表

mysqldump

```sql
mysqldump -h$host -P$port -u$user --add-locks=0 --no-create-info --single-transaction  --set-gtid-purged=OFF db1 t --where="a>900" --result-file=/client_tmp/t.sql
```

导出csv

 ```sql
select * from db1.t where a>900 into outfile '/server_tmp/t.csv';

load data infile '/server_tmp/t.csv' into table db2.t;
 ```













