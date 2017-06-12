package dataAccess;

public class ticketSystem {
	/**
	 * 這個function 是用來一般性訂票
	 * @param userID ：使用者ID
	 * @param movieID : 欲訂票之電影ID
	 * @param playTime : 欲訂之場次
	 * @param numTickets ：欲訂之張數
	 * @return String : 成功與否的message
	 */
	public String ticketBook(int userID, int movieID, String playTime, int numTickets) throws ticketSystemException{
		Movie_DAO tunnel = new Movie_DAO();
		
		// 獲取訂票者的年齡,欲訂場次之剩餘座位,電影資料
		int userAge = tunnel.getUserAge(userID);
		int remainSeats = tunnel.getRemainSeats(movieID, playTime);
		String[] movie_info = tunnel.getMovieInfo(movieID); // --> [名稱,分級,敘述,片長,分數,場次,廳]
		if(!validBook(userAge, movie_info[1]))
			throw new ticketSystemException("失敗，該電影分級為"+movie_info[1] + "，" + userAge + "歲無法購買");
		else if (remainSeats - numTickets < 0)
			throw new ticketSystemException("失敗，" + movieID  + "於" + playTime + "座位數量不夠");
		else {
			tunnel.bookTicket(movie_info[0],playTime,movie_info[6]);	
		}
		return null;		
	}
	/**
	 * 這個function 是用來取消訂票
	 * @param ticketID ： 訂票ID
	 * @return String : 成功與否之message
	 */
	public String cancelTicket(int ticketID) throws ticketSystemException{
		return null;
	}
	
	/**
	 * 這個function用來判斷使用者可不可以訂票
	 * @param userAge : 使用者年齡
	 * @param classification : 欲看電影之分級
	 * @return boolean : 表示可不可以定
	 */
	private boolean validBook(int userAge, String classification){
		// 可不可以買用boolean來表示
		boolean valid = false;
		switch(classification){
			case "普遍":
				valid = true;
				break;
			case "保護":
				valid = (userAge < 6) ? false : true;
				break;
			case "輔導":
				valid = (userAge < 12) ? false : true;
				break;
			case "限制":
				valid = (userAge < 18) ? false : true;
				break;
		}
		return valid;
	}
}
