import Foundation

class ListViewModel: ObservableObject {
    @Published var drinks: [CoffeeDrink] = [
        CoffeeDrink(name: "Espresso", icon: "cup.and.saucer.fill", count: 0),
        CoffeeDrink(name: "Cappuccino", icon: "cup.and.saucer", count: 0),
        CoffeeDrink(name: "Latte", icon: "mug.fill", count: 0),
        CoffeeDrink(name: "Americano", icon: "mug", count: 0)
    ]

    func increment(drinkId: UUID) {
        if let index = drinks.firstIndex(where: { $0.id == drinkId }) {
            drinks[index].count += 1
        }
    }

    func decrement(drinkId: UUID) {
        if let index = drinks.firstIndex(where: { $0.id == drinkId }),
           drinks[index].count > 0 {
            drinks[index].count -= 1
        }
    }
}
