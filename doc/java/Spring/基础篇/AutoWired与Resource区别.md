# AutoWired 与 Resource 区别

今天给大家分享一道非常经典的面试题，同时也是一道高频面试题：Resource和AutoWired注解有什么不同？
首先，先了解一下**AutoWired注解**。
AutoWired是**Spring框架**提供的一个注解，它**默认**根据类型来实现Bean的依赖注入。
在这个注解中，有一个required属性，默认值为true，表示**必须实例化一个注入的Bean**。**如果找不到**对应类型的Bean，**会在应用启动时报错**。
不过，如果我们不希望实现自动注入，可以将这个属性设置为false。
此外，如果在Spring IOC**容器中存在多个相同类型的Bean实例**，**AutoWired**会**先按照byType查找**，然后**再按照byName查找**，如果都找不到就会提示异常。
针对这种情况，我们可以使用primary或者Qualifier这两个注解来解决。
**primary**表示优先级，当存在多个相同类型的Bean时，优先使用声明了primary注解的Bean。
而Qualifier注解则类似于条件筛选，根据指定的Bean名称找目标Bean。
接下来，再来看看**Resource注解**。
Resource是JDK提供的注解，Spring在实现上提供了对这种注解功能的支持，它的使用方式与AutoWired完全相同。
最大的区别在于，Resource**默认**先按照byName查找Bean，然后再按照byType查找Bean，同时也支持指定由哪种方式查找Bean。
当指定为byName时，Spring会根据Bean的名称进行依赖注入；
指定为byType时，Spring会根据类型来实现依赖注入。


> 原文: <https://www.yuque.com/tulingzhouyu/db22bv/gl8aguqidlwwv9nr>