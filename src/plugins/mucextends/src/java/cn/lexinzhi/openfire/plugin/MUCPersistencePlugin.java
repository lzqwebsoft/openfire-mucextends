package cn.lexinzhi.openfire.plugin;

import java.io.File;

import org.jivesoftware.openfire.IQRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.muc.MUCEventDispatcher;

public class MUCPersistencePlugin implements Plugin {

    @Override
    public void initializePlugin(PluginManager manager, File pluginDirectory) {
        System.out.println("用户房间转群插件运行成功!");
        // 添加自定义的IQ 查询处理器，用于查询返回当前用户加入的访间（群）
        IQHandler myHandler = new MUCPersistenceHandler();
        IQRouter iqRouter = XMPPServer.getInstance().getIQRouter();
        iqRouter.addHandler(myHandler);
        // 添加用户加入房间的事件监听，用于将加入房间的用户写入数据库，实现群的功能
        MUCEventDispatcher.addListener(new MyMUCEventListener());
    }

    @Override
    public void destroyPlugin() {

    }

}
