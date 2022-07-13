- top命令查看cpu占用情况找到占用进程pid
- 关键操作,查看该进程下的线程信息  ps -mp pid -o THREAD,tid,time
- 找到线程占用cpu异常的线程,将线程id(上方tid)需要转为化16进制,得到16进制线程id printf "%x\n" tid
- 使用jstack(拉出指定线程的堆栈信息)  jstack pid | grep 线程id -A100,可以得到堆栈日志,找到自己的代码

cpu飙升的原因
- while陷入死循环无法推出或者死递归,或者超大循环
- 频繁创建对象除非young gc会导致cpu占用率飙升
- 超大的浮点计算,比如3d渲染,科学计算
- 超多线程应用程序,线程调度开销极大 


oom排查过程
- jps查看java进程
- jstat -gcutil pid 1000 10 (一秒钟一次 产生十次下)  可以查看堆中各个分区内存占用情况以及gc回收次数
- arths查看具体情况 命令行交互排查 dashboard 动态展现仪表盘,直观看到个各种大致占用情况
- 详细分析,将堆中文件dump (如果不让dump,只能花费n倍时间排查问题),使用visualvm分析文件
- JVM 启动参数配置添加以下参数
  -XX:+HeapDumpOnOutOfMemoryError
  -XX:HeapDumpPath=./(参数为 Dump 文件生成路径)
 当 JVM 发生 OOM 异常自动导出 Dump 文件，文件名称默认格式：java_pid{pid}.hprof
