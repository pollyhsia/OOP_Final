package dataAccess;

// import packages
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Driver;

public class ConnectionFactory {
	String driver = "com.mysql.jdbc.Driver";
	String username = "root";
	String password = "ab2289114";
	String dbUrl = "jdbc:mysql://localhost:3306/Movie_System?useUnicode=yes&characterEncoding=UTF-8";
	
	// 使用 Singleton Pattern Design 確保在執行時只有一個 ConnectionFactory 物件
	
	// 將 connectionFactory 設為 private static 物件
	private static ConnectionFactory connectionFactory = null;
	
	// ConnectionFactory 的 constructor
	private ConnectionFactory(){
		try {
			// 透過java.lang.Class類別的forName()來載入並向DriverManager註冊JDBC驅動程式
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 這個function 用來回傳 Connection 物件
	 * 錯誤的話throw SQLException
	 * @return Connection : 回傳的Connection物件
	 */
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(dbUrl,username,password);
	}
	
	/**
	 * 這個function 用來實作訪問 ConnectionFactory 的 instance 的方法
	 * 並保證只會有一個instance 被建立
	 * @return ConnectionFactory : ConnectionFactory 的 instance
	 */
	public static ConnectionFactory getInstance() {
		if(connectionFactory == null){
			
			// 使用synchronized 這個keyword來保證同時只有一個thread呼叫此method
			synchronized (Connection.class) {
				if(connectionFactory == null)
					connectionFactory = new ConnectionFactory();
			}
		}
		return connectionFactory;
	}
}
