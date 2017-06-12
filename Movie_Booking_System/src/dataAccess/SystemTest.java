package dataAccess;

public class SystemTest {

	public static void main(String[] args) throws Exception {
		DBBuilder builder = new DBBuilder();
		/*try {
			builder.createDB();
		} catch (Exception e) {
			System.out.println(e);
		}*/
		Movie_DAO system = new Movie_DAO();
		
		//test get user age
		/*int temp = system.getUserAge(1);
		System.out.println(temp);*/
		
		// test get remain seats
		int seat = system.getRemainSeats(6, "13：50");
		System.out.println(seat);
		
	}
}
