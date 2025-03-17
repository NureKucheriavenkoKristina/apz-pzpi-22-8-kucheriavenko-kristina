public class DecoratorPattern {

    // 1. Базовий інтерфейс Component
    public interface Coffee {
        double cost();  // Метод для отримання вартості
    }

    // 2. ConcreteComponent - реалізація основного об'єкта
    public static class SimpleCoffee implements Coffee {
        @Override
        public double cost() {
            return 5.0;  // Вартість звичайної кави
        }
    }

    // 3. Абстрактний клас Decorator
    public static abstract class CoffeeDecorator implements Coffee {
        protected Coffee decoratedCoffee;

        public CoffeeDecorator(Coffee coffee) {
            this.decoratedCoffee = coffee;
        }

        @Override
        public double cost() {
            return decoratedCoffee.cost();  // Виклик методу в базовому класі
        }
    }

    // 4. ConcreteDecorator для молока
    public static class MilkDecorator extends CoffeeDecorator {
        public MilkDecorator(Coffee coffee) {
            super(coffee);
        }

        @Override
        public double cost() {
            return decoratedCoffee.cost() + 1.5;  // Додаємо вартість молока
        }
    }

    // 5. ConcreteDecorator для цукру
    public static class SugarDecorator extends CoffeeDecorator {
        public SugarDecorator(Coffee coffee) {
            super(coffee);
        }

        @Override
        public double cost() {
            return decoratedCoffee.cost() + 0.5;  // Додаємо вартість цукру
        }
    }

    // 6. Тестування патерну
    public static void main(String[] args) {
        // Створення простого об'єкта кави
        Coffee coffee = new SimpleCoffee();
        System.out.println("Cost of simple coffee: " + coffee.cost());

        // Додавання молока
        coffee = new MilkDecorator(coffee);
        System.out.println("Cost of coffee with milk: " + coffee.cost());

        // Додавання цукру
        coffee = new SugarDecorator(coffee);
        System.out.println("Cost of coffee with milk and sugar: " + coffee.cost());
    }
}
