package shopping;

import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;

/**
 * Тестирование класса {@link ShoppingServiceImpl}
 */
public class ShoppingServiceImplTest {

    /**
     * Мок объект класса {@link ProductDao}
     */
    private final ProductDao productDAOMock;

    /**
     * Тестируемый класс
     */
    private final ShoppingServiceImpl shoppingService;

    /**
     * Покупатель
     */
    private final Customer customer;

    /**
     * Корзина покупателя
     */
    private Cart cart;

    /**
     * Конструктор класса
     */
    public ShoppingServiceImplTest() {
        productDAOMock = Mockito.mock(ProductDao.class);
        shoppingService = new ShoppingServiceImpl(productDAOMock);
        customer = new Customer(1, "79876543210");
    }

    /**
     * Инициализация корзины покупателя перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        cart = new Cart(customer);
    }

    /**
     * В тестировании нет необходимости,
     * так как метод лишь возвращает результат метода из класса {@link ProductDao}
     */
    @Test
    void testGetAllProducts() {
    }

    /**
     * В тестировании нет необходимости,
     * так как метод лишь возвращает результат метода из класса {@link ProductDao}
     */
    @Test
    void testGetProductByName() {
    }

    /**
     * Тестирование метода {@link ShoppingServiceImpl#getCart(Customer)}
     * Проверяем, что возвращается корзина покупателя
     * Тест не проходит, так как метод каждый раз возвращает новый экземпляр корзины
     */
    @Test
    void testGetCart() {
        Assertions.assertEquals(cart, shoppingService.getCart(customer));
    }

    /**
     * Тестирование метода {@link ShoppingServiceImpl#buy(Cart)}
     * Проверяем, что возвращается true, корзина отчищается и у productDao вызывается метод save,
     * при условии, что корзина не пуста и в наличии есть нужное число товаров
     * Тест не проходит, так как корзина после покупки не отчищается
     */
    @Test
    void testBuyWithNotEmptyCart() throws BuyException {
        Product product1 = new Product("Продукт 1", 3);
        Product product2 = new Product("Продукт 2", 2);
        cart.add(product1, 2);
        cart.add(product2, 1);

        Assertions.assertTrue(shoppingService.buy(cart));
        Mockito.verify(productDAOMock).save(product1);
        Mockito.verify(productDAOMock).save(product2);
        Assertions.assertEquals(1, product1.getCount());
        Assertions.assertEquals(1, product2.getCount());

        Assertions.assertEquals(0, cart.getProducts().size());
    }

    /**
     * Тестирование метода {@link ShoppingServiceImpl#buy(Cart)}
     * Проверяем, что возвращается true и у productDao вызывается метод save,
     * при условии, что в корзине столько товаров, сколько в наличии
     * Тест не проходит, так как невозможно добавить в корзину столько товаров, сколько в наличии
     */
    @Test
    void testBuyWhenCountProductsInCartEqualsCountProductsInStock() throws BuyException {
        Product product1 = new Product("Продукт 1", 3);
        Product product2 = new Product("Продукт 2", 2);
        cart.add(product1, 2);
        cart.add(product2, 2);

        Assertions.assertTrue(shoppingService.buy(cart));
        Mockito.verify(productDAOMock).save(product1);
        Mockito.verify(productDAOMock).save(product2);
        Assertions.assertEquals(1, product1.getCount());
        Assertions.assertEquals(0, product2.getCount());

        Assertions.assertEquals(0, cart.getProducts().size());
    }

    /**
     * Тестирование метода {@link ShoppingServiceImpl#buy(Cart)}
     * Проверяем, что возвращается false и у productDao не вызывается метод save,
     * при условии, что корзина пуста
     */
    @Test
    void testBuyWithEmptyCart() throws BuyException {
        Assertions.assertFalse(shoppingService.buy(cart));
        Mockito.verifyNoInteractions(productDAOMock);
    }

    /**
     * Тестирование метода {@link ShoppingServiceImpl#buy(Cart)}
     * Проверяем, что при количестве товара в корзине больше, чем в наличии,
     * выбрасывается BuyException
     */
    @Test
    void testBuyWithProductCountInCartMoreThanAvailable() throws BuyException {
        Product product1 = new Product("Продукт 1", 3);
        Product product2 = new Product("Продукт 2", 3);
        cart.add(product1, 2);
        cart.add(product2, 2);
        product1.subtractCount(2);
        Exception exception = Assertions.assertThrows(BuyException.class,
                () -> shoppingService.buy(cart));
        Assertions.assertEquals("В наличии нет необходимого количества товара 'Продукт 1'",
                exception.getMessage());
    }

    /**
     * Тестирование метода {@link ShoppingServiceImpl#buy(Cart)}
     * Тест не проходит, так как в методе нет проверки на null
     */
    @Test
    void testBuyWithNullCart() throws BuyException {
        Assertions.assertFalse(shoppingService.buy(null));
    }
}
