package dataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.print.attribute.standard.RequestingUserName;

import com.mysql.fabric.xmlrpc.base.Array;

public class Movie_DAO {
	Connection connection = null;
	PreparedStatement qStmt  = null;
	PreparedStatement insertStmt = null;
	ResultSet result = null;
	// big_romm List, small_room List
	private List<String> big_rooms = Arrays.asList("武當","華山","少林");
	private List<String> small_rooms = Arrays.asList("峨嵋","崆峒");
	
	/**
	 * 這個function 用來建立連線
	 * @return Connection : 回傳連線
	 * @throws SQLException
	 */
 	private Connection getConnection() throws SQLException {
		return ConnectionFactory.getInstance().getConnection();
	}
	
	/**
	 * function 是用來獲取使用者年齡
	 * @param userID : 輸入使用者之ID
	 * @return int : 使用者之年齡
	 */
	public int getUserAge(int userID){
		try {
			connection = getConnection();
			qStmt = connection.prepareStatement("SELECT age FROM Users "
					+ "WHERE id = ?");
			qStmt.setInt(1, userID);
			result = qStmt.executeQuery();
			while(result.next())
				return result.getInt("age");	
		} catch (SQLException e) {
			System.out.println(e.getMessage() + "-> cannot get user age");
		}
		return 0;
	}
	
	public String[] getMovieInfo(int movieID){
		String[] info = new String[7];  //  -- > [名稱,分級,敘述,片長,分數,場次,廳]
		try {
			connection = getConnection();
			qStmt = connection.prepareStatement("SELECT * FROM Movie_info "
					+ "WHERE id = ?");
			qStmt.setInt(1, movieID);
			result = qStmt.executeQuery();
			while(result.next()){
				info[0] = result.getString("movie");
				info[1] = result.getString("classification");
				info[2] = result.getString("descri");
				info[3] = result.getString("infor");
				info[4] = result.getString("score");
				info[5] = result.getString("time");
				info[6] = result.getString("hall");
 			}
			return info;
		} catch (Exception e) {
			System.out.println(e.getMessage() + "cannot get Movie Info");
		}
		return null;
	}
	/**
	 * 這個function 用來獲取該電影場次剩餘的座位數
	 * @param movieName ： 輸入之電影名稱
	 * @param time : 輸入之場次
	 * @return int[] 個座位區剩餘座位
	 */
	public int getRemainSeats(int movieID, String time) {
		// 把輸入電影之場次存在list
		List<String> showTime = new ArrayList<String>();
		String hallName;
		int indexOfTime;
		
		// 全行轉半形
		time = DBBuilder.convertChar(time);
		
		try {
			connection = getConnection();
			qStmt = connection.prepareStatement("SELECT * FROM Movie_info  "
					+ "WHERE id = ?");
			qStmt.setInt(1, movieID);
			result = qStmt.executeQuery();
			while(result.next()){
				String[] temp_showTime = result.getString("time").split("、");
				for(String t : temp_showTime){
					showTime.add(t);
				}

				//取得上映的廳
				hallName = result.getString("hall");
				
				//取得他在場次陣列中的index --> SQL 中column 的 index是從1開始 !!
				indexOfTime = showTime.indexOf(time) + 1;
				
				// 從Seat_num table 中拿取剩餘的座位數
				qStmt = connection.prepareStatement("SELECT * FROM Seat_num "
						+ "WHERE hall = ?");
				qStmt.setString(1, hallName);
				result = qStmt.executeQuery();
				while(result.next()){
					String[] temp_Seats = result.getString(indexOfTime).split(",");
					// return [gray,blue,yellow,red] 座位陣列裡面的第一項（一般座位）
					return Integer.valueOf(temp_Seats[0]);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage() + "-> cannot getRemain Seats info!");
		}
		return 0;
	}
	
	public void bookTicket(String movie_name, String time,String hallName){
		try {
			connection = getConnection();
			insertStmt = connection.prepareStatement("INSERT INTO Movie_ticket (movie_name,time,hall,seat) "
					+ "VALUES (?,?,?,?)");
			insertStmt.setString(1, movie_name);
			insertStmt.setString(2, time);
			insertStmt.setString(3, hallName);
			
			// 這邊要指定座位
			String seatNum = null;
			
			insertStmt.setString(4, seatNum);
			insertStmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage() + "book ticket fail!");
		}
	}
	
	/**
	 * 這個function是用來隨機選位子
	 * @return String : 選擇之座位
	 */
	private String selectRandomSeat(Connection connection, String hallName){
		
		qStmt = connection.prepareStatement("SELECT ");
		return null;
	}
}
