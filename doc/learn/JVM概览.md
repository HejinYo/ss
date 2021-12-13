TAPD 自身 Markdown 解析存在问题，更好的阅读体验点这里[JVM 概览](http://www.ganpengyu.com/2019/04/12/go-into-jvm/)

# 为什么要学习 JVM

在很多 Java 程序员的开发生涯里，JVM 一直是黑盒子一般的存在，大家只知道运行 Java 程序需要依靠 JVM，千篇一律的配置几个类似 `-Xms` 和 `-Xmx` 的参数，可能到最后都不知道自己配置的参数有什么具体的意义。在我周围的 Java 程序员里面，甚至还有一部分有数年 Java 开发经验的人至今都不知道该怎么开启 JVM 的 GC 日志。但是，这一切并不妨碍我们开发出令人惊艳的产品。所以，我们为什么要学习 JVM？

从功利性的角度来讲，越来越多的公司在面试时都会针对 JVM 提问，学习 JVM 可以提高自己的面试通过率，当然这属于面向面试学习了。从实践的角度来讲，学习 JVM 可以帮助我们写出更优质的代码，比如你不会写出超过 8000 字节的巨型方法，因为你知道 JIT 不会编译它，每次只能解释执行，这是由 `-XX:HugeMethodLimit` 参数控制的；你也不会在 Metaspace OOM 时一头雾水，会首先定位是否是反射太频繁导致产生的类加载器过多而引发的。总的来说，学习 JVM 是提升我们 Java 内功的一种方式。

# JVM 的作用是什么

![](https://assets.ganpengyu.com/2019-03-29-Diagram-of-JVM.png)

如图所示，我们编写的 Java 代码通过 Java 编译器编译后成为字节码，即我们常说的 `.class` 文件。这些字节码文件和 JDK 类库的字节码文件分别通过 JVM 提供的基本的三个类加载器被加载到 JVM 之中，JVM 就是负责解析、执行这些字节码并管理和协调整个执行生命周期各项事件的平台。字节码是一种中间代码，通过 JVM 的解释、编译，最终通过操作系统与硬件达成交互。

# JVM 的运行时区域

JVM Runtime Area 也可以称作 JVM 运行时内存模型。JVM 本质上是操作系统上的一个进程，它的运行需要内存空间，而 JVM 运行时内存模型描述的就是 JVM 怎么划分和管理这些内存。

以 Hotspot VM 为例，我们将整个内存模型简化为下图所示：

![](https://assets.ganpengyu.com/2019-03-29-162626.png)

图中上方区域是受 JVM 直接管理的内存区域，其中 Stack Area 可以分为 JVM Stack 和 Native Method Stack，但在 Hotspot VM 中两者并合并到了一个区域。

下方的 Direct Memory 是不受 JVM 管理的内存，这些内存是属于操作系统的，JVM 通过在 Heap 中保存一个指向这些内存区域的引用来进行内存操作。当 Heap 中的引用被 GC 回收时，Direct Memory 中被使用的区域也自动被操作系统回收。这块内存通常用于 JDK1.4 开始提供的 NIO API 上，通过 `ByteBuffer#allocateDirect()` 方法实现直接内存的分配，而这个 ByteBuffer 就是这块分配出来的 Direct Memory 在 Heap 里存在的引用。基于 Direct Memory 进行 IO 操作的好处是避免了数据在 Direct Memory 和 Java Heap 之间的来回拷贝，也可以进一步实现「零拷贝」，避免数据在内核态与用户态之间来回拷贝。

当 Direct Memory 不足以分配新的空间时也会抛出 OOM。

## PC Register

在多核处理器上，一个时刻只能有一个核进行工作，而多线程情况下，线程可能分布在不同的核心上。当 A 线程任务还没有处理完时，所在核心失去了 CPU 执行权，此时就需要使用程序计数器记录当前程序执行的位置，等下次获得执行权后继续执行。这个区域是 JVM 规范中唯一一个不会出现 OOM 的区域。

## Stack Area

Stack Area 可以分为 JVM Stack 和 Native Method Stack。顾名思义，前者是 JVM 在调用 Java 方法时存储栈帧的空间，而后者用于在 JVM 调用 Native 方法时存储产生的栈帧。在 Hotspot VM 的实现中将两者合并成了一块内存区域。在 JVM 中，一个方法从调用开始到调用结束就是一个栈帧入栈和出栈的过程。每个线程栈的大小使用参数 `-Xss` 进行指定，默认是 1M。绝大部分情况下用不了这么大，建议配置为 256kb 即可。栈空间越大，则 JVM 能容纳的线程数越少；栈空间越小，可递归深度越低。

栈帧包含有局部变量表，存储编译期可知的基本数据类型以及对象的符号引用等信息，还包含有操作数栈、动态链接、方法出口等信息。

Stack 为什么是线程私有的，这涉及到「栈上分配」和「TLAB」两个概念。

### 栈上分配

所谓栈上分配就是允许将对象直接分配在栈上，而不用分配到 Heap 中。这样对象会随着栈帧的出栈自动销毁，不用等待 GC 进行回收，从而提高性能。但是要实现栈上分配是非常复杂的，涉及到「逃逸分析」和「标量替换」两项技术。

逃逸分析简单来说就是判断一个对象的存活范围是否有可能越出当前方法的执行范围，也就是说当前栈帧出栈的时候这个对象是否还会存活。如果分析结果是不会存活，那么这个对象就可以安全的分配在栈上，随着栈帧的出栈而被销毁。

标量替换是基于逃逸分析技术的。如果一个对象经过逃逸分析允许被分配在栈上，那么标量替换机制就可以将这个对象的字段直接以局部变量的方式进行分配，而不再采用对象的方法进行分配。

栈上分配自 JDK8 开始是默认开启的，如果关闭了栈上分配或者不符合栈上分配的条件，则 JVM 转而使用 TLAB 机制进行分配。

### TLAB 分配

TLAB 是 `Thread Local Allocation Buffer` 的首字母缩写，代表的是一块线程专属的内存区域。由于我们在 Heap 上无论是使用「指针碰撞」还是「空闲列表」方法进行内存分配，都会遇到多个线程请求内存分配时的同步问题。

TLAB 机制为每一条线程都划分了一块私有的内存区域供其分配对象，当一块 TLAB 被分配满之后就重新分配一块，而原来的那块区域从逻辑上变成了 Heap 的一部分。这样就避免了多线程直接向 Heap 请求内存分配的同步问题，提高了对象分配的效率。**一定注意：每一块 TLAB 都是 Heap 的一部分。**

从 JVM 源码来看，创建线程的第一步就是分配 TLAB 空间

```c++
void JavaThread::run() {
  // initialize thread-local alloc buffer related fields
  this->initialize_tlab();
  // used to test validitity of stack trace backs
  this->record_base_of_stack_pointer();
  // Record real stack base and size.
  this->record_stack_base_and_size();
  // Initialize thread local storage; set before calling MutexLocker
  this->initialize_thread_local_storage();
  this->create_stack_guard_pages();
  this->cache_global_variables();
  // ...
}
```

TLAB 的数据结构

```c++
class ThreadLocalAllocBuffer: public CHeapObj<mtThread> {
  friend class VMStructs;
private:
  HeapWord* _start;
  HeapWord* _top;
  HeapWord* _end;
  size_t    _desired_size;
  size_t    _refill_waste_limit;
  // ...
}
```

`_start` 和 `_end` 指针分别指向 TLAB 空间的头尾；`_top` 指针指向当前使用量的边界；`_desired_size` 是 TLAB 的大小；`_refill_waste_limit` 是 TLAB 的最大浪费空间。

假设最大浪费空间为 5K，如果 TLAB 目前还剩余 4K 空间，这时需要分配一个 8K 对象，这时就可以选择重新分配一个 TLAB 空间，因为当前这一块 TLAB 放弃后只会浪费 4K 空间，少于阈值。相反，对象则会直接去 Eden 分配，不会舍弃当前 TLAB。

## Heap Area

Heap 是 Java 内存模型中最大的一块区域，它被分为 Young Generation 和 Old (Tenured) Generation 两个部分，两个区域的比例默认大约是 1：2。Young 区又按照 8：1：1 的比例分为一个 Eden 区和 2 个 Survivor 区。

![](https://assets.ganpengyu.com/2019-03-31-MonitorJavaHeapSpace.png)

根据数据统计，程序运行过程中创建的大部分对象都会很快死亡，如果将对象都分配到一整块区域中，那么 GC 在回收这些死亡对象时负担就会变得非常重。为了提高内存的使用效率和 GC 的工作效率，根据对象存活周期的不同，将整个 Heap 划分为了上图中的几个部分。但无论怎么划分，每个区域里面保存的都是分配的对象。

### 对象的分配过程

当我们需要分配一个对象时，会直接在 Young 区中的 Eden 中尝试分配（大对象会直接在 Old 区上分配，使用参数 `PretenureSizeThreshold` 控制这个阈值，默认是无限大），如果空间不足则触发一次 YGC；YGC 完成后再次尝试分配，如果还是空间不足，则触发一次 FGC；FGC 结束后依然空间不足以分配，则再次触发 FGC，同时将软引用（Soft Reference）也一并回收；如果还是无法分配，则只能抛出 OOM。

### 对象在各个区域的流转

对象最初在 Eden 区中进行分配，当 YGC 发生时会将 Young 区所有存活的对象都放到当前没有使用的那个 Survivor 区中，两个 Survivor 区在两次 YGC 之间轮流使用，每次只使用一个（当 YGC 完成后存活对象超过 Survivor 区的大小时，会将部分对象晋升到 Old 区中）。Young 区中的对象每活过一次 YGC，存活周期就加 1，当活过 15 次 YGC 后就会晋升到 Old 区中，直到这个对象不再使用，被 GC 回收掉。

至于为什么需要两个 Survivor 区，我们在后面讲解 GC 算法时再分析。

#### 为什么对象最多活过 15 次 YGC 后就会晋升老年代

因为对象头中记录 YGC 存活周期的字段只有 4 bit 长度，最大表示数字就是 15。但这并不是说对象**一定**要活过 15 次 YGC 才能晋升到老年代，因为每次 YGC 后都会对这个阈值进行重新计算。比如使用 Serial GC 和 ParNew GC 的情况下，JVM 对于这个阈值的计算逻辑：

```text
uint ageTable::compute_tenuring_threshold(size_t survivor_capacity) {
  // TargetSurvivorRatio 默认值是 50
  size_t desired_survivor_size = (size_t)((((double) survivor_capacity)*TargetSurvivorRatio)/100);
  size_t total = 0;
  uint age = 1;
  assert(sizes[0] == 0, "no objects with age zero should be recorded");
  while (age < table_size) {
    total += sizes[age];
    // check if including objects of age 'age' made us pass the desired
    // size, if so 'age' is the new threshold
    if (total > desired_survivor_size) break;
    age++;
  }
  uint result = age < MaxTenuringThreshold ? age : MaxTenuringThreshold;
  
  if (PrintTenuringDistribution || UsePerfData) {

    if (PrintTenuringDistribution) {
      gclog_or_tty->cr();
      gclog_or_tty->print_cr("Desired survivor size " SIZE_FORMAT " bytes, new threshold %u (max %u)",
        desired_survivor_size*oopSize, result, (int) MaxTenuringThreshold);
    }
    // ....
  }
  return result;
}
```

这里的逻辑是将每个年龄段的对象占用空间进行累加，如果超过了 `desired_survivor_size` 则重新计算这个阈值，取这个阈值和配置的 MaxTenuringThreshold 中的最小值作为最新的 MaxTenuringThreshold。

`desired_survivor_size` 就是 survivor 区空间的一半。如果开启了 `PrintTenuringDistribution` 则会在 GC 日志中打印最新的 MaxTenuringThreshold。

PSGC 比较特殊，它通过 `InitialTenuringThreshold` 参数控制这个阈值，默认是 7，但每次 YGC 结束后也会重新计算阈值。

#### 对象从 Young 区晋升到 Old 区失败

当对象从 Young 区晋升到 Old 区时，如果 Old 区空间不足以容纳本次晋升对象所需要的空间就会晋升失败，并会导致触发一次 Full GC。JVM 并不总是等到 YGC 完成后要执行晋升时才做这个判断，而是在 YGC 发生之前就进行判定。根据多次晋升的数据统计出历次平均晋升对象的大小，如果当前 Old 区还有足够空间容纳，则直接进行 YGC，否则直接进行 Full GC 让 Old 区腾出足够的空间容纳将要晋升对象。

## Method Area

方法区是 JVM 规范中的一块区域，在 JDK8 之前使用永久代（Permanent Generation）实现方法区，从 JDK8 开始改用元数据区（Metaspace）来实现。所以「方法区」只是一种规范，而永久代和元数据区是对这个规范的一种实现。

Metaspace 被实现在 Native Memory 中，如果不限制最大使用内存，则除非系统内存耗尽，Metaspace 将无限增长。

### 为什么要将永久代替换为元数据区

根据官方给出的[说明](JEP 122: Remove the Permanent Generation)，这是为了促进 Oracle JRockit VM 和 Hotspot VM 融合而做出的选择，JRockit 本身没有永久代。

> Motivation
>
> This is part of the JRockit and Hotspot convergence effort. JRockit customers do not need to configure the permanent generation (since JRockit does not have a permanent generation) and are accustomed to not configuring the permanent generation.

从实际使用中来看，永久代和元数据区主要存在以下一些区别：

1. 内存空间设置

   在 JDK8 之前，我们通过 `-XX:PermSize` 和 `-XX:MaxPermSize` 来配置永久代的初始和最大容量。但是这个容量应该定多大其实是很难的，永久代里面有类元信息、常量池等各种数据，配置太小容易溢出、太大则浪费（尽管不会一次性 Commit 这么多内存）。永久代是紧邻 Heap 的内存区域，它使用的是 JVM 进程所拥有的内存。

   而元数据区是在 Native Memory 中分配，理论上可用最大内存就是操作系统的可用内存，也可以通过参数 `-XX:MaxMetaspaceSize` 来设置最大可用内存。相比于永久代，这是一种变通方法，以理论上无限大的空间来替代固定大小空间可能导致的空间不足或者空间浪费问题。

2. GC 触发

   永久代只有在空间耗尽导致无法分配新的对象时才会被动触发一次 Full GC；而元数据空间会动态调整参数 `-XX:MetaspaceSize` 的值，每当元数据空间使用内存达到这个阈值时就会主动触发 Full GC。主动触发可以避免被动触发时对象过多扫描困难和垃圾过多清理太慢的问题。

3. 不适应现代应用的特点

   永久代出现时，动态技术还不成熟和普及，永久代的空间足以容纳那时候的应用系统产生的方法区数据；而随着 JavaEE 和动态化技术的出现，永久代使用具有大小上限的空间来存储数据就显得捉襟见肘了。这一点可以参考第一点的说明。

当然，Metaspace 是否真的比永久代更优秀目前还没有一个统一的定论，也有很大一部分人认为 Metaspace 不仅没有解决永久代的问题，还增加了 JVM 方法区的实现难度。

### Metaspace 的内存布局

![](https://assets.ganpengyu.com/2019-03-30-metaspace_model.png)

从上图可以看到，Metaspace 主要由 Klass Metaspace 和 NoKlass Metaspace 两部分组成。顾名思义，Klass Metaspace 就是用来存储 Klass 的空间。

> 什么是 Klass ？
>
> Klass 是字节码在 JVM 中的运行时数据结构，只会存放在 Metaspace 中。而我们通常使用 `.class` 属性或者 `getClass()` 方法获取到的是 `java.lang.Class` 的对象，这个对象是存在于 Heap 中的。

Klass Metaspace 通过 `-XX:+UseCompressedClassPointers` 参数和 `-XX:+UseCompressedOops` 参数来启用，默认是启用的。其大小通过 `-XX:CompressedClassSpaceSize` 参数来控制，默认是 1G，但这块空间的初始大小是无法设定的。从这一点也可以看出，JVM 研发团队考虑以堆外更大空间来缓解方法区的 OOM。这块空间也可以不启用，Klass 将直接存放到 NoKlass Metaspace 中。特别的是，如果 `-Xmx` 大于 32G，JVM 也必然不会启用这块空间。Klass Metaspace 分配在紧邻 Heap 的 Native Memory 中。

`-XX:+UseCompressedClassPointers` 这个参数实际要表达的是启用压缩指针。在 32bit VM 中，存在于对象头 MarkWord 中的类型指针占 4 字节，而在 64bit VM 中占用 8 字节。当开启压缩指针后，64bit VM 中的类型指针会被压缩到 4 字节，从而达到节省内存空间的目的。

NoKlassMetaspace 除了在 Klass Metaspace 空间不存在时用于存储非压缩 Klass 指针外，还包括运行时常量池、CodeCache 等区域。

#### 运行时常量池

在每一份字节码文件中都存在一个 `Class 常量池`，主要保存的是编译期生成的符号引用和字面量。字面量包括文本字符串、基本类型的值和使用 `final` 声明的常量；符号引用包括类和方法的全限定名、字段（方法）的名称和描述符。

当一个类被 JVM 加载后，JVM 就会将它 Class 常量池中的内容放到运行时常量池中，同时将符号引用替换为直接应用，对于有文本字符串字面量的，会在 StringPool 中查找是否存在同样字符串以确保在运行时常量池中引用的字符串和在 StringPool 中的是一致的。

> Class 常量池和 Runtime 常量池的关系
>
> The runtime constant pool is an implementation-specific data structure that maps to the constant pool in the class file。

#### CodeCache

随着方法的不断被调用，JVM 也会统计各个方法被调用的次数。当某个方法调用频率比较高时，JVM 会使用 C1 JIT 编译器将其进行编译，编译后的代码就保存在 CodeCache 中。这样，之后再调用这个方法就不会在解释执行，提高了执行效率。

如果被 C1 编译后的方法中还存在更热点的方法，JVM 就会使用 C2 编译器对其进行更高层级的编译。C2 比 C1 更能编译出更高的执行效率，但编译时对时间和资源的消耗也更大，所以 JVM 采用解释执行、C1 编译、C2 编译这种「分层编译」方式来平衡效率和资源耗用。

分层编译使用参数 `-XX:+TieredCompilation` 开启，默认是开启的。

使用 `-Xint` 参数强制 JVM 对所有代码都解释执行，无论是否出现热点方法，都不进行编译；`-Xcomp` 参数强制 JVM 对所有代码都编译执行。

`ReservedCodeCacheSize` 参数用于设置 CodeCache 的大小，默认开启分层编译的情况下是 240M。如果 CodeCache 空间不足，会将一半最早编译的代码放到一个 Old 列表中，如果在一定时间内 Old 列表中的方法没有被调用就会从 CodeCache 中清除，表示这已经不是热点方法了。

### MetaspaceSize 和 MaxMetaspaceSize

如果配置过 Java8 之前版本的 JVM，你一定使用过 `-XX:PermSize` 和 `-XX:MaxPermSize` 两个参数来控制永久代的大小。当你看到 `-XX:MetaspaceSize` 和 `-XX:MaxMetaspaceSize`  两个参数的时候，也很大可能会认为他们与永久代配置的两个参数的作用是对应的，但实际上只能参数名称造成的误解。

MetaspaceSize 主要用于控制触发 MetaspaceGC 的阈值，即当 Metaspace 使用内存达到指定大小时触发一次 MetaspaceGC。但是 MetaspaceGC 并不是一个真正的 GC，它只是 Full GC 下的一种 GC Cause，在 GC 日志中表示为 `Full GC (Metadata GC Threshold)`。通过参数指定的 MetaspaceSize 的值是第一次触发 Full GC 的阈值，这个值会在每次 GC 完成后动态增大或者缩小。

MaxMetaspaceSize 用于控制 Metaspace 的最大可使用内存，默认数值非常大，基本上可以当做是无限大。由于 Metaspace 使用的是 Native Memory，为了避免因类存泄露而导致 Native Memory 被无限制消耗，通常还是建议设定一个合适的值。另外，MaxMetaspaceSize 只用于限制 Metaspace 可使用内存的上限（Max Memory），并不会在 JVM 已启动就分配这么大的内存空间。

> 关于 `java.lang.management.MemoryUsage` 四个值的说明
>
> init：表示 JVM 启动时向 OS 报告的需要的内存（并不会实际分配），约等于 `Xms`
>
> max：最大可向 OS 申请的内存空间，约等于 `Xmx
>
> committed：已向 OS 实际申请到的、可以直接使用的内存空间，空间不足时 JVM 继续向 OS 申请，直到 max
>
> used：表示已实际使用的内存空间，小于等于 committed

# 垃圾回收算法和垃圾回收器

## 对象存活判断算法

目前主流的存活判断算法有两种，引用计数器法和可达性分析算法，几乎所有主流的 JVM 都采用后者来判断对象是否存活。

可达性分析算法首先通过确定一些 `GC Roots` 根节点，然后跟着引用关系不断搜索对象，最终所有存活的对象形成一个图，凡是不属于这个图的节点的对象就是不可达的，会被 GC 回收掉。最常见的 GC Roots 有三种：

1. 栈帧中的本地变量表上引用的对象；
2. 类的 `static` 属性引用的对象；
3. 方法区运行时常量池中的常量引用的对象。

## 垃圾回收算法

在 JVM 中最常见的 GC 算法有三种，分别是标记-清除算法、复制算法、标记-整理-清除算法。

### 标记-清除算法

这是最原始最基础的 GC 算法，之后的其他算法都是基于这个算法改进的。整个算法分两步，先标记出所有要回收的对象，第二步将标记好的对象直接回收。这种算法简单直接，但是会产生大量的内存碎片，最终可能因为总的剩余空间足够分配对象，但是却找不到连续的内存空间而提前进行一次垃圾回收。

### 复制算法

这种算法要求把内存空间等分为两块，每次只使用一块。对象标记完成后将未被标记的对象全部复制到未使用的另一块空间，然后将当期使用的这部分空间直接清空。由于复制时目标空间是干净的，所以复制也是在连续空间在进行，最终不会产生内存碎片。

这种算法的劣势是直接让可用内存减少了一半，如果对象存活率过高，复制效率会明显降低。目前几乎所有的 JVM 都采用这种算法来回收 Young 区，但基于 IBM 的一项研究，98% 的对象都会很快死亡，所以将内存区域按照 8：1：1 的比例分为了一个 Eden 区和 2 个 Survivor 区，从而克服了算法最初设计导致的内存使用效率低的问题。

#### 为什么需要两个 Survivor 区

首先要明确 Survivor 区存在的意义是

1. 避免 Old 区中存在太多生命周期很短的对象；
2. 避免内存碎片化。

如果只有一个 Survivor 区，当 Eden 满了之后触发一次 YGC，存活对象被复制到 Survivor 中，下次 Eden 满了又触发 YGC 时问题就出现了。由于第二次 YGC 可能也回收了上次复制到 Survivor 区中的一部分对象，这时如果将本次 Eden 中存活的内存复制到 Survivor 区中就会出现内存碎片，而且碎片是存在已死亡对象的，这些对象无法被回收。

两个 Survivor 区的情况下，每次只使用一个区域，另一个区域就会是干净且连续的内存空间，这样就避免了内存碎片的出现。关于内存碎片带来的问题，我们在后面的 CMS GC 部分还会更细的聊到。

### 标记-整理-清除算法

这种算法也被叫做标记-整理算法，相比于标记-清除算法多了一个整理的过程。在标记完对象后，让存活对象都往一端移动，然后将端边界之外的内存都清空，这样就解决了标记-清除算法会产生大量内存碎片的问题。

虽然复制算法也解决了内存碎片问题，但是对于对象存活率非常高的内存区域而言，复制算法的效率比较低。

### 分代回收算法

分代回收算法并不是一种实际的算法，它是指对于 JVM Heap 中的不同内存区域采用不同的垃圾回收算法，从而整体提高 JVM 的内存使用效率和 GC 工作效率。

## 垃圾回收器

正如我们前文提到的「分代回收算法」，针对不同的内存区域，JVM 也提供了不同的垃圾回收器。Young 区常见的垃圾回收器有：

1. Serial GC

   这是 JVM 以 Client 模式启动时默认的 GC，实现逻辑简单，单线程全程 STW 进行垃圾回收。这个 GC 不能发挥多核的优势，通常很少使用。但随着 Serverless 架构的兴起，Serial GC 又有了用武之地。

2. ParNew GC

   这是 Serial GC 的多线程版本，可以通过 `XX:ParallelGCThreads` 参数设置参与 GC 的线程数。当 Old 区启动 CMS GC 时，将默认在 Young 区使用这个 GC。

3. PSGC

   这是一款注重吞吐量的垃圾回收器，多用于后台计算类的系统。

Old 区常见的垃圾回收器有：

1. Serial Old

   Serial GC 的 Old 区版本，主要作用是在 CMS 出现 CMF 时替代 CMS 进行 Full GC。

2. Parallel Old

   PSGC 的 Old 区版本，同样注重吞吐量。

3. CMS

   在 G1GC 出现之前使用最多的 Old 区 GC，它是一款几乎完全并发的垃圾回收器，注意不是完全并发。它采用标记-清除算法，随着应用程序的长时间运行会产生很严重的内存碎片问题。

除了上面的这些常见分代 GC 外，随着垃圾回收算法的不断改进，又出现了如 G1GC 这类混合式 GC。这类 GC 不再从物理上将 Heap 划分为 Young 区和 Old 区，而是仅从逻辑上进行划分，甚至 JDK11 提供的 ZGC 连逻辑上都不存在 Young 区和 Old 区了。

## 垃圾回收器的默认配对

|  #   |        配置         | Young 区 |    Old 区    |                             备注                             |
| :--: | :-----------------: | :------: | :----------: | :----------------------------------------------------------: |
|  1   |    +UseSerialGC     |  Serial  |  Serial Old  |                      默认设置 Old 区 GC                      |
|  2   |   +UseParallelGC    |    PS    | Parallel Old |              默认同时设置 -XX:+UseParallelOldGC              |
|  3   | +UseConcMarkSweepGC |  ParNew  |     CMS      | 默认同时设置 +UseParNewGC，且 Old 区使用 CMS 时，Young 区只能使用 ParNew |
|  4   |      +UseG1GC       |    G1    |      G1      |             G1下 Young 区和 Old 区变成了逻辑分区             |

## GC 术语

Young GC = Minor GC

Major GC = 对老年代和方法区的收回

Full GC = 对整个堆进行回收

除了 YGC 和 Minor GC 的意义是在业界达成了一致，都表示对 Young 区的回收，Major GC 和 Full GC 的定义都不确定，包括 JVM 规范也没有明确定义。Major GC 有时候也可以看做是 Full GC，因为 Major GC 很多时候会触发一次 Minor GC。

## CMS GC

CMS GC 从 JDK1.4 引入，经过多个版本的不断发展和增强，最终在 Java9 中被标记为 Deprecated。这个 GC 适用于对停顿时间非常敏感的前端系统，目的是以较短的停顿尽快回收垃圾。CMS GC 之所以称作「几乎完全并发的垃圾回收器」是因为在整个 GC 过程中仍然存在 2 处 STW（Stop The World），也就是全局暂停，用户线程也不能执行。

### CMS 的工作流程

CMS 主要由四个步骤构成：

1. 初始标记

   STW 阶段，寻找 GC Roots

2. 并发标记

   和用户线程并发执行，这个阶段会根据 GC Roots 标记对象

3. 最终标记

   并发标记过程中客户线程的运行导致引用发生变动，这一步对并发标记阶段中不可达对象在最终标记阶段变成可达对象的情况进行修正。

   如果并发标记阶段新产生的不可达对象，且没有在并发标记阶段被标记的，成为「浮动垃圾」无法回收，等待下一次 CMS GC。

4. 并发清除

   和用户线程并发执行，将标记的死亡对象进行清理

一个 CMS GC 会增长两次 `jstat` 中 `FGC` 的值，也就是说，FGC 记录的是 GC STW 的次数，而不是一次对整个堆的完整收集。

### -XX:+UseConcMarkSweepGC

这个参数表示启用 CMS GC，默认会在 Young 区启用 ParNew GC。从官方对参数的描述来看，CMS GC 确实是一个应用于 Old 区的 GC。

```c++
product(bool, UseConcMarkSweepGC, false,                                  \
          "Use Concurrent Mark-Sweep GC in the old generation")
```

JVM 默认启动 ParNew GC 的逻辑：

```c++
// Set per-collector flags
if (UseParallelGC || UseParallelOldGC) {
  set_parallel_gc_flags();
} else if (UseConcMarkSweepGC) { // Should be done before ParNew check below
  set_cms_and_parnew_gc_flags();
} else if (UseParNewGC) {  // Skipped if CMS is set above
  set_parnew_gc_flags();
} else if (UseG1GC) {
  set_g1_gc_flags();
}
```

### -XX:CMSInitiatingOccupancyFraction

这个参数用于设置当 Old 区使用率达到多少时应该发生一次 CMS GC，但是要注意是「应该发生」而不是「一定发生」，也就是说即使 Old 区使用率达到了指定阈值也可能不会触发 CMS GC。因为 CMS GC 的触发是由其扫描 Old 区使用量的线程控制的，只有当线程扫描到使用率达到阈值时才发触发 CMS GC。这个线程每 2 秒对 Old 区使用率进行一次计算，如果超过指定值则启动 CMS GC。这个扫描频率可以使用参数 `-XX:CMSWaitDuration` 指定。

这个参与必须要和 `-XX:+UseCMSInitiatingOccupancyOnly` 参数配合使用，只有开启了这个参数，自定义配置才起作用，否则会使用 JVM 的默认值，通常是 92%。

### -XX:+CMSScavengeBeforeRemark

这个参数表示 CMS GC 在执行最终标记（重新标记）时先「尝试」执行一次 Young GC。

最终标记时涉及整个堆，包括 Young 区和 Old 区。扫描 Young 区是因为如果 Young 区对象引用了 Old 区对象，那么 Old 区的对象就是存活对象，Young 区对象就是 Old GC 的 GC Roots。

在最终标记前进行一次 YGC 可以大量减少 Young 区对象，那么需要扫描的 Young 区对象数量就变少了，从而最终标记的速度也就提高了。

但是有利就有弊，如果运气不好遇到 Young 区对象存活率极高或者 Young 区对象很少的情况，这次 YGC 的意义就不大了。

#### JVM 对跨代引用的处理

YGC 的目的是回收掉 Young 区中不再存活的对象，如果 Young 区中的对象直接就在 Tracing Chain 上自然是不可回收，但是 Old 区也有可能引用了 Young 区对象，这时候为了确保 Young 区中的对象不被错误回收，最直接的办法就是扫描整个 Old 区的对象，看他们引用了哪些 Young 区对象。

如果 Old 区很小，上面的做法简单直接没有问题，但是如果 Old 区很大的情况，效率就非常低了。JVM 采用了一种叫 CardTable（卡表）的数据结构来解决这个问题。

![](https://assets.ganpengyu.com/2019-04-02-cardtable.jpg)

卡表就是一个 bit 数组，元素默认值为 0。从上图可以看出，Old 区被等分成了多个区域，每个区域对应卡表上的一个位置，如果某个区域中有对象引用了 Young 区的对象，则这个区域在卡表中对应的位置的值被设为 1。

YGC 时通过卡表的标志位就能让 GC 只扫描存在跨代引用的内存区域，从而避免了全 Old 区扫描。基于卡表的扫描流程可以从源码中看到：

```c++
void ClearNoncleanCardWrapper::do_MemRegion(MemRegion mr) {
  // ...
  // Old 区最后一个 Card 起始地址
  jbyte* cur_entry = _ct->byte_for(mr.last());
  // Old 区第一个 Card 起始地址
  const jbyte* limit = _ct->byte_for(mr.start());
  // Dirty Card 截止地址
  HeapWord* end_of_non_clean = mr.end();
  // Dirty Card 起始地址
  HeapWord* start_of_non_clean = end_of_non_clean;
  while (cur_entry >= limit) { // 从后往前遍历 Card
    HeapWord* cur_hw = _ct->addr_for(cur_entry);
    // 如果当前 Card Dirty，先用 clear_card() 方法将其设置为 Clean
    if ((*cur_entry != CardTableRS::clean_card_val()) && clear_card(cur_entry)) {
      // 记录等会要清除的起始地址
      start_of_non_clean = cur_hw;
    } else { // 如果遇到一个 Clean Card
      // 如果之前遇到过 DirtyCard，先清理掉再继续扫描
      if (start_of_non_clean < end_of_non_clean) {
        const MemRegion mrd(start_of_non_clean, end_of_non_clean);
        _dirty_card_closure->do_MemRegion(mrd);
      }
      // ...
      end_of_non_clean = cur_hw;
      start_of_non_clean = cur_hw;
    }
    cur_entry--;
  }
  // 最终清理记录的 Dirty Card
  if (start_of_non_clean < end_of_non_clean) {
    const MemRegion mrd(start_of_non_clean, end_of_non_clean);
    _dirty_card_closure->do_MemRegion(mrd);
  }
}
```

### -XX:+CMSClassUnloadingEnabled

开启这个参数则每次触发 CMS GC 时都会顺带收集一次 Metaspace。当 Metaspace 达到空间使用阈值时会触发一次 FullGC，通过 CMS GC 经常清理 Metaspace，可以减小 Metaspace 触发 Full GC 的频率。

### -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses

开启这个参数，则每次由 System.gc() 触发的 FullGC 都转变为 CMS GC（前提是使用 CMS GC），并且要对 Metaspace 进行收集。

### -XX:+UseCMSCompactAtFullCollection

开启这个参数表示在 Full GC 后需要进行空间压缩，清除内存碎片，配合参数 `-XX:CMSFullGCsBeforeCompaction` 使用。后者指定多少次 Full GC 实际发生之后才进行一次压缩，默认是 0，表示每次 Full GC 后都要压缩。

清除内存碎片需要移动内存中的对象，所以只能单线程允许，这会让应用系统停顿时间更长。如果 Old 区足够大且内存足够零碎，那等待整理碎片的时间是不可接受的。如果不清理内存碎片，随着应用程序的长时间允许，最终会因为大量的内存碎片而没有足够空间分配对象，导致频繁 Full GC，最终 OOM。

### 一个生产可用的 CMS 配置

参考服务器配置：Linux 64bit、8C16G、JDK8

```shell
-Xmx10880M 
-Xms10880M 
-Xmn4032M 
-XX:MaxMetaspaceSize=512M 
-XX:MetaspaceSize=512M 
-XX:+UseConcMarkSweepGC 
-XX:+UseCMSInitiatingOccupancyOnly 
-XX:CMSInitiatingOccupancyFraction=70 
-XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses 
-XX:+CMSClassUnloadingEnabled 
-XX:+CMSScavengeBeforeRemark   
-XX:+PrintGCDetails 
-XX:+PrintGCDateStamps 
-XX:ErrorFile=/home/admin/gclogs/hs_err_pid%p.log   
-Xloggc:/home/admin/gclogs/gc.log
-XX:+HeapDumpOnOutOfMemoryError 
-XX:HeapDumpPath=/home/admin/gclogs/heapdump 
-XX:+PrintGCApplicationStoppedTime
```

`ErrorFile` 是 JVM Crash 日志；`PrintGCApplicationStoppedTime` 是打印 GC 过程中用户线程停顿时间，即 STW 的时长。将 Heap、Young、Metaspace 的初始和限制大小设置为一样可以避免扩容带来的 Full GC。

### CMS 劣势

1. CPU 敏感

   默认会使用 （CPU数 + 3）/ 4 条线程，如果服务器只有 2C，那么服务应用系统的能力直接减少一半。

2. 内存碎片

   默认不会清理碎片，长时间运行会导致严重碎片，引发频繁 Full GC，甚至 OOM。

3. CMF 导致 GC  退化

   出现 CMS 时 GC 退化为 Serial Old，等待垃圾回收和内存清理，停顿时间变长。

### CMS 保护性 OOM

如果程序运行过程中，98%的时间都在做垃圾回收，同时这些回收动作清理的堆内存空间不足总大小的 2%，则 CMS GC 会主动抛出 OOM。因为 CMS GC 会导致内存碎片，这个机制可以防止应用在小堆上长时间运行。因为大部分时间都在 GC，应用基本就等于失去了服务能力。

# GC 日志

GC 日志反映了 GC 的工作情况，读懂 GC 日志可以辅助我们定位内存问题，在 GC 日志中，我们主要关注 GC Cause、GC Flow 以及 GC 成果。以下面示例的 GC 日志为例：

## 示例程序

Programmer.java

```java
public class Programmer {
  	private long id;
    private String name;
    private int age;
    private boolean male;
}
```

CreateTask.java

```java
public class CreateTask implements Runnable {
    private List<Programmer> programmers = new ArrayList<>();
    @Override
    public void run() {
        for (int i = 0; i < 100000000; i++) {
            programmers.add(create());
        }
    }
    private Programmer create() {
        Random random = new Random();
        long id = (long) random.nextInt(99999999);
        String name = "brandon.p.gan";
        int age = random.nextInt(150);
        boolean male = age % 2 == 0;
        return new Programmer(id, name, age, male);
    }
}
```

Main.java

```java
public class Main {
    public static void main(String[] args) {
        Thread[] threads = new Thread[8];
        for (int i = 0; i < 8; i++) {
            threads[i] = new Thread(() -> new CreateTask());
        }
        for (int i = 0; i < 8; i++) {
            threads[i].start();
        }
        TimeUnit.DAYS.sleep(1);
    }
}
```

## JVM 参数

```shell
-Xms20M 
-Xmx20M 
-XX:+UseConcMarkSweepGC 
-XX:+UseCMSInitiatingOccupancyOnly 
-XX:CMSInitiatingOccupancyFraction=70 
-XX:+CMSScavengeBeforeRemark
-XX:+HeapDumpOnOutOfMemoryError 
-XX:HeapDumpPath=/Users/brandon.p.gan/Desktop/jvmdemo/oom/logs 
-XX:+PrintGCDetails 
-XX:+PrintGCDateStamps 
-Xloggc:/Users/brandon.p.gan/Desktop/jvmdemo/oom/logs/gc.log 
```

## Young GC 日志

```txt
2019-04-09T15:01:38.575-0800: 0.932: [GC (Allocation Failure) 2019-04-09T15:01:38.575-0800: 0.933: [ParNew: 5504K->639K(6144K), 0.0057317 secs] 5504K->1449K(19840K), 0.0058849 secs] [Times: user=0.01 sys=0.00, real=0.01 secs] 
```

`2019-04-09T15:01:38.575-0800` 是日志打印时间；

`0.932` 是一个相对时间，即 JVM 从启动到现在所经过的时间；

`[GC (Allocation Failure) 2019-04-09T15:01:38.575-0800: 0.933` 表示本次 GC 是由于内存分配失败导致的，后面的时间是 GC 触发的时间和相对时间。

`[ParNew: 5504K->639K(6144K), 0.0057317 secs]` 表示这是 Young GC，内存统计信息模式为：GC前->GC后（Young区大小），接着是回收 Young 区的耗时。

`[Times: user=0.01 sys=0.00, real=0.01 secs] ` user 是用户态 CPU 时间，sys 是内核态 CPU 事件和操作的墙钟时间，real 是最终的实际耗时。

​	墙钟时间包括非运算等待耗时，比如线程阻塞等待时间、磁盘I/O 等。

​	多线程情况下 user 和 sys 时间可能会叠加，所以输出中可能会超过 real 时间。

## CMS GC 日志

```txt
[GC (CMS Initial Mark) [1 CMS-initial-mark: 11317K(13696K)] 12040K(19840K), 0.0016389 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]  
```

`GC (CMS Initial Mark)` CMS GC 分为四个阶段，这是一次 CMS GC 的一个阶段，初始标记。

`CMS-initial-mark: 11317K(13696K)` 出师标记时 Old 区使用量和总量。

`12040K(19840K), 0.0016389 secs` 整个堆当前用量和总量，以及本次初始标记耗时。

```txt
2019-04-09T15:01:38.783-0800: 1.141: [CMS-concurrent-mark-start]
```

```txt
2019-04-09T15:01:38.792-0800: 1.150: [GC (Allocation Failure) 2019-04-09T15:01:38.792-0800: 1.150: [ParNew: 6144K->6144K(6144K), 0.0000375 secs]2019-04-09T15:01:38.792-0800: 1.150: [CMS2019-04-09T15:01:38.803-0800: 1.161: [CMS-concurrent-mark: 0.020/0.020 secs] [Times: user=0.04 sys=0.00, real=0.02 secs] 
```

`GC (Allocation Failure)` 结合上一条并发标记日志，表示这是在并发标记过程中 YGC 发生。

```txt
 (concurrent mode failure): 11317K->13695K(13696K), 0.0506163 secs] 17461K->14595K(19840K), [Metaspace: 3407K->3407K(1056768K)], 0.0507949 secs] [Times: user=0.05 sys=0.00, real=0.05 secs] 
```

并发标记中 YGC 发生导致对象晋升，Old 区空间不足，出现 CMF。

```txt
[Full GC (Allocation Failure) 2019-04-09T15:01:38.851-0800: 1.209: [CMS: 13695K->13695K(13696K), 0.0442915 secs] 19839K->17237K(19840K), [Metaspace: 3407K->3407K(1056768K)], 0.0444186 secs] [Times: user=0.05 sys=0.00, real=0.04 secs] 

```

`Full GC (Allocation Failure)` 由于 CMF 出现，转入 Full GC。

`CMS: 13695K->13695K(13696K), 0.0442915 secs] 19839K->17237K(19840K)` 这次 Full GC 的结果是 Old 区没有对象被回收，Young 区回收了 2M 左右。

# 怎么选择垃圾回收器

1. 单核只能用串行；
2. 看吞吐量用并行；
3. 看停顿时间用并发；
4. 不会选可以把堆的大小设置好，交给 JVM 帮你选。

# 什么时候会触发 FullGC

1. System.gc() 调用

   虽然该方法调用并不一定触发 Full GC，但很大概率下都会触发，可以使用 -XX:+ DisableExplicitGC 来禁止通过这个方法触发 FullGC。

2. Old 区空间不足

   在 Old 区分配大数组大对象失败时触发 Full GC 尝试腾出空间

3. Metaspace 空间不足

   类加载过多、自定义类加载器过多，以及反射操作都会导致这里空间不足

4. Promotion Failed 晋升失败

   根据历次晋升数据大小的平均值计算，如果 Old 区空间不足以容纳本次晋升对象，则触发 Full GC 腾空间

5. CMS 出现 CMF

   CMF 的出现是因为 GC 线程和应用线程在「并发清理」阶段是并行的，如果 GC 线程不能及时回收完对象腾出足够的内存空间，当回收过程中出现对象晋升失败或者大对象分配失败，就会出现 CMF，这时候会转为 Serial Old GC 进行 FullGC。

# 内存溢出问题排查

JVM 内部定义的 OOM 有 6 种：

```c++
Handle msg = java_lang_String::create_from_str("Java heap space", CHECK_false);
java_lang_Throwable::set_message(Universe::_out_of_memory_error_java_heap, msg());

msg = java_lang_String::create_from_str("Metaspace", CHECK_false);
java_lang_Throwable::set_message(Universe::_out_of_memory_error_metaspace, msg());

msg = java_lang_String::create_from_str("Compressed class space", CHECK_false);
java_lang_Throwable::set_message(Universe::_out_of_memory_error_class_metaspace, msg());

msg = java_lang_String::create_from_str("Requested array size exceeds VM limit", CHECK_false);
java_lang_Throwable::set_message(Universe::_out_of_memory_error_array_size, msg());

msg = java_lang_String::create_from_str("GC overhead limit exceeded", CHECK_false);
java_lang_Throwable::set_message(Universe::_out_of_memory_error_gc_overhead_limit, msg());

msg = java_lang_String::create_from_str("Java heap space: failed reallocation of scalar replaced objects", CHECK_false);
java_lang_Throwable::set_message(Universe::_out_of_memory_error_realloc_objects, msg());

```

当应用系统出现 OOM 时，我们可以根据异常信息快速定位到 OOM 的内存区域。下一步是保留现场，把当前内存 Dump 成文件。

```shell
jmap -dump:live,format=b,file=heap.bin <pid>

```

需要注意如果使用了 `:live` 则会先触发一次 Full GC 再 Dump，这个参数表示只 Dump 活着的对象。

如果配置了参数

```shell
-XX:+HeapDumpOnOutOfMemoryError 
-XX:HeapDumpPath=/home/admin/gclogs/heapdump

```

当发生 OOM 时会自动 Dump 内存到指定目录。

拿到了 Dump 文件可以选择 MAT 工具或者 jhat 进行分析。以 MAT 为例，从 Histogram 和 Dominator_tree 视图中可分别以「类」和「对象实例」视角进行分析。前者可以快速定位存在大量实例的类；后者可以快速定位大量相同类的实例的引用关系。

## Histogram 视角

![](https://assets.ganpengyu.com/2019-04-10-mat_histogram.jpg)

从这个视角中可以很方便的看到 `Programmer` 的实例有 51.7 万个，占用了 16 M的堆内存。

### Shallow Heap & Retained Heap

Shallow Heap 表示对象自身所占用的空间，不包括直接或间接引用对象所占用的空间

Retained Heap 表示对象自身及其直接或间接引用对象所占用的空间

## Dominator_tree 视角

![](https://assets.ganpengyu.com/2019-04-10-mat_dominator_tree.jpg)

在这个视角下，通过堆内存占用量进行排序可以快速定位到持有大量对象实例的线程，观察发现这些就是我们创建的应用线程，展开详情可以看到持有的所有对象。

# 获取 openjdk 源码

openjdk 的源码使用 Mercurial 这个反人类的源码管理工具进行管理，如果需要直接从官方下载源码，不仅需要科学上网，还需要有强大的运气。但是 GitHub 上有一个好心人做了 [同步仓库](https://github.com/unofficial-openjdk/openjdk) ，你可以直接从这里下载到源码。

这个仓库非常大，以天朝访问 GitHub 的速度，我们有生之年不一定能够下载完成，所以建议你最好在境外服务器上下载，大约 3 分钟就能下载完成，通过切换不同的分支拿到不同版本的 openjdk 源码。
