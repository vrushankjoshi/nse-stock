package testing;

import com.test.DailyStock;
import com.test.Stock;

public class MainApp {

    public static void main(String args[]) {
        DailyStock d1 = new DailyStock();

        d1.setAge(1);
        d1.setName("Vrushank");

        Stock stck = d1;

        
    }
}
