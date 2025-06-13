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
            TabView {
                CalendarView(viewModel: CalendarViewModel(modelContext: sharedModelContainer.mainContext))
                    .tabItem {
                        Label("1", systemImage: "person.3")
                    }

                CalendarView(viewModel: CalendarViewModel(modelContext: sharedModelContainer.mainContext))
                    .tabItem {
                        Label("2", systemImage: "checkmark.circle")
                    }
            }

        }
        .modelContainer(sharedModelContainer)
    }
}
