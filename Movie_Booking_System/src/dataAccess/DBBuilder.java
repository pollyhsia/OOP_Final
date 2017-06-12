package dataAccess;


import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DBBuilder {
	// 設定DB變數初始值
	Connection connection = null;
	PreparedStatement createTable = null;
	PreparedStatement insertStmt = null;
	
	// json 檔路徑
	private String user_json = "/Users/Brian/Desktop/Java/jsonTest/json/user.json";
	private String movie_json = "/Users/Brian/Desktop/Java/jsonTest/json/movie_info.json";
	private String big_room = "/Users/Brian/Desktop/Java/jsonTest/json/big_room.json";
	private String small_room = "/Users/Brian/Desktop/Java/jsonTest/json/small_room.json";

	/**
	 * 這個function會建立與DB的連線
	 * return Connection : 與DB的連線
	 * throws SQLException
	 */
	private Connection getConnection() throws SQLException {
		return ConnectionFactory.getInstance().getConnection();
	}
	
	/**
	 * 這個function是用來parse JSON
	 * @param filePath : input 檔案路徑
	 * @return JSONArray : parse 完的檔案
	 */
	private JSONArray parseJSON(String filePath) {
		try {
			// read JSON file
			FileReader reader = new FileReader(filePath);
			
			// parse file into JSONArray
			JSONParser parser = new JSONParser();
			return (JSONArray) parser.parse(reader);
			
		} catch (Exception e) {
			System.out.println(e + " -> parse JSON fail!");
			return null;
		}
	}
	
	/**
	 * 這個function 會建立 Users 的 table
	 */
	private void createUserTable() {
		try {
			// SQL code to create Users table
			connection = getConnection();
			createTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Users("
					+ "id int NOT NULL AUTO_INCREMENT,"
					+ "name varchar(45),"
					+ "age int NOT NULL,"
					+ "PRIMARY KEY(id))");
			createTable.executeUpdate();
			
			// insert users info into table
			String name;
			long age;
			insertStmt = connection.prepareStatement("INSERT INTO Users(name,age)"
					+ "VALUES (?,?)");
			JSONArray userList = parseJSON(user_json);
			for(Object user : userList){
				JSONObject currentUser = (JSONObject) user;
				name = (String) currentUser.get("name");
				age = (long) currentUser.get("age");
				
				// set placeholder in SQL code
				insertStmt.setString(1, name);
				insertStmt.setLong(2, age);
				insertStmt.executeUpdate();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * 這個 function 會建立 Movie_info 的 table
	 */	
	private void createMovieTable() {
		try {
			// SQL code to create Movie_info table
			connection = getConnection();
			createTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Movie_Info("
					+ "id int NOT NULL AUTO_INCREMENT,"
					+ "movie varchar(100),"
					+ "classification varchar(2),"
					+ "descri varchar(255),"
					+ "infor varchar(10),"
					+ "score Double,"
					+ "time varchar(255),"
					+ "hall varchar(2),"
					+ "PRIMARY KEY(id))");
			createTable.executeUpdate();
			
			// insert movie_info into table
			String movieName;
			String classification;
			String description;
			String info;
			double score;
			String time;
			String hall;
			insertStmt = connection.prepareStatement("INSERT INTO Movie_Info(movie,classification,descri,infor,score,time,hall)"
					+ "VALUES (?,?,?,?,?,?,?)");
			JSONArray movieList = parseJSON(movie_json);
			for(Object movie : movieList){
				JSONObject currentMovie = (JSONObject) movie;
				// method trim() is used to remove whitespace at the beginning and end of string
				movieName = ((String) currentMovie.get("movie")).trim();
				classification = (String) currentMovie.get("classification");
				description = (String) currentMovie.get("descri");
				info = convertChar((String) currentMovie.get("infor"));
				
				// remove the part of score after "/" character
				String tmpScore = ((String) currentMovie.get("score")).trim();
				int indexOfEnd = tmpScore.indexOf("/");
				tmpScore = tmpScore.substring(0, indexOfEnd);
				score = Double.parseDouble(tmpScore);
				
				time = convertChar(((String) currentMovie.get("time")).trim());
				hall = ((String) currentMovie.get("hall")).trim();
				
				// insert into DB
				insertStmt.setString(1, movieName);
				insertStmt.setString(2, classification);
				insertStmt.setString(3, description);
				insertStmt.setString(4, info);
				insertStmt.setDouble(5, score);
				insertStmt.setString(6, time);
				insertStmt.setString(7, hall);
				insertStmt.executeUpdate();
			}
		} catch (Exception e) {
			System.out.println(e + " -> cannot create Movie_info table");
		}
	}
	
	/**
	 * 這個function會建立 Big_room 的 table
	 */
	private void createBigRoomTable() {
		try {
			// SQL code to create big_room table
			connection = getConnection();
			createTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Big_room("
					+ "id varchar(24),"
					+ "row varchar(1),"
					+ "seatNum int NOT NULL,"
					+ "region varchar(10),"
					+ "PRIMARY KEY(id))");
			createTable.executeUpdate();
			
			// insert big_room seat info into table
			String id;
			String row;
			long seatNum;
			String occupied_hall1;
			String occupied_hall2;
			String occupied_hall3;
			String region;
			int movie_num_hall1 = 7;
			int movie_num_hall2 = 6;
			int movie_num_hall3 = 4;
			JSONArray seatList = parseJSON(big_room);
			insertStmt = connection.prepareStatement("INSERT INTO Big_room(id,row,seatNum,region)"
					+ "VALUES (?,?,?,?)");
			for (Object seat : seatList){
				JSONObject currentSeat = (JSONObject) seat;
				id = (String) currentSeat.get("id");
				row = (String) currentSeat.get("row");
				seatNum = (long) currentSeat.get("seatNum");
				// initialize occupied strings
				if(currentSeat.get("occupied") != null){
					region = (String) currentSeat.get("region");
				} else{
					region = "blank";
				}
				// insert into DB
				insertStmt.setString(1, id);
				insertStmt.setString(2, row);
				insertStmt.setLong(3, seatNum);
				insertStmt.setString(4, region);
				insertStmt.executeUpdate();
			}

		} catch (Exception e) {
			System.out.println(e + " -> cannot create Big_room table");
		}
	}

	/**
	 * 這個 function 會建立 Small_room 的 table
	 * param seatList : 傳入 small_room.json 的座位表
	 */
	private void createSmallRoomTable() {
		try {
			// SQL code to create small_room table
			connection = getConnection();
			createTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Small_room("
					+ "id varchar(24),"
					+ "row varchar(1),"
					+ "seatNum int NOT NULL,"
					+ "PRIMARY KEY(id))");
			
			createTable.executeUpdate();
			
			// insert small_room info to table
			String id;
			String row;
			long seatNum;
			String occupied_hall1;
			String occupied_hall2;
			int movie_num = 5;
			JSONArray seatList = parseJSON(small_room);
			insertStmt = connection.prepareStatement("INSERT INTO Small_room(id,row,seatNum)"
					+ "VALUES (?,?,?)");
			for (Object seat : seatList){
				JSONObject currentSeat = (JSONObject) seat;
				id = (String) currentSeat.get("id");
				row = (String) currentSeat.get("row");
				seatNum = (long) currentSeat.get("seatNum");
				
				// insert into DB
				insertStmt.setString(1, id);
				insertStmt.setString(2, row);
				insertStmt.setLong(3, seatNum);
				insertStmt.executeUpdate();
			}			
		} catch (Exception e) {
			System.out.println(e +" -> cannot create Small_room table");
		}
	}
	
	/**
	 *  這個 function 會建立訂票成功之電影票的table
	 */
	private void createTicketTable() {
		try {
			// SQL code to create Movie_tickets table
			connection = getConnection();
			createTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Movie_ticket("
					+ "id int NOT NULL AUTO_INCREMENT,"
					+ "movie_name varchar(255),"
					+ "time varchar(10),"
					+ "hall varchar(2),"
					+ "seat varchar(3),"
					+ "PRIMARY KEY(id))");
			createTable.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e + " -> cannot create Movie_tickets table");
		}
	}
	/**
	 * 這個funciton 是用來建立一個table來存每個廳
	 * 在放映時刻有多少座位
	 */
	private void createSeatNumTable(){
		try {
			// SQL code to create seat Num table
			connection = getConnection();
			createTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Seat_num("
					+ "hall varchar(2),"
					+ "time_1 varchar(20) ,"    // 這邊存的是座位區[gray,blue,yellow,red] 的數量
					+ "time_2 varchar(20) ,"
					+ "time_3 varchar(20) ,"
					+ "time_4 varchar(20) ,"
					+ "time_5 varchar(20) ,"
					+ "time_6 varchar(20) ,"
					+ "time_7 varchar(20) ,"
					+ "PRIMARY KEY (hall))");
			createTable.executeUpdate();
			
			// initialize seat_num table
			insertStmt = connection.prepareStatement("INSERT INTO Seat_num(hall,time_1,time_2,time_3,time_4,time_5,time_6,time_7)"
					+ "VALUES (?,?,?,?,?,?,?,?)");
			int gray = 277;
			int blue = 44;
			int yellow = 62;
			int red = 24;
			String numConcat = "" + gray + "," + blue + "," + yellow + "," + red;
			String smallRoomSeats = "144";
 			String[] hallName = {"武當","少林","華山","峨眉","崆峒"};
			for(int i = 0; i < hallName.length; i++){
				insertStmt.setString(1, hallName[i]);
				if(!(hallName[i].equals("峨眉") || hallName[i].equals("崆峒"))){
					insertStmt.setString(2, numConcat);
					insertStmt.setString(3, numConcat);
					insertStmt.setString(4, numConcat);
					insertStmt.setString(5, numConcat);
					// 根據特定情況加入座位數量
					if(hallName[i].equals("華山")){
						insertStmt.setString(6, null);
					}
					else{
						insertStmt.setString(6, numConcat);
					}
			
					if(hallName[i].equals("武當") || hallName[i].equals("少林")){
						insertStmt.setString(7,numConcat);
					} else{
						insertStmt.setString(7,null);
					}
					
					if(hallName[i].equals("武當")){
						insertStmt.setString(8,numConcat);
					} else{
						insertStmt.setString(8,null);
					}
				} else{
					insertStmt.setString(2, smallRoomSeats);
					insertStmt.setString(3, smallRoomSeats);
					insertStmt.setString(4, smallRoomSeats);
					insertStmt.setString(5, smallRoomSeats);
					insertStmt.setString(6, smallRoomSeats);
					insertStmt.setString(7,null);
					insertStmt.setString(8,null);
				}
				insertStmt.executeUpdate();
			}
		} catch (Exception e) {
			System.out.println(e + " - > cannot create seat_num table");
		}
	}
	
	// function that initialize occupied hall array
	private String initializeOccupiedHall(int movie_nums,String state) throws Exception{
		String[] temp = new String[movie_nums];
		
		// initialize occupied_hall,seperate with ","
		for (int i = 0; i < temp.length ; i++)
			temp[i] = state;
		return String.join(",",temp);
	}
	
	/**
	 * 這個function 是用來把全形字轉成半形
	 * @param str : 輸入之字串
	 * @return String : convert 完成的字串
	 */
	public static String convertChar(String str){
		System.out.println("全形 : " + str);
		for(char c : str.toCharArray()){
			str = str.replaceAll(" ", " ");
			if((int) c >= 65281 && c <= 65374)
				str = str.replace(c, (char) (((int) c) - 65248));
		}
		System.out.println("半形 : " + str);
		return str;
	}
	
	/** 
	 *  這邊設一個public 的 method 來建立DB
	 */
	public void createDB() {
		//createUserTable();
		//createMovieTable();
		//createBigRoomTable();
		//createSmallRoomTable();
		createTicketTable();
		//createSeatNumTable();
	}
}
