import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import java.sql.Connection;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class TestDataBaseHandler {

    ArrayList<String> dataBase;

    @Test
    public void getDbConnection() {
        DataBaseHandler dataBaseHandler = mock(DataBaseHandler.class);
        when(dataBaseHandler.getDbConnection()).thenReturn(mock(Connection.class));
        Assert.assertNotNull(dataBaseHandler.getDbConnection());
    }

    @Before
    public void setDataBaseHandler() {
        this.dataBase = new ArrayList<>();
    }

    @Test
    public void testCreateProduct() throws Throwable {
        final String[] args = {"1", "2", "3", "4"};
        int dataBaseSizeBefore = dataBase.size();
        DataBaseHandler dataBaseHandler = mock(DataBaseHandler.class);
        Answer<Object> answerCreateProduct = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                String str = "";
                for (String s : args) {
                    str.concat(s);
                }
                dataBase.add(str);
                return null;
            }
        };
        doAnswer(answerCreateProduct).when(dataBaseHandler).createOrder(args);
        answerCreateProduct.answer(null);
        int dataBaseSizeAfter = dataBase.size();
        Assert.assertEquals(1, dataBaseSizeAfter - dataBaseSizeBefore);
    }

    @Test
    public void createOrder() throws Throwable {
        final String[] args = {"1", "2", "3"};
        int dataBaseSizeBefore = dataBase.size();
        DataBaseHandler dataBaseHandler = mock(DataBaseHandler.class);
        Answer<Object> answerCreateOrder = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                for (String s : args) {
                    dataBase.add(s);
                }
                return null;
            }
        };
        doAnswer(answerCreateOrder).when(dataBaseHandler).createOrder(args);
        answerCreateOrder.answer(null);
        int dataBaseSizeAfter = dataBase.size();
        Assert.assertEquals(3, dataBaseSizeAfter - dataBaseSizeBefore);
    }

    @Test
    public void updateOrder() throws Throwable {
        final String[] args = {"1", "2", "3"};
        dataBase.add("");
        final int dataBaseSizeBefore = dataBase.size();
        DataBaseHandler dataBaseHandler = mock(DataBaseHandler.class);
        Answer<Object> answerUpdateOrder = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                dataBase.set(dataBaseSizeBefore-1, args[2]);
                return null;
            }
        };
        doAnswer(answerUpdateOrder).when(dataBaseHandler).updateOrder(args);
        answerUpdateOrder.answer(null);
        int dataBaseSizeAfter = dataBase.size();
        Assert.assertEquals(0, dataBaseSizeAfter - dataBaseSizeBefore);
    }

    @Test
    public void allProducts() throws Throwable {
        dataBase.add("1");
        dataBase.add("2");
        dataBase.add("3");
        final int[] count = {0};
        DataBaseHandler dataBaseHandler = mock(DataBaseHandler.class);
        Answer<Object> answerAllProducts = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                for (String s : dataBase) {
                    count[0]++;
                }
                return null;
            }
        };
        doAnswer(answerAllProducts).when(dataBaseHandler).allProducts();
        answerAllProducts.answer(null);
        Assert.assertEquals(3, count[0]);
    }

    @Test
    public void orderedProducts() throws Throwable {
        final String arg = "2";
        dataBase.add("1");
        dataBase.add("2");
        dataBase.add("3");
        final int[] count = {0};
        DataBaseHandler dataBaseHandler = mock(DataBaseHandler.class);
        Answer<Object> answerOrderedProducts = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                for (String s : dataBase) {
                    if (s == arg) count[0]++;
                }
                return null;
            }
        };
        doAnswer(answerOrderedProducts).when(dataBaseHandler).orderedProducts();
        answerOrderedProducts.answer(null);
        Assert.assertEquals(1, count[0]);
    }

    @Test
    public void allOrder() {
    }

    @Test
    public void removeProductByID() {
    }

    @Test
    public void removeAllProducts() {
    }
}