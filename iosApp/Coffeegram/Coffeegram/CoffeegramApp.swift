import SwiftUI
import SwiftData

@main
struct CoffeegramApp: App {
    var sharedModelContainer: ModelContainer = {
        do {
            return try ModelContainer(for: CoffeeDrink.self, DailyConsumption.self)
        } catch {
            fatalError("Failed to create ModelContainer: \(error.localizedDescription)")
        }
    }()

    var body: some Scene {
        WindowGroup {
            CalendarView(viewModel: CalendarViewModel(modelContext: sharedModelContainer.mainContext))
        }
        .modelContainer(sharedModelContainer)
    }
}
