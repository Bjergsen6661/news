<html>
<head>
    <title>Hello Freemarker</title>
</head>
<body>
<#--
    Freemarker的构成语法：
    1. 注释
    2. 表达式
    3. 指令
    4. 普通文本
-->

<#-- 注释 -->
<#-- ${} 为变量表达式，同jsp -->

<#-- 输出字符串 -->
<div>hello ${there}</div>

<br>

<#-- 输出对象 -->
<div>
    用户id：${stu.uid}<br/>
    用户姓名：${stu.username}<br/>
    年龄：${stu.age}<br/>
    生日：${stu.birthday?string('yyyy-MM-dd HH:mm:ss')}<br/>
    余额：${stu.amount}<br/>
    已育：${stu.haveChild?string('yes', 'no')}<br/>
</div>


</body>
</html>
