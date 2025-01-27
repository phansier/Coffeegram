import Foundation
import SwiftData

class ListViewModel: ObservableObject {
    let defaultDrinks = [
        CoffeeDrink(name: "Espresso", icon: "cup.and.saucer.fill"),
        CoffeeDrink(name: "Cappuccino", icon: "cup.and.saucer"),
        CoffeeDrink(name: "Latte", icon: "mug.fill"),
        CoffeeDrink(name: "Americano", icon: "mug")
    ]
    
    @Published var drinks: [CoffeeDrink] = []
    private var modelContext: ModelContext

    init(modelContext: ModelContext) {
        self.modelContext = modelContext
        fetchDrinks()
    }

    private func fetchDrinks() {
        let descriptor = FetchDescriptor<CoffeeDrink>()
        drinks = (try? modelContext.fetch(descriptor)) ?? []

        // Initialize default drinks if empty
        if drinks.isEmpty {
            defaultDrinks.forEach { modelContext.insert($0) }
            drinks = defaultDrinks
        }
    }

    func getDrinkCount(drinkId: UUID, for date: Date) -> Int {
        guard let drink = drinks.first(where: { $0.id == drinkId }),
              let consumption = drink.dailyConsumption.first(where: {
                  Calendar.current.isDate($0.date, inSameDayAs: date)
              }) else {
            return 0
        }
        return consumption.count
    }

    func increment(drinkId: UUID, date: Date) {
        guard let drink = drinks.first(where: { $0.id == drinkId }) else { return }

        if let consumption = drink.dailyConsumption.first(where: {
            Calendar.current.isDate($0.date, inSameDayAs: date)
        }) {
            consumption.count += 1
        } else {
            let newConsumption = DailyConsumption(date: date, count: 1, drink: drink)
            drink.dailyConsumption.append(newConsumption)
            modelContext.insert(newConsumption)
        }

        //try? modelContext.save()
    }

    func decrement(drinkId: UUID, date: Date ) {
        guard let drink = drinks.first(where: { $0.id == drinkId }),
              let consumption = drink.dailyConsumption.first(where: {
                  Calendar.current.isDate($0.date, inSameDayAs: date)
              }),
              consumption.count > 0 else { return }

        consumption.count -= 1 // todo decrement does not work
        //try? modelContext.save()
    }
}
