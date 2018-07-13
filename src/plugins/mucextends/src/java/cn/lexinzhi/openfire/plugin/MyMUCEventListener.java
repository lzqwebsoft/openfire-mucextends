package cn.lexinzhi.openfire.plugin;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.muc.MUCEventListener;
import org.jivesoftware.openfire.muc.MUCRoom;
import org.jivesoftware.openfire.muc.MultiUserChatService;
import org.jivesoftware.openfire.muc.NotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

import cn.lexinzhi.openfire.plugin.dao.MUCDao;

public class MyMUCEventListener implements MUCEventListener {

    private static final Logger Log = LoggerFactory.getLogger(MUCDao.class);

    @Override
    public void roomCreated(JID roomJID) {

    }

    @Override
    public void roomDestroyed(JID roomJID) {

    }

    @Override
    public void occupantJoined(JID roomJID, JID userJID, String nickname) {
        // 只在用户加入群的时候进行操作，将用户id写入对应的数据库
        XMPPServer server = XMPPServer.getInstance();
        MultiUserChatService mucservice = server.getMultiUserChatManager().getMultiUserChatService(roomJID);
        if (mucservice != null) {
            String roomName = roomJID.getNode();
            try {
                MUCRoom room = mucservice.getChatRoom(roomName, userJID);
                // 当用户加入房间时，将其数据写入数据库.
                if (room.isPersistent() && !MUCDao.hasJoinedRoom(room, userJID)) {
                    MUCDao.saveMember(room, userJID, nickname);
                }
            } catch (NotAllowedException e) {
                Log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void occupantLeft(JID roomJID, JID user) {

    }

    @Override
    public void nicknameChanged(JID roomJID, JID user, String oldNickname, String newNickname) {

    }

    @Override
    public void messageReceived(JID roomJID, JID user, String nickname, Message message) {

    }

    @Override
    public void privateMessageRecieved(JID toJID, JID fromJID, Message message) {

    }

    @Override
    public void roomSubjectChanged(JID roomJID, JID user, String newSubject) {

    }

}
