import Foundation
import SwiftData

@Model
final class CoffeeDrink: Identifiable {
    let id = UUID()
    let name: String
    let icon: String // SF Symbol name
    var count: Int

    init(name: String, icon: String, count: Int) {
        self.name = name
        self.icon = icon
        self.count = count
    }
}
