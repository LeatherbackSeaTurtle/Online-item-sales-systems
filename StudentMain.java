import operations.*;
import student.sp130541_ArticleOperation;
import student.sp130541_BuyerOperations;
import student.sp130541_CityOperations;
import student.sp130541_GeneralOperations;
import student.sp130541_OrderOperation;
import student.sp130541_ShopOperations;
import student.sp130541_TransactionOperations;

import org.junit.Test;
import tests.TestHandler;
import tests.TestRunner;

import java.util.Calendar;

public class StudentMain {

    public static void main(String[] args) {

        ArticleOperations articleOperations = new sp130541_ArticleOperation(); // Change this for your implementation (points will be negative if interfaces are not implemented).
        BuyerOperations buyerOperations = new sp130541_BuyerOperations();
        CityOperations cityOperations = new sp130541_CityOperations();
        GeneralOperations generalOperations = new sp130541_GeneralOperations();
        OrderOperations orderOperations = new sp130541_OrderOperation();
        ShopOperations shopOperations = new sp130541_ShopOperations();
        TransactionOperations transactionOperations = new sp130541_TransactionOperations();
//
//        Calendar c = Calendar.getInstance();
//        c.clear();
//        c.set(2010, Calendar.JANUARY, 01);
//
//
//        Calendar c2 = Calendar.getInstance();
//        c2.clear();
//        c2.set(2010, Calendar.JANUARY, 01);
//
//        if(c.equals(c2)) System.out.println("jednako");
//        else System.out.println("nije jednako");

        TestHandler.createInstance(
                articleOperations,
                buyerOperations,
                cityOperations,
                generalOperations,
                orderOperations,
                shopOperations,
                transactionOperations
        );

        TestRunner.runTests();
    }
}
