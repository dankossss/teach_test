import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException{
        DataBaseHandler dbHandler = new DataBaseHandler();
        String command;
        String description = "   You can provide such operations:\n" +
                " Create product -name(String) -price(int) -status(int[1-3])  .. add product to DataBase\n" +
                " Create order -product_id(int) -product_id(int) -...  ......... creates order with a list of the products specified by id\n" +
                " Update order -order_id(int) -product-id(int) -quantity(int) .. changes quantity product (product_id) in a order (order_id)\n" +
                " All products  ................................................ shows list of all products\n" +
                " Ordered products  ............................................ shows list all products which have been ordered at least once\n" +
                " All orders  .................................................. shows list all orders\n" +
                " Remove product -product_id(int)  ............................. removes product(product_id) from DataBase)\n" +
                " Remove all products -password(String)  ....................... removes all products only if you enter a password\n" +
                " Show menu  ................................................... shows all possible ways how to interact with application\n" +
                " Exit  ........................................................ finishes the application";
        System.out.println(description);
        do { command = new BufferedReader(new InputStreamReader(System.in)).readLine();
            String[] command_args = command.split("-");
            switch (command_args[0].toLowerCase().trim()) {
                case "create product":
                    dbHandler.createProduct(command_args);
                    break;
                case "create order":
                    dbHandler.createOrder(command_args);
                    break;
                case "update order":
                    dbHandler.updateOrder(command_args);
                    break;
                case "all products":
                    dbHandler.allProducts();
                    break;
                case "ordered products":
                    dbHandler.orderedProducts();
                    break;
                case "all orders":
                    dbHandler.allOrder();
                    break;
                case "remove product":
                    dbHandler.removeProductByID(command_args);
                    break;
                case "remove all products":
                    dbHandler.removeAllProducts(command_args);
                    break;
                case "show menu":
                    System.out.println(description);
                    break;
                case "exit":
                    break;
                default:
                    System.out.println(" Unknown command");
            }
        } while (!command.equals("exit"));
    }

}
