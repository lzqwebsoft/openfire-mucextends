# MUCExtends Plugin Readme

## Overview-概述
The MUCExtends plugin extends XMPP IQ protocol for fetch the rooms list which users has been joined, and save the members of MUC to database then users join the rooms.

MUCExtends插件扩展了XMPP的IQ查询协议，用于获取用户加的房间列表。同时当用户加入房间时，将加入的用户数据保存于数据库，实现类QQ群的功能.

## 思路
由于Openfire的房间是当用户加入退出登录后，用户也自动退出了该房间，当下次再登录时，需要重新出席加入房间，没有能像QQ群那样持久保持的功能。

为了实现房间的持久保存，设计这个插件的思路就是，使用`org.jivesoftware.openfire.muc.MUCEventListener`监听器处理，当用户发送出席房间的`presence`消息时，将加入房间的用户保存入表`ofMucMember`。再当用户下次登录时，使用自定义的`iq`消息查询获取用户加入的房间列表，再自动加入（使用原生的XMPP出席房间协议，这一步需客户端自己实现），从而实现类QQ群的功能。

## 安装
复制编译好的文件：`wrok/plugins/mucextends.jar`，到你安装的Openfire的plugins目录，插件将会自动部署，同时确保运行的Openfire版本不低于3.4.0。

## 使用
当你安装好插件后，查询用户加入的房间列表只需要发送如下的IQ协议：
```xml
<iq type='get' xmlns='jabber:client' id='b36d63aa-c8f3-4262-9368-2e7ab0858bbe:sendIQ'>
    <query xmlns='im:iq:group'/>
</iq>
```
这时Openfire服务器的响应如下：
```xml
<iq type="result" to="zqluo@qz-zqluo-01/web5b483285f104e" id="b36d63aa-c8f3-4262-9368-2e7ab0858bbe:sendIQ">
    <query xmlns="im:iq:group">
       <room xmlns="" nickname="zqluo" id="room2@conference.localhost" naturalName="room2">room2</room>
    </query>
</iq>
```
房间的用户数据将会被保存在`ofMucMember`表中.

## 开发
只需要将上面的代码导入到对应的Openfire源码目录中，目录结构不变，使用Openfire提供的`build/build.xml` Ant工具编译。
![set](https://user-images.githubusercontent.com/1137657/42677221-b852a374-86ad-11e8-9ddf-c06f68848f2f.png)
![build](https://user-images.githubusercontent.com/1137657/42677224-ba0ff02c-86ad-11e8-9c1e-7a5e7576e5be.png)
![run](https://user-images.githubusercontent.com/1137657/42677227-bbe69310-86ad-11e8-80f4-875684d618f1.png)

## 参考：

* [Openfire4源码部署到eclipse中并编译](http://www.cnblogs.com/mvilplss/p/6005158.html)
* [Openfire插件开发图解](https://www.cnblogs.com/mvilplss/p/6022022.html)
* [将Openfire中的MUC改造成类似QQ群一样的永久群](https://blog.csdn.net/yangzl2008/article/details/16991175)
* [Openfire简介](https://308681282.gitbooks.io/openfire/content/)
* [XEP-0045: 多用户聊天](http://wiki.jabbercn.org/XEP-0045#.E8.BF.9B.E5.85.A5.E4.B8.80.E4.B8.AA.E6.88.BF.E9.97.B4)