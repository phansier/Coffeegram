import Foundation
import SwiftData

@Model
class CoffeeDrink {
    let id: UUID
    let name: String
    let icon: String
    @Relationship(deleteRule: .cascade) var dailyConsumption: [DailyConsumption]

    init(name: String, icon: String) {
        self.id = UUID()
        self.name = name
        self.icon = icon
        self.dailyConsumption = []
    }
}
