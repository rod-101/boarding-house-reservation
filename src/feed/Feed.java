package feed;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import room.Room;
import database.DBConnection;
import account.LandlordAccount;
import account.StudentAccount; //for using the menu

public class Feed {
	ArrayList<Room> rooms = new ArrayList<>(); 
	static String currentPage = "Feed";
	
	public void getAvailableRooms() {
		try {
			Connection con = DBConnection.getConnection();
			
			String query = "SELECT "
					+ "rooms.room_id, "
					+ "rooms.owner, "
					+ "rooms.price, "
					+ "rooms.capacity, "
					+ "rooms.address, "
					+ "rooms.current_occupancy, "
					+ "rooms.max_occupancy, "
					+ "room_status.status_name, "
					+ "room_type.type_name "
					+ "FROM rooms "
					+ "INNER JOIN room_type ON rooms.type = room_type.type_id "
					+ "INNER JOIN room_status ON rooms.status = room_status.status_id "
					+ "WHERE rooms.status = 1";
			PreparedStatement stmt = con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				Room room = new Room();
				room.id = rs.getInt("room_id");
				room.owner = rs.getInt("owner");
				room.typeName = rs.getString("type_name");
				room.price = rs.getInt("price");
				room.capacity = rs.getString("capacity");
				if(room.typeName.equals("family") || room.typeName.equals("single")) {
					room.capacity = rs.getString("capacity");
				} else if(room.typeName.equals("bed-spacer")) {
					room.current = rs.getInt("current_occupancy");
					room.max = rs.getInt("max_occupancy");
					room.capacity = room.current + "/" + room.max;
				}
				room.address = rs.getString("address");
				room.statusName = rs.getString("status_name");
				rooms.add(room);
			}
			
			DBConnection.closeConnection(con);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void displayFeed() {
		System.out.println("\n------------ Rooms Feed ------------");
		System.out.println("Feed lists all rooms that are available for reservation.\n");
		
		for(Room room : rooms) {
			room.displayRoomDetails();
			System.out.println("");
		}
		
		StudentAccount.currentPage = currentPage;
		LandlordAccount.currentPage = currentPage;
	}
}
