package cn.lexinzhi.openfire.plugin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.muc.MUCRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

public class MUCDao {

    private static final Logger Log = LoggerFactory.getLogger(MUCDao.class);

    // 判断用户是否加入了房间，避免重复加入错误
    private static final String HAS_JOINED_ROOM = "SELECT roomID, jid FROM ofMucMember WHERE roomID=? AND jid=?";
    // 将用户加入群
    private static final String ADD_MEMBER = "INSERT INTO ofMucMember (roomID,jid,nickname) VALUES (?,?,?)";
    // 查询用户加入的群列表
    private static final String GET_MEMBER_MUCS = "SELECT ofMucRoom.serviceID, ofMucRoom.name, ofMucRoom.roomid, ofMucRoom.naturalName, ofMucRoom.description ,ofMucMember.nickname "
            + "FROM ofMucRoom JOIN ofMucMember ON ofMucRoom.roomID = ofMucMember.roomID AND ofMucMember.jid = ?";

    /**
     * 根据用户名查询出用户所在群组的信息
     * 
     * @blog http://blog.csdn.net/yangzl2008
     * @date 2013年11月27日 下午7:16:22
     * @version V1.0
     * @param jid
     *            用户的名jid名称
     * @return List 对应的群列表
     */
    public static List<Map<String, String>> getMUCInfo(String jid) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Map<String, String> map = null;
        try {
            connection = DbConnectionManager.getConnection();
            statement = connection.prepareStatement(GET_MEMBER_MUCS);
            statement.setString(1, jid);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                map = new HashMap<String, String>();
                map.put("serviceID", resultSet.getString(1));
                map.put("name", resultSet.getString(2));
                map.put("roomid", resultSet.getString(3));
                map.put("naturalName", resultSet.getString(4));
                map.put("description", resultSet.getString(5));
                map.put("nickname", resultSet.getString(6));
                list.add(map);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            DbConnectionManager.closeConnection(resultSet, statement, connection);
        }
        return list;
    }

    /**
     * 将用户加入群
     * 
     * @param localMUCRoom
     * @param bareJID
     * @param nickname
     */
    public static void saveMember(MUCRoom localMUCRoom, JID bareJID, String nickname) {
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(ADD_MEMBER);
            pstmt.setLong(1, localMUCRoom.getID());
            pstmt.setString(2, bareJID.toBareJID());
            pstmt.setString(3, nickname);
            pstmt.executeUpdate();
        } catch (SQLException sqle) {
            Log.error(sqle.getMessage(), sqle);
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
    }

    /**
     * 判断用户是否加入了群
     * 
     * @param roomJID
     * @param bareJID
     * @return
     */
    public static boolean hasJoinedRoom(MUCRoom localMUCRoom, JID bareJID) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(HAS_JOINED_ROOM);
            pstmt.setLong(1, localMUCRoom.getID());
            pstmt.setString(2, bareJID.toBareJID());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException sqle) {
            Log.error(sqle.getMessage(), sqle);
        } finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
        return false;
    }
}
