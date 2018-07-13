package cn.lexinzhi.openfire.plugin;

import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.muc.spi.LocalMUCRoom;
import org.jivesoftware.openfire.muc.spi.MultiUserChatServiceImpl;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

import cn.lexinzhi.openfire.plugin.dao.MUCDao;

public class MUCPersistenceHandler extends IQHandler {
    private IQHandlerInfo info;
    private XMPPServer server;

    public MUCPersistenceHandler() {
        super("grups Roster Handler");
        server = XMPPServer.getInstance();
        // 自定义的xmmp iq查询协议
        info = new IQHandlerInfo("query", "im:iq:group");
    }

    @Override
    public IQHandlerInfo getInfo() {
        return info;
    }

    @Override
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        JID userJid = packet.getFrom();

        server = XMPPServer.getInstance();
        List<Map<String, String>> data = MUCDao.getMUCInfo(userJid.toBareJID());

        if (data == null || data.isEmpty()) {
            return null;
        }
        Map<String, String> map = null;

        /**
         * 构建iq的扩展包，用于发送用户所在房间的名称。
         */
        Document document = DocumentHelper.createDocument();
        Element iqe = document.addElement("iq");
        iqe.addAttribute("type", "result");
        iqe.addAttribute("to", userJid.toFullJID());
        iqe.addAttribute("id", packet.getID());

        Namespace namespace = new Namespace("", info.getNamespace());
        Element muc = iqe.addElement(info.getName());
        muc.add(namespace);

        for (int i = 0, len = data.size(); i < len; i++) {
            map = data.get(i);

            String serviceID = map.get("serviceID");
            MultiUserChatServiceImpl mucService = (MultiUserChatServiceImpl) server.getMultiUserChatManager().getMultiUserChatService(Long.parseLong(serviceID));
            String roomName = map.get("name");
            LocalMUCRoom room = (LocalMUCRoom) mucService.getChatRoom(roomName);

            // 增加room和account信息
            Element roome = muc.addElement("room");
            roome.setText(map.get("description"));
            roome.addAttribute("nickname", map.get("nickname"));
            roome.addAttribute("id", room.getJID().toBareJID());
            roome.addAttribute("naturalName", map.get("naturalName"));
        }

        IQ reply = new IQ(iqe);

        // IQ reply = IQ.createResultIQ(packet);
        // System.out.println("XML " + reply.toXML());
        return reply;
    }

}
