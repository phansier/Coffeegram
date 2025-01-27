import Foundation
import SwiftData

@Model
class DailyConsumption {
    let id: UUID
    let date: Date
    var count: Int
    var drink: CoffeeDrink?

    init(date: Date, count: Int, drink: CoffeeDrink?) {
        self.id = UUID()
        self.date = date
        self.count = count
        self.drink = drink
    }
}
